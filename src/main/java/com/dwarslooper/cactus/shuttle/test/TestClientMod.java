package com.dwarslooper.cactus.shuttle.test;

import com.dwarslooper.cactus.shuttle.Shuttle;
import com.dwarslooper.cactus.shuttle.handler.IAddonHandler;
import net.fabricmc.api.ClientModInitializer;

public class TestClientMod implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		System.out.println("Hello! I'm a mod!");
		IAddonHandler handler = Shuttle.createAddonHandler("shuttle");
		handler.discoverAndRegister();
		handler.initializeAll();
	}

}
