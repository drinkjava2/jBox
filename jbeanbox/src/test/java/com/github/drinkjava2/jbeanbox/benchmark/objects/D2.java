package com.github.drinkjava2.jbeanbox.benchmark.objects;

import javax.inject.Inject;

import com.github.drinkjava2.jbeanbox.annotation.PROTOTYPE;

@PROTOTYPE
public class D2 { 
	public final E e;

	@Inject
	public D2(E e) {
		this.e = e;

	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
