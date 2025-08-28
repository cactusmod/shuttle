/*******************************************************************************
 * Copyright (c) 2025 Cactus Team.
 * This code is part of the Cactus-Client distribution. All Rights reserved.
 ******************************************************************************/

package com.dwarslooper.cactus.shuttle.register.registries.impl;

import com.dwarslooper.cactus.shuttle.register.registries.IContentRegistry;

import java.util.LinkedList;
import java.util.List;

public class SimpleRegistry implements IContentRegistry<Object> {
	private final List<Object> instances = new LinkedList<>();

	@Override
	public void register(Object value) {
		instances.add(value);
	}

	@Override
	public List<Object> getAll() {
		return instances;
	}
}
