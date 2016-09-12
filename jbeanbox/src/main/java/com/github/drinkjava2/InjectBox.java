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
package com.github.drinkjava2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * InjectBox is the only annotation in this project <br/>
 * 
 * On field, use value or box0 to define a BeanBox, set required=true to throw a exception if injection failed, <br/>
 * box0 to inject BeanBox, pox0 to inject prototype BeanBox, sox0 to inject singleton BeanBox: <br/>
 * 
 * @InjectBox(value=FieldBox.class, required=true) <br/>
 * Field someField;
 * 
 * On field can also use s0, i0, bl0... to inject constant value<br/>
 * @InjectBox(s0="Hello") <br/>
 * Field someField;
 * 
 * On constructor, parameter index start from 0, if parameter omitted will use default value<br/>
 * @InjectBox(s0="hello world", b2=false, box4=EBox.class) <br/>
 * public A(String s, B b, Boolean b1, D d, E e){ ... }
 * 
 * 
 * On Class, use prototype=true to define a prototype bean, otherwise default is singleton bean <br/>
 * @InjectBox(prototype=true) <br/>
 * public SomeClass {...} <br/>
 * 
 * 
 * @author Yong Zhu
 * @version 2.4.1
 * @since 2.4
 * @update 2016-09-07
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.METHOD })
public @interface InjectBox {

	// For field
	public Class<?> value() default Object.class;// Note: equal to box0

	// default if inject failed, will throw an exception, set to false to keep silence
	public boolean required() default true;

	// Default type is singleton, if set prototype=true will build new instance each time
	public boolean prototype() default false;

	// Below are for constructor parameters, only support maximum 6 parameters, if need more add by yourself
	public Class<?> box0() default Object.class;// BeanBox

	public Class<?> box1() default Object.class;

	public Class<?> box2() default Object.class;

	public Class<?> box3() default Object.class;

	public Class<?> box4() default Object.class;

	public Class<?> box5() default Object.class;

	public Class<?> pox0() default Object.class;// force inject a prototype BeanBox

	public Class<?> pox1() default Object.class;

	public Class<?> pox2() default Object.class;

	public Class<?> pox3() default Object.class;

	public Class<?> pox4() default Object.class;

	public Class<?> pox5() default Object.class;

	public Class<?> sox0() default Object.class;// force inject a singleton BeanBox

	public Class<?> sox1() default Object.class;

	public Class<?> sox2() default Object.class;

	public Class<?> sox3() default Object.class;

	public Class<?> sox4() default Object.class;

	public Class<?> sox5() default Object.class;

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
