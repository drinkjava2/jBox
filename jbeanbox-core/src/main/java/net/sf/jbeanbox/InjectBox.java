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
 * Inject BeanBox to a field or constructor with @Inject annotation, use box0, box1... to inject singleTon Bean
 * constructor parameters, use s0,s1,s2... to inject string parameters, use i0, i1, i2.. inject int, use b0, b1, b2..
 * inject boolean and only allowed "true" or "false"
 * 
 * @author Yong Zhu
 * @since 2016-2-13
 * @update 2016-08-31
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.CONSTRUCTOR })
public @interface InjectBox {
	public static String IMPOSSIBLE_STRING = "StuPid AnNotatioN";
	public static int IMPOSSIBLE_INT = -99819981;

	public Class<?> value() default Object.class;

	// Default if no BeanBox can be created, will throw an exception, set to false to disable
	public boolean required() default true;

	// Below are for constructor parameters, more than 6 parameters better use other configuration
	public Class<?> box0() default Object.class;// inject BeanBox

	public Class<?> box1() default Object.class;

	public Class<?> box2() default Object.class;

	public Class<?> box3() default Object.class;

	public Class<?> box4() default Object.class;

	public Class<?> box5() default Object.class;

	public String s0() default "-99819981"; // inject String, defalut is an impossible value

	public String s1() default "-99819981";

	public String s2() default "-99819981";

	public String s3() default "-99819981";

	public String s4() default "-99819981";

	public String s5() default "-99819981";

	public int i0() default -99819981; // inject int

	public int i1() default -99819981;

	public int i2() default -99819981;

	public int i3() default -99819981;

	public int i4() default -99819981;

	public int i5() default -99819981;

	public String b0() default "-99819981";// inject boolean

	public String b1() default "-99819981";

	public String b2() default "-99819981";

	public String b3() default "-99819981";

	public String b4() default "-99819981";

	public String b5() default "-99819981";

	// TODO add byte, long, double, ...
}
