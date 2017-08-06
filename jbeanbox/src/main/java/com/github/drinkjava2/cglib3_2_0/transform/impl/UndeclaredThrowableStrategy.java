/*
 * Copyright 2003 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.drinkjava2.cglib3_2_0.transform.impl;

import com.github.drinkjava2.cglib3_2_0.core.ClassGenerator;
import com.github.drinkjava2.cglib3_2_0.core.DefaultGeneratorStrategy;
import com.github.drinkjava2.cglib3_2_0.core.GeneratorStrategy;
import com.github.drinkjava2.cglib3_2_0.core.TypeUtils;
import com.github.drinkjava2.cglib3_2_0.transform.ClassTransformer;
import com.github.drinkjava2.cglib3_2_0.transform.MethodFilter;
import com.github.drinkjava2.cglib3_2_0.transform.MethodFilterTransformer;
import com.github.drinkjava2.cglib3_2_0.transform.TransformingClassGenerator;

/**
 * A {@link GeneratorStrategy} suitable for use with {@link com.github.drinkjava2.cglib3_2_0.proxy.Enhancer} which
 * causes all undeclared exceptions thrown from within a proxied method to be wrapped
 * in an alternative exception of your choice.
 */
@SuppressWarnings({"rawtypes" })
public class UndeclaredThrowableStrategy extends DefaultGeneratorStrategy {
    

    private Class wrapper;

	/**
     * Create a new instance of this strategy.
     * @param wrapper a class which extends either directly or
     * indirectly from <code>Throwable</code> and which has at least one
     * constructor that takes a single argument of type
     * <code>Throwable</code>, for example
     * <code>java.lang.reflect.UndeclaredThrowableException.class</code>
     */
    public UndeclaredThrowableStrategy(Class wrapper) {
       this.wrapper = wrapper;
    }
    
    private static final MethodFilter TRANSFORM_FILTER = new MethodFilter() {
        public boolean accept(int access, String name, String desc, String signature, String[] exceptions) {
            return !TypeUtils.isPrivate(access) && name.indexOf('$') < 0;
        }
    };

    protected ClassGenerator transform(ClassGenerator cg) throws Exception {
    	 ClassTransformer   tr = new UndeclaredThrowableTransformer(wrapper);
         tr = new MethodFilterTransformer(TRANSFORM_FILTER, tr);
        return new TransformingClassGenerator(cg, tr);
    }
}

