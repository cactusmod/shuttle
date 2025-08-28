package com.dwarslooper.cactus.shuttle.test;

import com.dwarslooper.cactus.shuttle.addon.IShuttleAddonLifecycle;
import com.dwarslooper.cactus.shuttle.register.RegistryBus;
import com.dwarslooper.cactus.shuttle.test.obj.Printer;
import com.dwarslooper.cactus.shuttle.test.obj.TestRegistration;

public class TestAddonEntrypoint implements IShuttleAddonLifecycle {

	@Override
	public void onInitialize(RegistryBus registryBus) {
		System.out.println("Hello! I'm an addon!");

		registryBus.provideService(Printer.class, new Printer());

		registryBus.register(TestRegistration.class, ctx -> new TestRegistration(ctx.require(Printer.class), "Bob", 42));

		registryBus.completeAndTake(TestRegistration.class, reg -> {
			System.out.println("completed tests!");
			reg.print();
		});
	}

}
