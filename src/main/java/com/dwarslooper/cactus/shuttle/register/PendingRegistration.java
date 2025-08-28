/*******************************************************************************
 * Copyright (c) 2025 Cactus Team.
 * This code is part of the Cactus-Client distribution. All Rights reserved.
 ******************************************************************************/

package com.dwarslooper.cactus.shuttle.register;

import com.dwarslooper.cactus.shuttle.addon.ShuttleAddon;

import java.util.function.Function;

public record PendingRegistration(ShuttleAddon owner, Function<RegistrationContext, ?> factory) {

}
