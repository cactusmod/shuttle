package com.dwarslooper.cactus.shuttle.test.obj;

public record TestRegistration(Printer printer, String name, int value) {

	public void print() {
		printer.print(this);
	}

}
