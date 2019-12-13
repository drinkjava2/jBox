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

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Yong Zhu
 * @since 2.4.9
 */
public class JavaSuperclassTest extends BeanBoxTest {
	static class Parent {
		A field;

		A method;

		public void setField(A a) {
			method = a;
		}
	}

	static class Child extends Parent {
	}

	static class SubBox extends BeanBox {
		{
			this.setBeanClass(Child.class);
			this.injectField("field", new A());
			this.injectMethod("setField", A.class, new A());
			this.injectMtd("setField", new A()); // this is the shortcut usage if type same
		}
	}

	static class A {
	}

	@Test
	public void testSuperclassInjection() {
		Child c = JBEANBOX.getBean(SubBox.class);
		Assert.assertNotNull(c.field);
		Assert.assertNotNull(c.method);
	}

}
