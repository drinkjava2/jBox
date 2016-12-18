
package test.core_test.reflection_utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

import com.github.drinkjava2.ReflectionUtils;

public class ReflectionUtilsTest {

	@Test
	public void testGetDeclaredMethod() {
		Object obj = new Son();
		Method method = ReflectionUtils.getDeclaredMethod(obj, "publicMethod");
		Assert.assertEquals("publicMethod", method.getName());
		method = ReflectionUtils.getDeclaredMethod(obj, "defaultMethod");
		Assert.assertEquals("defaultMethod", method.getName());
		method = ReflectionUtils.getDeclaredMethod(obj, "protectedMethod");
		Assert.assertEquals("protectedMethod", method.getName());
		method = ReflectionUtils.getDeclaredMethod(obj, "privateMethod");
		Assert.assertEquals("privateMethod", method.getName());

	}

	/**
	 * Test invoke Parent class method
	 */
	@Test
	public void testInvokeMethod() throws Exception {
		Object obj = new Son();
		ReflectionUtils.invokeMethod(obj, "publicMethod", null, null);
		ReflectionUtils.invokeMethod(obj, "defaultMethod", null, null);
		ReflectionUtils.invokeMethod(obj, "protectedMethod", null, null);
		ReflectionUtils.invokeMethod(obj, "privateMethod", null, null);
	}

	/**
	 * Test get Parent class field
	 */
	@Test
	public void testGetDeclaredField() {
		Object obj = new Son();
		Field field = ReflectionUtils.getDeclaredField(obj, "publicField");
		Assert.assertEquals("publicField", field.getName());
		field = ReflectionUtils.getDeclaredField(obj, "defaultField");
		Assert.assertEquals("defaultField", field.getName());
		field = ReflectionUtils.getDeclaredField(obj, "protectedField");
		Assert.assertEquals("protectedField", field.getName());
		field = ReflectionUtils.getDeclaredField(obj, "privateField");
		Assert.assertEquals("privateField", field.getName());
	}

	@Test
	public void testGetFieldValue() {
		Object obj = new Son();
		Assert.assertEquals("publicField", ReflectionUtils.getFieldValue(obj, "publicField"));
		Assert.assertEquals("defaultField", ReflectionUtils.getFieldValue(obj, "defaultField"));
		Assert.assertEquals("protectedField", ReflectionUtils.getFieldValue(obj, "protectedField"));
		Assert.assertEquals("privateField", ReflectionUtils.getFieldValue(obj, "privateField"));
	}

	@Test
	public void testSetFieldValue() {
		Object obj = new Son();
		ReflectionUtils.setFieldValue(obj, "publicField", "a");
		Assert.assertEquals("a", ReflectionUtils.getFieldValue(obj, "publicField"));
		ReflectionUtils.setFieldValue(obj, "defaultField", "b");
		Assert.assertEquals("b", ReflectionUtils.getFieldValue(obj, "defaultField"));
		ReflectionUtils.setFieldValue(obj, "protectedField", "c");
		Assert.assertEquals("c", ReflectionUtils.getFieldValue(obj, "protectedField"));
		ReflectionUtils.setFieldValue(obj, "privateField", "d");
		Assert.assertEquals("d", ReflectionUtils.getFieldValue(obj, "privateField"));
	}
}