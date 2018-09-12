package com.github.drinkjava2.jbeanbox.benchmark.objects;

import javax.inject.Inject;

import com.github.drinkjava2.jbeanbox.annotation.PROTOTYPE;

@PROTOTYPE
public class A {
	public B b;

	@Inject
	public A(B b) {
		this.b = b;
	}

}
