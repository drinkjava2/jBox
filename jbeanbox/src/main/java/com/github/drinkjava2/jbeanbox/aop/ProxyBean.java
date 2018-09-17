/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.github.drinkjava2.jbeanbox.aop;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;

import com.github.drinkjava2.cglib3_2_0.proxy.Callback;
import com.github.drinkjava2.cglib3_2_0.proxy.MethodInterceptor;
import com.github.drinkjava2.cglib3_2_0.proxy.MethodProxy;
import com.github.drinkjava2.jbeanbox.BeanBox;
import com.github.drinkjava2.jbeanbox.BeanBoxContext;
import com.github.drinkjava2.jbeanbox.BeanBoxException;

/**
 * ProxyBean to build a Invocation, Invocation call next invocation...
 * 
 * @author Yong Zhu
 * @since 2.4
 *
 */
@SuppressWarnings("all")
class ProxyBean implements MethodInterceptor, Callback {
	protected Object[] box_ctx;

	protected ProxyBean(BeanBox box, BeanBoxContext ctx) {
		box_ctx = new Object[] { box, ctx };
	}

	@Override
	public Object intercept(Object obj, Method m, Object[] args, MethodProxy mprxy) throws Throwable {
		BeanBox box = (BeanBox) box_ctx[0];
		BeanBoxContext ctx = (BeanBoxContext) box_ctx[1];
		List<Object> inters = box.getMethodAops().get(m);
		if(inters==null || inters.size()==0)
			return mprxy.invokeSuper(obj, args); 
		org.aopalliance.intercept.MethodInterceptor inter = ctx.getBean(inters.get(0));
		if (inter != null)
			return inter.invoke(new MtdInvoc(obj, m, args, mprxy, inters, ctx, 1));
		return mprxy.invokeSuper(obj, args);
	}

	//@formatter:off
	public static class MtdInvoc implements MethodInvocation {// AOP alliance required
		private final Object obj;
		private final Method m;
		private final Object[] args;
		private final MethodProxy mprxy;
		private final List<Object> inters;
		private final BeanBoxContext ctx;
		private int count;

		protected MtdInvoc(Object obj, Method m, Object[] args, MethodProxy mprxy, List<Object> inters, BeanBoxContext ctx,
				int count) {
			this.obj = obj;	this.m = m;	this.args = args;	this.mprxy = mprxy;
			this.inters = inters;	this.ctx = ctx;	this.count = count;
		}
 
		public Object proceed() throws Throwable {
			if (count <= (inters.size() - 1)) { 
				org.aopalliance.intercept.MethodInterceptor inter = ctx.getBean(inters.get(count));
			    return inter.invoke(new MtdInvoc(obj, m, args, mprxy, inters, ctx, count + 1));
			}
			return mprxy.invokeSuper(obj, args);
		} 
		
		public final Object getThis() { return obj; }
 
		public final AccessibleObject getStaticPart() { return m; }
 
		public final Method getMethod() { return m; }
 
		public final Object[] getArguments() { return this.args != null ? this.args : new Object[0]; }
	}

}