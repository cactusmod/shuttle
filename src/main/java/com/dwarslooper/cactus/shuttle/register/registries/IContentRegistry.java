/*******************************************************************************
 * Copyright (c) 2025 Cactus Team.
 * This code is part of the Cactus-Client distribution. All Rights reserved.
 ******************************************************************************/

package com.dwarslooper.cactus.shuttle.register.registries;

import java.util.List;

public interface IContentRegistry<T> {
    void register(T value);
    List<T> getAll();
}
