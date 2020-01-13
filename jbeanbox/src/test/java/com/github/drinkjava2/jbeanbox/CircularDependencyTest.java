/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.github.drinkjava2.jbeanbox;

import java.util.Set;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.drinkjava2.jbeanbox.annotation.INJECT;

/**
 * Circular Dependency Test, field or method circular dependency is OK,
 * constructor circular dependency is not OK
 * 
 * @author Yong Zhu
 * @since 2.4.7
 */
public class CircularDependencyTest {
	@Before
	public void init() {
		BeanBoxContext.reset();
	}

	public void _______________________() {
	}

	public static class A {
		@Inject
		public B b;
	}

	public static class B {
		@Inject
		public A a;
	}

	/** Field circular dependency inject is allowed */
	@Test
	public void fieldCircularInjectTest() {
		A a = JBEANBOX.getBean(A.class);
		Assert.assertTrue(a == a.b.a);
	}

	public void __________________________() {
	}

	public static class AA {
		public BB bb;

		@INJECT
		private void print(BB bb) {
			this.bb = bb;
		}
	}

	public static class BB {
		public AA aa;

		@Inject
		private void print(AA aa) {
			this.aa = aa;
		}
	}

	/** Method circular dependency inject is allowed */
	@Test
	public void methodCircularInjectTest() {
		AA aa = JBEANBOX.getBean(AA.class);
		Assert.assertTrue(aa == aa.bb.aa);
	}

	public void ________________________() {
	}

	public static class C {
		public D d;
		@Inject
		public C(D d) {
			this.d=d;
		}
	}

	public static class D {
		@Inject
		public C c;
	}

	/** Constructor circular dependency will throw exception */
	@Test(expected = BeanBoxException.class)
	public void badCircularTest() {
		C c=JBEANBOX.getBean(C.class);
		D d=JBEANBOX.getBean(D.class);
		System.out.println(c);
		System.out.println(d);
		System.out.println(c.d);
		System.out.println(d.c);
	}

	public void _________________________() {
	}

	public static class E {
		public F f;
	}

	public static class Ebox extends BeanBox {
		public Object create(BeanBoxContext caller, Set<Object> history) {
			E e = new E();
			e.f = caller.getBean(Fbox.class, true, history);
			return e;
		}
	}

	public static class F {
		public E e;
	}

	public static class Fbox extends BeanBox {
		public Object create(BeanBoxContext caller, Set<Object> history) {
			F f = new F();
			f.e = caller.getBean(Ebox.class,true, history);
			return f;
		}
	}

	/** Create method circular dependency will throw exception */
	@Test(expected = BeanBoxException.class)
	public void badCircularTest2() {
		JBEANBOX.bind(E.class, Ebox.class);
		JBEANBOX.getBean(E.class);
	}

}
