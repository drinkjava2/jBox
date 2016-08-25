package examples.example5_transaction;

import java.util.Properties;

import net.sf.jbeanbox.BeanBox;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class TesterBox extends BeanBox {
	static {
		BeanBox.defaultBeanBoxContext.setAOPAround("examples.example5_transaction.Test\\w*", "insert\\w*",
				new TxInterceptorBox(), "invoke");
	}

	static class DSPoolBeanBox extends BeanBox {
		{
			setClassOrValue(ComboPooledDataSource.class);
			setProperty("jdbcUrl", "jdbc:mysql://127.0.0.1:3306/test?user=root&password=root888");// change to your pwd
			setProperty("driverClass", "com.mysql.jdbc.Driver");// change to your jdbc driver name
			setProperty("maxPoolSize", 10);
			setProperty("CheckoutTimeout", 2000);
		}
	}

	static class TxManagerBox extends BeanBox {
		{
			setClassOrValue(DataSourceTransactionManager.class);
			setProperty("dataSource", new DSPoolBeanBox());
		}
	}

	static class TxInterceptorBox extends BeanBox {// Advice
		{
			Properties props = new Properties();
			props.put("insert*", "PROPAGATION_REQUIRED");
			setConstructor(TransactionInterceptor.class, new TxManagerBox(), props);
		}
	}

	public static class JdbcTemplateBox extends BeanBox {
		{
			setConstructor(JdbcTemplate.class, new DSPoolBeanBox());
		}
	}
}
