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

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;

/**
 * JoinPoint interface detail see AspectJ doc, here is a brief implementation
 * 
 * @author Yong Zhu
 * @since 2.4
 */
class AspectjJoinPoint implements JoinPoint {
	protected Object proxy;
	protected Object target;
	protected Method method;
	protected Signature signature;
	protected Object[] arguments;
	protected AdviceCaller caller;

	protected AspectjJoinPoint(Object proxyBean, Object target, Method method, Object[] arguments,
			AdviceCaller caller) {
		this.proxy = proxyBean;
		this.target = target;
		this.method = method;
		this.arguments = arguments;
		this.caller = caller;
	}

	@Override
	public Object[] getArgs() {
		return arguments;
	}

	@Override
	public String getKind() {
		return JoinPoint.METHOD_EXECUTION;
	}

	@Override
	public Signature getSignature() {
		if (this.signature == null)
			this.signature = new MethodSignatureImpl();
		return signature;
	}

	@Override
	public SourceLocation getSourceLocation() {
		throw new UnsupportedOperationException();
	}

	@Override
	public StaticPart getStaticPart() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getTarget() {
		return target;
	}

	@Override
	public Object getThis() {
		return proxy;
	}

	@Override
	public String toLongString() {
		return this.getClass().getName();
	}

	@Override
	public String toShortString() {
		return this.getClass().getName();
	}

	protected class MethodSignatureImpl implements MethodSignature {// MethodSignature is required by Aspectj

		@Override
		public String getName() {
			return method.getName();
		}

		@Override
		public int getModifiers() {
			return method.getModifiers();
		}

		@Override
		public Class<?> getDeclaringType() {
			return method.getDeclaringClass();
		}

		@Override
		public String getDeclaringTypeName() {
			return method.getDeclaringClass().getName();
		}

		@Override
		public Class<?> getReturnType() {
			return method.getReturnType();
		}

		@Override
		public Method getMethod() {
			return method;
		}

		@Override
		public Class<?>[] getParameterTypes() {
			return method.getParameterTypes();
		}

		@Override
		public String[] getParameterNames() {// just throw unsupported exception
			throw new UnsupportedOperationException();
		}

		@Override
		public Class<?>[] getExceptionTypes() {
			return method.getExceptionTypes();
		}

		@Override
		public String toShortString() {
			return method.getName();
		}

		@Override
		public String toLongString() {
			return method.getName();
		}
	}
}