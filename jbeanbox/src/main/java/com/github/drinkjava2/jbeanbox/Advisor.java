/**
 * Copyright (C) 2016 Yong Zhu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.drinkjava2.jbeanbox;

import java.util.regex.Pattern;

/**
 * Advisor class, Advisor = Advice + Point-cut
 * 
 * @author Yong Zhu
 * @since 2.4
 *
 */
class Advisor {
	String classnameReg;
	String methodNameReg;
	String adviceMethodName;
	BeanBox adviceBeanBox;
	boolean isAOPAlliance = false;
	String adviceType;

	/**
	 * Create an Advisor
	 * 
	 * @param classnameReg
	 * @param methodNameReg
	 * @param adviceBeanBox
	 * @param adviceMethodName
	 * @param adviceType
	 * @param isAOPAlliance
	 */
	Advisor(String classnameReg, String methodNameReg, BeanBox adviceBeanBox, String adviceMethodName,
			String adviceType, boolean isAOPAlliance) {
		if (BeanBoxUtils.isEmptyStr(classnameReg) || BeanBoxUtils.isEmptyStr(methodNameReg) || adviceBeanBox == null
				|| BeanBoxUtils.isEmptyStr(adviceMethodName))
			throw new AssertionError("BeanBox create Advisor error! ClassNameReg:" + classnameReg + " methodNameReg:"
					+ methodNameReg + " beanbox:" + adviceBeanBox + " aroundMethodName:" + adviceMethodName);
		this.classnameReg = classnameReg;
		this.methodNameReg = methodNameReg;
		this.adviceBeanBox = adviceBeanBox;
		this.adviceMethodName = adviceMethodName;
		this.isAOPAlliance = isAOPAlliance;
		this.adviceType = adviceType;
	}

	/**
	 * Check if beanClassName and methodName match classnameReg and methodNameReg
	 */
	protected boolean match(String beanClassName, String methodName) {
		String beanclzName = beanClassName;
		int i = beanclzName.indexOf("$$");
		if (i > 0)
			beanclzName = beanclzName.substring(0, i);

		boolean match1 = beanclzName.equals(classnameReg)
				|| Pattern.compile(classnameReg).matcher(beanclzName).matches();
		boolean match2 = methodName.equals(methodNameReg)
				|| Pattern.compile(methodNameReg).matcher(methodName).matches();
		/*
		 * System.out.println("^^^^^^^^^^^^^^^^^^^^^^^"); //NOSONAR
		 * System.out.println("classnameReg=" + classnameReg);//NOSONAR
		 * System.out.println("beanclzName=" + beanclzName);//NOSONAR
		 * System.out.println("methodNameReg=" + methodNameReg);//NOSONAR
		 * System.out.println("methodName=" + methodName); //NOSONAR
		 * System.out.println("match result="+(match1 && match2));//NOSONAR
		 * System.out.println("vvvvvvvvvvvvvvvvvvvvvvv");//NOSONAR
		 */
		return match1 && match2;
	}
}