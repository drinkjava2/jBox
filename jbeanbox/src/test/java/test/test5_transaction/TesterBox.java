package test.test5_transaction;

import java.util.Properties;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.github.drinkjava2.jbeanbox.BeanBox;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 *
 *
 * @author Yong Zhu
 * @since 2.4
 */
public class TesterBox extends BeanBox {
	static {
		BeanBox.defaultContext.setAOPAround("test.test5_transaction.Test\\w*", "insert\\w*", new TxInterceptorBox(),
				"invoke");
	}

	// H2Database memory database c3p0 DataSource pool setting
	public static class DSPoolBeanBox extends BeanBox {
		{
			setClassOrValue(ComboPooledDataSource.class);
			setProperty("jdbcUrl", "jdbc:h2:~/test");
			setProperty("driverClass", "org.h2.Driver");
			setProperty("user", "sa");
			setProperty("password", "");
			setProperty("maxPoolSize", 10);
		}
	}

	static class TxManagerBox extends BeanBox {
		{
			setClassOrValue(DataSourceTransactionManager.class);
			setProperty("dataSource", DSPoolBeanBox.class);
		}
	}

	static class TxInterceptorBox extends BeanBox {// Advice
		{
			Properties props = new Properties();
			props.put("insert*", "PROPAGATION_REQUIRED");
			setConstructor(TransactionInterceptor.class, TxManagerBox.class, props);
		}
	}

	public static class JdbcTemplateBox extends BeanBox {
		{
			setConstructor(JdbcTemplate.class, DSPoolBeanBox.class);
		}
	}
}
