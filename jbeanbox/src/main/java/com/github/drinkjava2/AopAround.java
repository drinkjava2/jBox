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
 * @since 2.4
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface AopAround {
	/**
	 * AOP around box class, point to an intercepter implemented org.aopalliance.intercept.MethodInterceptor, usually
	 * used to support Spring's TransactionInterceptor, detail usage see jBeanBox example.
	 */
	public Class<?> value() default Object.class;// Note: equal to box0

}
