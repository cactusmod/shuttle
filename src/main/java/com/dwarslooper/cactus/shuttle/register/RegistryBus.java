/*******************************************************************************
 * Copyright (c) 2025 Cactus Team.
 * This code is part of the Cactus-Client distribution. All Rights reserved.
 ******************************************************************************/

package com.dwarslooper.cactus.shuttle.register;

import com.dwarslooper.cactus.shuttle.Shuttle;
import com.dwarslooper.cactus.shuttle.addon.ShuttleAddon;
import com.dwarslooper.cactus.shuttle.register.registries.IContentRegistry;
import com.dwarslooper.cactus.shuttle.register.registries.impl.SimpleRegistry;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class RegistryBus {
    private final RegistrationContext context = new RegistrationContext();
    private final Set<Class<?>> completed = new HashSet<>();

    private final Map<Class<?>, IContentRegistry<?>> registries = new HashMap<>();
    private final Map<Class<?>, List<PendingRegistration>> pending = new LinkedHashMap<>();
    private final Map<Object, ShuttleAddon> owners = new HashMap<>();

    private ShuttleAddon currentRegistrar;

    @ApiStatus.Internal
    public void withRegistrar(ShuttleAddon registrar, Consumer<RegistryBus> wrapped) {
        this.currentRegistrar = registrar;
        wrapped.accept(this);
        this.currentRegistrar = null;
    }

    public <T> void provideRegistry(Class<T> type, IContentRegistry<T> registry) {
        registries.put(type, registry);
    }

    public <T> void provideService(Class<T> serviceClass, T instance) {
        context.bind(serviceClass, instance);
    }

    public <T> void register(Class<T> type, Function<RegistrationContext, T> factory) {
        ensureNonComplete(type);
        add(type, factory);
    }

    public <T> void register(Class<T> type, BiConsumer<List<T>, RegistrationContext> consumer) {
        ensureNonComplete(type);
        add(type, ctx -> {
            List<T> list = new ArrayList<>();
            consumer.accept(list, ctx);
            return list;
        });
    }

    private void add(Class<?> type, Function<RegistrationContext, ?> factory) {
        pending.computeIfAbsent(type, k -> new ArrayList<>()).add(new PendingRegistration(currentRegistrar, factory));
    }

    public <T> void completeAndTake(Class<T> type, BiConsumer<T, ShuttleAddon> callback) {
        complete(type);
        take(type, callback);
    }

    public <T> void take(Class<T> type, BiConsumer<T, ShuttleAddon> callback) {
        request(type).forEach(t -> callback.accept(t, getOwner(t)));
    }

    public <T> void completeAndTake(Class<T> type, Consumer<T> callback) {
        complete(type);
        take(type, callback);
    }

    public <T> void take(Class<T> type, Consumer<T> callback) {
        request(type).forEach(callback);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> request(Class<T> type) {
        IContentRegistry<?> registry = registries.get(type);

        if(registry == null) {
            if(pending.containsKey(type)) {
                Shuttle.getLogger().warn("Requested type '{}' was not found in registries, however this type is pending. Was this type completed correctly?", type.getCanonicalName());
            }

            return Collections.emptyList();
        }

        return (List<T>) registry.getAll();
    }

    @SuppressWarnings("unchecked")
    public <T> void complete(Class<T> type) {
        ensureNonComplete(type);

        List<PendingRegistration> registrations = pending.get(type);

        if(registrations != null) {
            IContentRegistry<Object> registry = (IContentRegistry<Object>) registries.computeIfAbsent(type, k -> new SimpleRegistry());
            for (PendingRegistration reg : registrations) {
                Object obj = reg.factory().apply(context);
                owners.put(obj, reg.owner());

                if(obj instanceof Collection<?> collection) {
                    collection.forEach(registry::register);
                } else {
                    registry.register(obj);
                }
            }
        }

        completed.add(type);
        pending.remove(type);
    }

    public ShuttleAddon getOwner(Object o) {
        return owners.get(o);
    }

    private void ensureNonComplete(Class<?> type) {
        if(completed.contains(type)) {
            throw new IllegalStateException("This type is already completed. An instance of RegistryBus should not be retained, use outside of CactusAddon#onInitialize is unsafe!");
        }
    }
}
