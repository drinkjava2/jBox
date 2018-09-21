package com.github.drinkjava2.jbeanbox.benchmark.objects;

import javax.inject.Inject;

import com.github.drinkjava2.jbeanbox.annotation.PROTOTYPE;

@PROTOTYPE
public class C {
	public D1 d1;
	public D2 d2;

	@Inject 
	public C(D1 d1, D2 d2) {
		this.d1 = d1;
		this.d2 = d2;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
