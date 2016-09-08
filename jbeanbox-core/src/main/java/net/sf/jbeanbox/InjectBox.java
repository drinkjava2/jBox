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
 * Inject BeanBox to a field or constructor
 * 
 * On field example:<br/>
 * @InjectBox(value=FieldBox.class, required=true) <br/>
 * Field someField;
 * 
 * On constructor, Inject Object type paramerters:<br/>
 * @InjectBox(s0="hello world", b2=false, box4=EBox.class) <br/>
 * public A(String s, B b, Boolean b1, D d, E e){ ... }
 * 
 * @author Yong Zhu
 * @version 2.4.1
 * @since 2.4
 * @update 2016-09-07
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.CONSTRUCTOR })
public @interface InjectBox {

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

	public String s0() default "";

	public String s1() default "";

	public String s2() default "";

	public String s3() default "";

	public String s4() default "";

	public String s5() default "";

	public int i0() default 0;

	public int i1() default 0;

	public int i2() default 0;

	public int i3() default 0;

	public int i4() default 0;

	public int i5() default 0;

	public boolean b0() default false;

	public boolean b1() default false;

	public boolean b2() default false;

	public boolean b3() default false;

	public boolean b4() default false;

	public boolean b5() default false;

	public byte bt0() default 0;

	public byte bt1() default 0;

	public byte bt2() default 0;

	public byte bt3() default 0;

	public byte bt4() default 0;

	public byte bt5() default 0;

	public long l0() default 0;

	public long l1() default 0;

	public long l2() default 0;

	public long l3() default 0;

	public long l4() default 0;

	public long l5() default 0;

	public short st0() default 0;

	public short st1() default 0;

	public short st2() default 0;

	public short st3() default 0;

	public short st4() default 0;

	public short st5() default 0;

	public float f0() default 0;

	public float f1() default 0;

	public float f2() default 0;

	public float f3() default 0;

	public float f4() default 0;

	public float f5() default 0;

	public double d0() default 0;

	public double d1() default 0;

	public double d2() default 0;

	public double d3() default 0;

	public double d4() default 0;

	public double d5() default 0;

	public char c0() default '\u0000';

	public char c1() default '\u0000';

	public char c2() default '\u0000';

	public char c3() default '\u0000';

	public char c4() default '\u0000';

	public char c5() default '\u0000';

}
