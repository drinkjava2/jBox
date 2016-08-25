jBeanBox
====

**License:** [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

jBeanBox is a micro scale IOC & AOP tool, uses java classes as configuration to replace XML.  
(jBeanBox is still in developing, looking for contributors to improve/test/debug this project)  

Advantage of jBeanBox:  
1) Simple, very few source code, No XML, only 1 Annotation. Easy to learn and use.  
2) The Java-Based configuration is simpler and easier to use than Spring or Guice's Java configuration.  
3) jBeanBox is a full function IOC/AOP tool supports bean life cycle, multiple contexts.  

Key Feature of jBeanBox:  
* Use Java Configurations (called "BeanBox") to replace XML , configurations can be created/modified at runtime.
* Bean instance lazy initialization (Similar like Guice)
* SingleTon/Prototype support, SingleTon Cache (Similar like Spring)
* AOP & AspectJ support
* Multiple injection mechanisms (Similar like Spring)  
   Push type(No need source): Value injection, Bean injection, Constructor injection, Static factory injection, Bean factory injection  
   Pull type(Need source code): @InjectBox Annotation  
* multiple contexts support (Similar like create multiple "ApplicationContexts" in Spring)  
* Bean life cycle support(postConstruction & preDestory)   
* init method allow create instance manually to achieve Java type safe(like Spring's Java configuration), and can mixed with normal injection. 
 
How to use jBeanBox?  
Download jbeanbox-core-x.x.jar or jbeanbox-core-x.x-sources.jar and below 4 jars put them in your project lib folder:  
http://central.maven.org/maven2/aopalliance/aopalliance/1.0/aopalliance-1.0.jar  
http://central.maven.org/maven2/asm/asm/3.3.1/asm-3.3.1.jar  
http://central.maven.org/maven2/org/aspectj/aspectjrt/1.8.9/aspectjrt-1.8.9.jar  
http://central.maven.org/maven2/cglib/cglib/2.2.2/cglib-2.2.2.jar  
You can use Maven to download these jars by run "mvn -f pom.xml dependency:copy-dependencies".

How to import jBeanBox project into Eclipse?  
1)install JDK6+, Git bash, Maven, on command mode, run:  
2)git clone https://github.com/drinkjava2/jBeanBox  
3)cd jBeanBox  
4)mvn eclipse:eclipse  
5)mvn test  
6)Open Eclipse, "import"->"Existing Projects into Workspace", select jBeanBox folder, there are 2 module projects   "jbeanbox-core" and "jbeanbox-example" will be imported in Eclipse.  

===
A basic introduction of how to use jBeanBox:  
Example 1 - Basic Injection 
```
public class Order{
  private Company company  //getter & setter...
}

public class Company{
  private String name;  //getter & settter...	
}

//Configuration class, equal to XML in Spring
public class OrderBox extends BeanBox {
	{  
	  setProperty("company", CompanyBox.class);
	}
	
	public static class CompanyBox extends BeanBox {
		{
			setClassOrValue(Company.class);
			setProperty("name", "Pet Store");
		}
	}
}

public class Tester {
	public static void main(String[] args) {
		Order order = BeanBox.getBean(Order.class);
		System.out.println("Order bean is a SingleTon? " + (order == new OrderBox().getBean()));//true
	}
} 
```

Example 2 - AOP & Aspectj demo
```
public class Tester {
	private Iitem item;

	public void setItem(Iitem item) {
		this.item = item;
	}

	public void doPrintItem() {
		item.doPrint();
	}

	public static void main(String[] args) {
		BeanBox advice = new BeanBox(AOPLogAdvice.class).setProperty("name", "AOP Logger");
		BeanBox.defaultContext.setAOPAround("examples.example2_aop.\\w*", "doPrint\\w*", advice, "doAround");
		
		BeanBox advice2 = new BeanBox(AspectjLogAdvice.class).setProperty("name", "AspectJ Logger");
		BeanBox.defaultContext.setAspectjAfterReturning("examples.example2_aop.\\w*", "doPrint\\w*", advice2, "doAfterReturning");

		Tester t = new BeanBox(Tester.class) {
			{ setProperty("item", ItemImpl.class);
			}
		}.getBean();
		t.doPrintItem();
	}
}
```

Example 3 - @InjectBox &  multiple Contexts
```
public class Tester {
	@InjectBox(A.StrBox.class)
	String s1;// Use StrBox.class as configuration

	@InjectBox(A.class)
	String s2;// Use A.StringBox.class (or A.StringBox2.class depends context setting)

	@InjectBox(B.class)
	String s3;// Use B$S3Box.class (or B$S3Box2.class)

	@InjectBox
	C c4;// Use CBox.class (or CBox2.class)

	@InjectBox
	String s5;// Use TesterBox$StringBox.class (or TesterBox$StringBox2.class)

	@InjectBox(required = false) //default required is true
	D d6;// Use Config$DBox.class (or Config2$DBox2)

	@InjectBox(required = false)
	E e7;// Use Config$E7Box.class (or Config2$E7Box2)

	public String s8; // injected by field, similar like injected by Spring's XML configuration

	private String s9; // injected by setter, similar like injected by Spring's XML configuration, recommend this way

	public void setS9(String s9) {
		this.s9 = s9;
	}

	public void print() {
			//print out s1 to s9		
			System.out.println(s1);
			...
	}

	public static void main(String[] args) {
		Tester t = BeanBox.getBean(Tester.class);
		t.print();

		BeanBoxContext ctx = new BeanBoxContext(Config2.class).setBoxIdentity("Box2");
		Tester t2 = ctx.getBean(Tester.class);
		t2.print();
	}
}
```

 
Example 4 - PostConstructor and PreDestory 
```
public class Tester {
	private String name;

	public void init() {
		name = "Sam";
	}

	public void destory() {
		System.out.println("Bye " + name);
	}

	public static class TesterBox extends BeanBox {
		{
			setPostConstructor("init");
			setPreDestory("destory");
		}
	}

	public static void main(String[] args) {
		BeanBox.getBean(Tester.class);
		BeanBox.defaultContext.close();// print Bye Sam
	}
}
```
 
Example 5 - This example shows how to use jBeanBox to replace Spring's core to achieve "Declarative Transaction".
This example integrated "C3P0" + "JDBCTemplate" + "Spring Declarative Transaction Service"  but did not use Spring's IOC/AOP core, a weird combination but target is clear: "To eliminate XML".

```
public class TesterBox extends BeanBox {
	static {
		BeanBox.defaultContext.setAOPAround("examples.example5_transaction.Test\\w*", "insert\\w*", new TxInterceptorBox(), "invoke");
	}

	static class DSPoolBeanBox extends BeanBox {
		{
			setClassOrValue(ComboPooledDataSource.class);
			setProperty("jdbcUrl", "jdbc:mysql://127.0.0.1:3306/test?user=root&password=yourPWD&useUnicode=true&characterEncoding=UTF-8");
			setProperty("driverClass", "com.mysql.jdbc.Driver");//DB driver
			setProperty("maxPoolSize", 10);
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

public class Tester {
	@InjectBox
	private JdbcTemplate dao;

	public void insertUser() {
		dao.execute("insert into users values ('User1')");
		int i = 1 / 0; //Throw a runtime Exception to roll back transaction
		dao.execute("insert into users values ('User2')");
	}

	public static void main(String[] args) {
		Tester tester = BeanBox.getBean(Tester.class);
		tester.insertUser();
	}
}
```

Example 6 - use init method to create bean instance to achieve Java type safe, it supports method name refactor in IDE,  
below configuration do the same thing like example5 but use init method instead:
```
public class TesterBox extends BeanBox {
	static {
		BeanBox.defaultBeanBoxContext.setAOPAround("examples.example6_type_safe.Test\\w*", "insert\\w*",
				new TxInterceptorBox(), "invoke");
	}

	static class DSPoolBeanBox extends BeanBox {// Type-unsafe and type-safe configurations can mixed use.
		{
			setProperty("driverClass", "com.mysql.jdbc.Driver");
			setProperty("jdbcUrl", "jdbc:mysql://127.0.0.1:3306/test?user=root&password=root888");
		}

		public DataSource init() {
			ComboPooledDataSource ds = new ComboPooledDataSource();
			ds.setMaxPoolSize(10);
			ds.setCheckoutTimeout(2000);
			return ds;
		}
	}

	static class TxManagerBox extends BeanBox {
		public DataSourceTransactionManager init() {
			DataSourceTransactionManager dm = new DataSourceTransactionManager();
			dm.setDataSource((DataSource) new DSPoolBeanBox().getBean());
			return dm;
		}
	}

	static class TxInterceptorBox extends BeanBox {// Advice
		public TransactionInterceptor init() {
			Properties props = new Properties();
			props.put("insert*", "PROPAGATION_REQUIRED");
			return new TransactionInterceptor((DataSourceTransactionManager) new TxManagerBox().getBean(), props);
		}
	}

	public static class JdbcTemplateBox extends BeanBox {
		public JdbcTemplate init() {
			return new JdbcTemplate((DataSource) new DSPoolBeanBox().getBean());
		}
	}

}

```

