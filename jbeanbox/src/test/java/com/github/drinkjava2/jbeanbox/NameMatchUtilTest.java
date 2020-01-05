/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.github.drinkjava2.jbeanbox;

import static com.github.drinkjava2.jbeanbox.NameMatchUtil.nameMatch;

import org.junit.Assert;
import org.junit.Test;

public class NameMatchUtilTest {
	@Test
	public void nameMatchUtilTest() {
		Assert.assertTrue(nameMatch("abc*", "abcx"));
		Assert.assertTrue(nameMatch("ghi*jkl", "ghixxjkl"));
		Assert.assertTrue(nameMatch("*mn", "xmn"));

		Assert.assertTrue(nameMatch("abc*|def*|ghi*jkl|*mn", "abcd"));
		Assert.assertTrue(nameMatch("abc*|def*|ghi*jkl|*mn", "def"));
		Assert.assertTrue(nameMatch("abc*|def*|ghi*jkl|*mn", "ghixxjkl"));
		Assert.assertTrue(nameMatch("abc*|def*|ghi*jkl|*mn", "ghixxjkl"));
		Assert.assertTrue(nameMatch("abc*|def*|ghi*jkl|*mn", "mn"));

		Assert.assertFalse(nameMatch("abc*|def*|ghi*jkl|*mn", "abd"));
		Assert.assertFalse(nameMatch("abc*|def*|ghi*jkl|*mn", "de"));
		Assert.assertFalse(nameMatch("abc*|def*|ghi*jkl|*mn", "ghxxkl"));
		Assert.assertFalse(nameMatch("abc*|def*|ghi*jkl|*mn", "n"));
	}
}
