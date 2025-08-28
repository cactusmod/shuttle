/*******************************************************************************
 * Copyright (c) 2025 Cactus Team.
 * This code is part of the Cactus-Client distribution. All Rights reserved.
 ******************************************************************************/

package com.dwarslooper.cactus.shuttle.addon;

import com.dwarslooper.cactus.shuttle.register.RegistryBus;

public interface IShuttleAddonLifecycle {
	void onInitialize(RegistryBus registryBus);
	default void onLoadComplete() {}
	default void onShutdown() {}
}
