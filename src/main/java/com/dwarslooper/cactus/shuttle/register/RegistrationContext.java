/*******************************************************************************
 * Copyright (c) 2025 Cactus Team.
 * This code is part of the Cactus-Client distribution. All Rights reserved.
 ******************************************************************************/

package com.dwarslooper.cactus.shuttle.register;

import java.util.HashMap;
import java.util.Map;

public class RegistrationContext {
    private final Map<Class<?>, Object> services = new HashMap<>();

    protected <T> void bind(Class<T> type, T instance) {
        services.put(type, instance);
    }

    public <T> T require(Class<T> type) {
        T val = type.cast(services.get(type));

        if (val == null) {
            throw new IllegalStateException("Missing service '%s'".formatted(type.getCanonicalName()));
        }

        return val;
    }
}
