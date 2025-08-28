package com.dwarslooper.cactus.shuttle;

import com.dwarslooper.cactus.shuttle.handler.IAddonHandler;
import com.dwarslooper.cactus.shuttle.handler.impl.AddonHandlerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Shuttle {

	private static final Logger logger = LoggerFactory.getLogger("Shuttle Addon Handler");

	public static Logger getLogger() {
		return logger;
	}

	public static IAddonHandler createAddonHandler(String entrypoint) {
		return new AddonHandlerImpl(entrypoint);
	}

}
