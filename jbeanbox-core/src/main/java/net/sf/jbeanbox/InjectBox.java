/**
 * Copyright (C) 2016 Yong Zhu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.jbeanbox;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Inject BeanBox to a field or constructor with @Inject annotation <br/>
 * use arg1, arg2... to inject singleTon Bean constructor parameters<br/>
 * use p1, p2... to inject prototype Bean constructor parameters<br/>
 * 
 * @author Yong Zhu
 * @since 2016-2-13
 * @update 2016-08-21
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.CONSTRUCTOR })
public @interface InjectBox {
	public Class<?> value() default Object.class;

	// Default if no BeanBox can be created, will throw an error, set to false to disable
	public boolean required() default true;

	// Below are for constructor parameters, if more than 3 parameters, better to use BeanBox configuration
	public Class<?> class1() default Object.class;// for signleTon bean

	public Class<?> class2() default Object.class;

	public Class<?> class3() default Object.class;

	public Class<?> proto1() default Object.class;// for prototype bean

	public Class<?> proto2() default Object.class;

	public Class<?> proto3() default Object.class;

	public String s1() default ""; // String

	public String s2() default "";

	public String s3() default "";

	public int i1() default 0; // String

	public int i2() default 0;

	public int i3() default 0;

	public boolean b1() default false;

	public boolean b2() default false;

	public boolean b3() default false;

	public byte bt1() default 0;

	public byte bt2() default 0;

	public byte bt3() default 0;

	public double d1() default 0;

	public double d2() default 0;

	public double d3() default 0;

	public float f1() default 0;

	public float f2() default 0;

	public float f3() default 0;

}
