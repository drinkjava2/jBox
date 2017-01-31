
package test.core_test.reflection_utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.drinkjava2.jbeanbox.BeanBox;
import com.github.drinkjava2.jbeanbox.springsrc.ReflectionUtils;

public class ReflectionUtilsTest {
	@Before
	public void beforeTest() {
		System.out.println("========= ReflectionUtilsTest =========");
	}
	
	@Test
	public void testGetDeclaredMethod() {
		Object obj = new Son();
		Method method = ReflectionUtils.findMethod(obj.getClass(), "publicMethod");
		Assert.assertEquals("publicMethod", method.getName());
		ReflectionUtils.invokeMethod(method, obj);
		method = ReflectionUtils.findMethod(obj.getClass(), "defaultMethod");
		Assert.assertEquals("defaultMethod", method.getName());
		ReflectionUtils.makeAccessible(method);
		ReflectionUtils.invokeMethod(method, obj);
		method = ReflectionUtils.findMethod(obj.getClass(), "protectedMethod");
		Assert.assertEquals("protectedMethod", method.getName());
		ReflectionUtils.makeAccessible(method);
		ReflectionUtils.invokeMethod(method, obj);
		method = ReflectionUtils.findMethod(obj.getClass(), "privateMethod");
		Assert.assertEquals("privateMethod", method.getName());
		ReflectionUtils.makeAccessible(method);
		ReflectionUtils.invokeMethod(method, obj);
	}

	/**
	 * Test get Parent class field
	 */
	@Test
	public void testGetDeclaredField() {
		Object obj = new Son();

		Field field = ReflectionUtils.findField(obj.getClass(), "publicField");
		Assert.assertEquals("publicField", field.getName());
		ReflectionUtils.setField(field, obj, "publicField");
		Assert.assertEquals("publicField", ReflectionUtils.getField(field, obj));
		field = ReflectionUtils.findField(obj.getClass(), "defaultField");
		Assert.assertEquals("defaultField", field.getName());
		ReflectionUtils.makeAccessible(field);
		ReflectionUtils.setField(field, obj, "defaultField");
		Assert.assertEquals("defaultField", ReflectionUtils.getField(field, obj));
		field = ReflectionUtils.findField(obj.getClass(), "protectedField");
		Assert.assertEquals("protectedField", field.getName());
		ReflectionUtils.makeAccessible(field);
		ReflectionUtils.setField(field, obj, "protectedField");
		Assert.assertEquals("protectedField", ReflectionUtils.getField(field, obj));
		field = ReflectionUtils.findField(obj.getClass(), "privateField");
		Assert.assertEquals("privateField", field.getName());
		ReflectionUtils.makeAccessible(field);
		ReflectionUtils.setField(field, obj, "privateField");
		Assert.assertEquals("privateField", ReflectionUtils.getField(field, obj));
	}

	public static class SonBox extends BeanBox {
		{
			this.setClassOrValue(Son.class);
			this.setProperty("user", "user1");
			this.setProperty("userName", "user2");
			this.setProperty("age", 10);
		}
	}

	@Test
	public void testSon() {
		Son son = BeanBox.getBean(SonBox.class);
		Assert.assertEquals("user1", son.getUser());
		Assert.assertEquals("user2", son.getUserName());
		Assert.assertEquals(10, (int) son.getAge());
	}

	public static void main(String[] args) {
		Son son = BeanBox.getBean(SonBox.class);
		Assert.assertEquals("user1", son.getUser());
		Assert.assertEquals("user2", son.getUserName());
		Assert.assertEquals(10, (int) son.getAge());
	}
}