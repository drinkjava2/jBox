package test.test5_transaction;

import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import com.github.drinkjava2.BeanBox;
import com.github.drinkjava2.InjectBox;

/**
 * This example shows how to use jBeanBox to replace Spring's core to achieve "Declarative Transaction". <br/>
 * This example integrated "C3P0" + "JDBCTemplate" + "Spring Declarative Transaction Service" but <br/>
 * did not use Spring's IOC/AOP core, a weird combination but target is clear: "To eliminate XML".<br/>
 * 
 * @author Yong Zhu
 * @since 2016-6-18
 * @update 2016-6-18
 */

public class Tester {
	@InjectBox
	private JdbcTemplate dao;

	public void insertUser() {
		dao.execute("insert into users (username) values ('User1')");
		// int i = 1 / 0; // Throw a runtime Exception to roll back transaction
		dao.execute("insert into users (username) values ('User2')");
		System.out.println("Users saved successfully");
	}

	@Test
	public void doTest() {
		Tester tester = BeanBox.getBean(Tester.class);
		tester.insertUser();
	}

}