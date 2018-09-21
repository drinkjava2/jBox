package com.github.drinkjava2.jbeanbox.benchmark.objects;

import javax.inject.Inject;

import com.github.drinkjava2.jbeanbox.annotation.PROTOTYPE;

@PROTOTYPE
public class B {
	public C c;

	@Inject 
	public B(C c) {
		this.c = c;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
