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

/**
 * AopUtils create AOP proxy bean
 * 
 * @author Yong Zhu
 * @since 2.5.0
 *
 */
public enum NameMatchUtil {
	;

	/**
	 * A simple matcher for class and method name, only 1 * allowed <br/>
	 * "*abc.ef" matches "any.abc.ef", "anymoreabc.ef" ... <br/>
	 * "abc.ef*" matches "abc.efg", "abc.efg.hj" ... <br/>
	 * "abc*def" matches "abcd.efg.ddef", "abcany*anydef"
	 */
	public static boolean nameMatch(String regex, String name) {
		if (regex == null || regex.length() == 0 || name == null || name.length() == 0)
			return false;
		if ('*' == (regex.charAt(0))) {
			return name.endsWith(regex.substring(1));
		} else if (regex.endsWith("*")) {
			return name.startsWith(regex.substring(0, regex.length() - 1));
		} else {
			int starPos = regex.indexOf('*');
			if (-1 == starPos)
				return regex.equals(name);
			return name.startsWith(regex.substring(0, starPos)) && name.endsWith(regex.substring(starPos + 1));
		}
	}
}
