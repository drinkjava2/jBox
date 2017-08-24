# jBeanBox  
**License:** [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

jBeanBox is a micro scale IOC & AOP tool use pure java as configuration, run on Java6 or above.  

### Advantage of jBeanBox:  
1) Simple, very few source code(core sourcecode less than 3000 lines), No XML, only 2 Annotations, easy to learn and use.  
2) The Java-Based configuration is easy use.  
3) jBeanBox is a full function IOC/AOP tool supports bean life cycle, multiple contexts. 
4) Start quick.

Key Feature of jBeanBox:  
* Use Java initialization block Configurations (called "BeanBox") to replace XML , configurations can be created/modified at runtime.
* Bean instance lazy initialization, SingleTon/Prototype support, SingleTon Cache. 
* AOP support
* Multiple injection mechanisms (Similar like Spring)  
   Push type(No need source): Value injection, Bean injection, Constructor injection, Static factory injection, Bean factory injection  
   Pull type(Need source code): @InjectBox Annotation  
* Multiple contexts support (Similar like create multiple "ApplicationContexts" in Spring)  
* Bean life cycle support(postConstruction & preDestory method callback)   
* Java method callback configurations to achieve Java type safe(similar like Spring's Java configuration), and can mixed use with other 2 configurations. 
 
### Problems of jBeanBox: 
Pretty new, need do more test, only support CGLib proxy, for point-cut definition only support Java Regex or @AopAround annotation.
 
### How to use jBeanBox?  
Download jbeanbox-2.4.4.jar put into lib folder, or add below lines in pom.xml file:  
```
<dependency>
    <groupId>com.github.drinkjava2</groupId>
    <artifactId>jbeanbox</artifactId>
    <version>2.4.4</version>
</dependency>
```
jBeanBox only has no any other 3rd party dependency. 

### How to import jBeanBox project into Eclipse (for developer)?  
1)install JDK1.6+, Git bash, Maven3.3.9+, on command mode, run:  
2)git clone https://github.com/drinkjava2/jBeanBox  
3)cd jBeanBox  
4)mvn eclipse:eclipse  
5)Open Eclipse, "import"->"Existing Projects into Workspace", select jBeanBox folder.   

===
### Examples show how to use jBeanBox:  
Example 1 - Hello World
```
import com.github.drinkjava2.BeanBox;
public class HelloWorld {
  private String field1;
  public static class HelloWorldBox extends BeanBox {
    {
      this.setProperty("field1", "Hello World!");
    }
  }
  public static void main(String[] args) {
    HelloWorld h = BeanBox.getBean(HelloWorld.class);
    System.out.println(h.field1); //print "Hello World!"
  }
}
```
This HelloWorld shows 2 key features of jBeanBox:  
   1)Configuration be written in Java initialization block.  
   2)Configuration classes be searched following some conventions usually "classname+Box", and usaually in same folder of target class or just write inside of target class.  
 
Example 2 - Basic Injection (Detail source code see test folder)  
```
public class Order{
  private Company company  
  //getter & setter...
}

public class Company{
  private String name;  
  //getter & settter...  
}

//Configuration class, equal to XML in Spring
public class OrderBox extends BeanBox {
  {  
      //setPrototype(false);   //default is singleton, this line can omit
      //setClassOrValue(Order.class); //if called by getBean(), this line can omit  
      setProperty("company", CompanyBox.class);
  }
  
  public static class CompanyBox1 extends BeanBox {
    {
      setClassOrValue(Company.class);
      setProperty("name", "Pet Store1");
    }
  }
  
  public static class CompanyBox extends CompanyBox1 {
    {
      setProperty("name", "Pet Store2"); //override property
    }
  }  
}

public class Tester {
  public static void main(String[] args) {
    Order order = BeanBox.getBean(Order.class);
    System.out.println("Order bean is a SingleTon? " + (order == BeanBox.getBean(Order.class)));//true
  }
} 
```

Example 3 - AOP & Aspectj demo
```
public class Tester {
    private Iitem item;
	public void setItem(Iitem item) {
		this.item = item;
	}

	public void doPrintItem() {
		item.doPrint();
	}

	@Test
	public void doTest() {
		BeanBox advice = new BeanBox(AOPLogAdvice.class).setProperty("name", "AOP Logger"); 
		//setAOPAround() method if last parameter is "invoke" can ignore
		BeanBox.defaultContext.setAOPAround("test.test2_aop.\\w*", "doPrint\\w*", advice, "doAround");
		BeanBox.defaultContext.setAOPBefore("test.test2_aop.\\w*", "doPrint\\w*", advice, "doBefore");
		BeanBox.defaultContext.setAOPAfterReturning("test.test2_aop.\\w*", "doPrint\\w*", advice, "doAfterReturning");
		BeanBox.defaultContext.setAOPAfterThrowing("test.test2_aop.\\w*", "doPrint\\w*", advice, "doAfterThrowing");
		
  		Tester t = new BeanBox(Tester.class) {
		}.setProperty("item", ItemImpl.class).getBean();
		t.doPrintItem();
	}

	public static void main(String[] args) {
		new Tester().doTest();
	}
}
``` 
AOPLogAdvice.java is not shown here, can find it in test folder.

Example 4 - @InjectBox &  multiple Contexts
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

  public String s8; // directly inject on field 

  private String s9; // injected by setter, recommend

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
    t2.print(); //print result is different because configuration different
  }
}
```
 
Example 5 - PostConstructor and PreDestory 
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
 
Example 6 - This example shows how to use jBeanBox to replace Spring's core to achieve "Declarative Transaction".
This example integrated "C3P0" + "JDBCTemplate" + "Spring Declarative Transaction Service"  but did not use Spring's IOC/AOP core, a weird combination but target is clear: "To eliminate XML".

```
public class TesterBox extends BeanBox {
  static {
    BeanBox.defaultBeanBoxContext.setAOPAround("examples.example5_transaction.Test\\w*", "insert\\w*",
        new TxInterceptorBox(), "invoke");
  }

  static class DSPoolBeanBox extends BeanBox {
    {
      setClassOrValue(ComboPooledDataSource.class);
      setProperty("jdbcUrl", "jdbc:mysql://127.0.0.1:3306/test?user=root&password=yourpwd");
      setProperty("driverClass", "com.mysql.jdbc.Driver");
      setProperty("maxPoolSize", 10);
      setProperty("CheckoutTimeout", 2000);
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

public class Tester {

  @InjectBox
  private JdbcTemplate dao;

  public void insertUser() {
    dao.execute("insert into users values ('User1')");
    // int i = 1 / 0; // Throw a runtime Exception to roll back transaction
    dao.execute("insert into users values ('User2')");
  }

  public static void main(String[] args) {
    Tester tester = BeanBox.getBean(Tester.class);
    tester.insertUser();
  }

}
```

Example 7 - Java type safe type configuration, is very similar to Spring's Java based configuration, advantage of this kind configuration is it's type safe and support method name IDE refactor, disadvantage is not flexible. Below configuration do the same thing like example6 but use java type safe configuration instead, in this example mixed used with 2 different configurations: Type-Safe and Initilization block.
```
public class TesterBox extends BeanBox {
    static {
        BeanBox.defaultBeanBoxContext.close();// clean up
        BeanBox.defaultBeanBoxContext.setAOPAround("examples.example6_type_safe.Test\\w*", "insert\\w*",
                new TxInterceptorBox(), "invoke");
    }

    static class DSPoolBeanBox extends BeanBox {// Type-unsafe and type-safe configurations can mixed use.
        public DataSource create() {
            ComboPooledDataSource ds = new ComboPooledDataSource();
            ds.setUser("root");
            return ds;
        }

        public void config(ComboPooledDataSource ds) {
            ds.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test");
            ds.setPassword("yourPassword");
            ds.setCheckoutTimeout(2000);
        }

        {    setProperty("driverClass", "com.mysql.jdbc.Driver");
        }

    }

    static class TxManagerBox extends BeanBox {
        public DataSourceTransactionManager create() {
            DataSourceTransactionManager dm = new DataSourceTransactionManager();
            dm.setDataSource((DataSource) context.getBean(DSPoolBeanBox.class));
            return dm;
        }
    }

    static class TxInterceptorBox extends BeanBox {// Advice
        public TransactionInterceptor create() {
            Properties props = new Properties();
            props.put("insert*", "PROPAGATION_REQUIRED");
            return new TransactionInterceptor((DataSourceTransactionManager) context.getBean(TxManagerBox.class),
                    props);
        }
    }

    public static class JdbcTemplateBox extends BeanBox {
        public JdbcTemplate create() {
            return new JdbcTemplate((DataSource) context.getBean(DSPoolBeanBox.class));
        }
    }
}
```

Example 8 - Show annotation inject on field, constructor & method, and mixed use with other 2 configurations,   
            parameters start from 0, s0 means 1st String parameter, i1 means 2nd Integer paramerter, box2 means 3rd BeanBox paramerter (Note: I have a plan, from v2.4.2 parameter order will start from s1, s0 will be replace by s)
```
public class Tester {
  String name1;
  String name2;

  @InjectBox(s = "name3")
  String name3;

  AA a4, a5;

  @InjectBox(s1 = "name1")
  public Tester(String name1, AA a4) {// a4 automatically find AABox
    this.name1 = name1;
    this.a4 = a4;
  }

  @InjectBox(s1 = "name2", box2 = A5Box.class)
  public void injectBymethod(String name2, AA a5) {
    this.name2 = name2;
    this.a5 = a5;
  }

  public static class AA {
    public String name;
  }

  public static class AABox extends BeanBox {
    {
      this.setProperty("name", "name4");
    }
  }

  public static class A5Box extends BeanBox {
    public AA create() {
      AA aa = new AA();
      aa.name = "name5";
      return aa;
    }
  }

  public static void main(String[] args) {
    Tester t = BeanBox.getBean(Tester.class);
    System.out.println("name1=" + t.name1); // name1=name1
    System.out.println("name2=" + t.name2); // name2=name2
    System.out.println("name3=" + t.name3); // name3=name3
    System.out.println("name4=" + t.a4.name); // name4=name4
    System.out.println("name5=" + t.a5.name); // name5=name5
  }
}
```
Example 9 - A simple BenchMark test, more detail please see project "https://github.com/drinkjava2/di-benchmark"  
```
Split Starting up DI containers & instantiating a dependency graph 100 times (Prototype):
-------------------------------------------------------------------------------
                                      Vanilla| start:     1ms   fetch:     6ms
                                        Guice| start:   727ms   fetch:   747ms
                                      Feather| start:     6ms   fetch:    39ms
                                       Dagger| start:    74ms   fetch:    48ms
                                         Pico| start:   115ms   fetch:   127ms
                                        Genie| start:   658ms   fetch:    89ms
                               jBeanBoxNormal| start:     3ms   fetch:   123ms
                             jBeanBoxTypeSafe| start:     1ms   fetch:    40ms
                           jBeanBoxAnnotation| start:     1ms   fetch:   106ms
                      SpringJavaConfiguration| start:  4542ms   fetch:   621ms
                      SpringAnnotationScanned| start:  4668ms   fetch:   757ms
```

```
Runtime benchmark, fetch bean for 10000 times (Prototype):
--------------------------------------------------
                                      Vanilla|    11ms
                                        Guice|   153ms
                                      Feather|    59ms
                                       Dagger|    43ms
                                        Genie|    52ms
                                         Pico|   430ms
                               jBeanBoxNormal|  3791ms
                             jBeanBoxTypeSafe|   950ms
                           jBeanBoxAnnotation|  4603ms
                      SpringJavaConfiguration|  5003ms
                      SpringAnnotationScanned|  6331ms
```

If change configuration to Singleton, only compared jBeanBox & Spring:
```
Runtime benchmark, fetch bean for 100000 times (Singleton):
--------------------------------------------------
                               jBeanBoxNormal|    47ms
                             jBeanBoxTypeSafe|    31ms
                           jBeanBoxAnnotation|    78ms
                      SpringJavaConfiguration|    94ms
                      SpringAnnotationScanned|    78ms
```

Example 10 - A new @AopAround annotation be used to mark a method which need AOP around Interceptor：
```
	@AopAround(TxInterceptorBox.class)
	public void insertUser() {
		insertUser1();
		int count = dao.queryForObject("select count(*) from users", Integer.class);
		System.out.println(count + " record inserted");
		Assert.assertEquals(1, count);
		System.out.println(1 / 0);// Throw a runtime Exception to roll back transaction
		insertUser2();
	}
```
