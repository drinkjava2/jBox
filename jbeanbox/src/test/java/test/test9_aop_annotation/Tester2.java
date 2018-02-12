package test.test9_aop_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import com.github.drinkjava2.jbeanbox.BeanBox;
import com.github.drinkjava2.jbeanbox.InjectBox;

import test.test9_aop_annotation.Tester2Box.TxInterceptorBox;

/**
 * This example shows how to use jBeanBox to replace Spring's core to achieve
 * "Declarative Transaction". <br/>
 * This example integrated "C3P0" + "JDBCTemplate" + "Spring Declarative
 * Transaction Service" but <br/>
 * did not use Spring's IOC/AOP core, a weird combination but target is clear:
 * "To eliminate XML".<br/>
 * 
 * @author Yong Zhu
 * @since 2.4
 */

public class Tester2 {

	@Before
	public void beforeTest() {
		System.out.println("========= AopAround Annotation of Declarative Transaction Test =========");
	}

	@InjectBox
	private JdbcTemplate dao;

	public void insertUser1() {
		dao.execute("insert into users (username) values ('User1')");
	}

	public void insertUser2() {
		dao.execute("insert into users (username) values ('User2')");
	}

	@MYAnno
	public void insertUser() {
		insertUser1();
		int count = dao.queryForObject("select count(*) from users", Integer.class);
		System.out.println(count + " record inserted");
		Assert.assertEquals(1, count);
		System.out.println(1 / 0);// Throw a runtime Exception to roll back transaction
		insertUser2();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public static @interface MYAnno {
		public Class<?> value() default Object.class;
	}

	@Test
	public void doTest() {
		BeanBox.regAopAroundAnnotation(MYAnno.class, TxInterceptorBox.class);
		Tester2 tester2 = BeanBox.getBean(Tester2.class);
		tester2.dao.execute("drop table if exists users");
		tester2.dao.execute("create table users(username varchar(30))");
		try {
			tester2.insertUser();
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("Exception found, roll back");
		}
		int count = tester2.dao.queryForObject("select count(*) from users", Integer.class);
		System.out.println(count + " record found");
		Assert.assertEquals(0, count);
	}

}