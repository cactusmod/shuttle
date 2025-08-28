package com.dwarslooper.cactus.shuttle.handler.impl;

import com.dwarslooper.cactus.shuttle.Shuttle;
import com.dwarslooper.cactus.shuttle.addon.IShuttleAddonLifecycle;
import com.dwarslooper.cactus.shuttle.addon.ShuttleAddon;
import com.dwarslooper.cactus.shuttle.handler.IAddonHandler;
import com.dwarslooper.cactus.shuttle.register.RegistryBus;
import com.dwarslooper.cactus.shuttle.util.DependencySorter;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AddonHandlerImpl implements IAddonHandler {

	private final String entrypointName;

	private final RegistryBus registryBus = new RegistryBus();
	private final List<ShuttleAddon> addons = new ArrayList<>();

	private final List<ModMetadata> loadFailed = new ArrayList<>();

	public AddonHandlerImpl(String entrypointName) {
		this.entrypointName = entrypointName;
	}

	@Override
	public void discoverAndRegister() {
		for (EntrypointContainer<IShuttleAddonLifecycle> entrypoint : FabricLoader.getInstance().getEntrypointContainers(this.entrypointName, IShuttleAddonLifecycle.class)) {
			ModMetadata metadata = entrypoint.getProvider().getMetadata();
			String definition = entrypoint.getDefinition();

			try {
				IShuttleAddonLifecycle lifecycle = entrypoint.getEntrypoint();
				addons.add(new ShuttleAddon(metadata.getId(), metadata.getName(), metadata.getAuthors().stream().map(Person::getName).toList(), metadata, lifecycle));
			} catch (Throwable t) {
				Shuttle.getLogger().error("Exception thrown during initialization of addon '{}' ({}).", metadata.getName(), metadata.getId(), t);

				if(t instanceof NoClassDefFoundError e) {
					try {
						Class<?> aClass = Class.forName(definition);
						if (!aClass.isInstance(IShuttleAddonLifecycle.class)) {
							Shuttle.getLogger().error("The class specified as the entrypoint for shuttle to use does not implement {} which is required for us to load it.", IShuttleAddonLifecycle.class.getSimpleName());
						}
					} catch (ClassNotFoundException ex) {
						Shuttle.getLogger().error("The class specified as the entrypoint for shuttle to use does not exist.");
					}
				}

				loadFailed.add(metadata);
			}
		}
	}

	@Override
	public void initializeAll() {
		List<ShuttleAddon> sortedAddons;

		try {
			sortedAddons = DependencySorter.sortAddonsByDependency(this);
		} catch (IllegalStateException e) {
			Shuttle.getLogger().error("Failed to sort addons by dependencies", e);
			return;
		}

		for (ShuttleAddon addon : sortedAddons) {
			try {
				registryBus.withRegistrar(addon, addon.lifecycle()::onInitialize);
			} catch (Exception e) {
				Shuttle.getLogger().error("Failed to initialize addon '{}'", addon.name(), e);
			}
		}
	}

	@Override
	public void callEach(Consumer<IShuttleAddonLifecycle> consumer) {
		addons.forEach(addon -> consumer.accept(addon.lifecycle()));
	}

	@Override
	public RegistryBus getRegistryBus() {
		return registryBus;
	}

	@Override
	public List<ShuttleAddon> getAddons() {
		return addons;
	}

	public List<ModMetadata> getLoadFailed() {
		return loadFailed;
	}
}
