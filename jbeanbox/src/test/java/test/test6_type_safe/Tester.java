package test.test6_type_safe;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import com.github.drinkjava2.BeanBox;
import com.github.drinkjava2.InjectBox;

/**
 * This example is similar like example5, but in "TesterBox" use Java type safe configurations to create bean instance.
 * <br/>
 * 
 * @author Yong Zhu
 * @since 2.4
 */

public class Tester {
	@Before
	public void beforeTest() {
		System.out.println("========= Java type safe transaction configuration test=========");
	}

	@InjectBox
	private JdbcTemplate dao;

	public void insertUser1() {
		dao.execute("insert into users (username) values ('User1')");
	}

	public void insertUser2() {
		dao.execute("insert into users (username) values ('User2')");
	}

	public void insertUser() {
		insertUser1();
		int count = dao.queryForObject("select count(*) from users", Integer.class);
		System.out.println(count + " record inserted");
		Assert.assertEquals(1, count);
		System.out.println(1 / 0);// Throw a runtime Exception to roll back transaction
		insertUser2();
	}

	@Test
	public void doTest() {
		Tester tester = BeanBox.getBean(Tester.class);
		tester.dao.execute("drop table if exists users");
		tester.dao.execute("create table users(username varchar(30))");
		try {
			tester.insertUser();
		} catch (Exception e) {
			System.out.println("Exception found, roll back");
		}
		int count = tester.dao.queryForObject("select count(*) from users", Integer.class);
		System.out.println(count + " record found");
		Assert.assertEquals(0, count);
	}

}