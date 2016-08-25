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
 * Inject BeanBox to a field with @Inject annotation <br/>
 * 
 * 
 * For a field with @Inject annotation, find BeanBox class and inject Bean Instance to it, finding is follow below
 * order: <br/>
 * Format: A.class{ @Inject(B.class) C field; ...} <br/>
 * 1) If B.class is BeanBox class, create bean instance and inject to field <br/>
 * 2) Find static class "B.CBox.class" in B.class <br/>
 * 3) Find static class "B.$FieldBox.class" in B.class <br/>
 * Format: A.class{ @Inject C field; ...} <br/>
 * 4)Find CBox.class in same package of C <br/>
 * 5)Find "ABox.CBox.class" in ABox.class <br/>
 * 6) find ConfigClass$CBox.class in globalConfig classes <br/>
 * 7) find ConfigClass$FieldBox.class in globalConfig classes <br/>
 * 
 * @author Yong Zhu
 * @since 2016-2-13
 * @update 2016-08-21
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InjectBox {
	// value can be ignore
	public Class<?> value() default Object.class;

	// if required set to false and no BeanBox found, do nothing. Otherwise throw a Error
	public boolean required() default true;
}
