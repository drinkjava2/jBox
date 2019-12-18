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

import java.util.Set;

/**
 * Caller store BeanBoxContext, required and history
 *
 * @author Yong Zhu
 * @since 2.4.8
 *
 */
public class Caller {
	public BeanBoxContext ctx;// NOSONAR
	public boolean required = true;// NOSONAR
	public Set<Object> history;// NOSONAR
	public Object result; // NOSONAR

	public Caller(BeanBoxContext ctx, boolean required, Set<Object> history, Object result) {
		this.result = result;
		this.ctx = ctx;
		this.required = required;
		this.history = history;
	}

	public <T> T getBean(Object target) {
		return ctx.getBean(target, required, history);
	}

	public <T> T get(Class<T> targetClass) {
		return ctx.getBean(targetClass, required, history);
	}

	public BeanBoxContext getCtx() {
		return ctx;
	}

	public void setCtx(BeanBoxContext ctx) {
		this.ctx = ctx;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public Set<Object> getHistory() {
		return history;
	}

	public void setHistory(Set<Object> history) {
		this.history = history;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

}
