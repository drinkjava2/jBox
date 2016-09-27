package examples.example6_type_safe;

import org.springframework.jdbc.core.JdbcTemplate;

import com.github.drinkjava2.BeanBox;
import com.github.drinkjava2.InjectBox;

/**
 * This example is similar like example5, but in "TesterBox" use Java type safe configurations to create bean instance.
 * <br/>
 * 
 * @author Yong Zhu
 * @since 2016-8-23
 * @update 2016-8-23
 */

public class Tester {

	@InjectBox
	private JdbcTemplate dao;

	public void insertUser() {
		dao.execute("insert into users values ('User1')");
		//int i = 1 / 0; //Throw a runtime Exception to roll back transaction
		dao.execute("insert into users values ('User2')");
		System.out.println("Users saved successfully");
	}

	public static void main(String[] args) {
		Tester tester = BeanBox.getBean(Tester.class);
		tester.insertUser();
	}
}