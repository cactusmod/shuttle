package com.dwarslooper.cactus.shuttle.handler;

import com.dwarslooper.cactus.shuttle.addon.IShuttleAddonLifecycle;
import com.dwarslooper.cactus.shuttle.addon.ShuttleAddon;
import com.dwarslooper.cactus.shuttle.register.RegistryBus;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

public interface IAddonHandler {

	void discoverAndRegister();
	void initializeAll();
	void callEach(Consumer<IShuttleAddonLifecycle> consumer);

	RegistryBus getRegistryBus();
	Collection<ShuttleAddon> getAddons();

	default Collection<ModMetadata> getLoadFailed() {
		return Collections.emptyList();
	}

	default void createDefaultProviders() {

	}

}
