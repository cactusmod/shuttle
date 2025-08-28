/*******************************************************************************
 * Copyright (c) 2025 Cactus Team.
 * This code is part of the Cactus-Client distribution. All Rights reserved.
 ******************************************************************************/

package com.dwarslooper.cactus.shuttle.addon;

import net.fabricmc.loader.api.metadata.ModMetadata;

import java.util.List;

public record ShuttleAddon(String id, String name, List<String> authors, ModMetadata metadata, IShuttleAddonLifecycle lifecycle) {

}
