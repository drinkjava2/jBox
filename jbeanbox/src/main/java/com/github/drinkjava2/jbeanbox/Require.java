/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.github.drinkjava2.jbeanbox;

import java.lang.annotation.Annotation;

/**
 * This class collect all required info inside of &#064;Inject and
 * &#064;Qualifiler and &#064;Value and &#064;Named...
 * 
 * @author Yong Zhu
 * @since 2.5.0
 */
public class Require {
	Object target;
	boolean pureValue;
	boolean required; 
	
	public Require() { // default constr
	}

	public Require(Object target) {
		this.target = target;
	}

	public Require(Object target, boolean required) {
		this.target = target;
		this.required = required;
	}

	public Require(Object target, boolean pureValue, boolean required, Class<? extends Annotation> qualifierClass,
			Object qualifierValue) {
		this.target = target;
		this.pureValue = pureValue;
		this.required = required; 
	}

	public <T> T getBean(Object target) {
		return null;
	}

	public Require setTarget(Object target) {
		this.target = target;
		return this;
	}

	public Require setPureValue(boolean pureValue) {
		this.pureValue = pureValue;
		return this;
	}

	public Require setRequired(boolean required) {
		this.required = required;
		return this;
	} 

}