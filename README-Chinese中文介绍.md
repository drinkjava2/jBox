#jBeanBox 
jBeanBox是一个微形但功能较齐全的IOC/AOP工具，利用了Java的初始化块实现的Java配置代替XML，比目前Spring或Guice的Java配置更简单更实用。jBeanBox采用Apache License 2.0开源协议。  
其他一些IOC/AOP框架的问题：  
1）Spring，HiveMind及其他一些利用XML作为配置文件的IOC/AOP框架：XML不支持类名称拼写检查和IDE重构，很难在运行时更改配置。(从Spring3.0开始使用一种基于Java的配置来取代XML，但Java配置与与注解混用，复杂且不支持配置的继承重用、动态变更配置。我的看法是，作为一个IOC/AOP工具来说，Spring过于复杂了。)  
2）Guice和其他依赖于注解的IOC/AOP项目：注解是一种拉式注入，入侵性强，不支持第三方库，IOC/AOP工具不应该完全依赖于更改Java源码，而且Guice只是一个DI工具，不具备Bean生命周期管理功能。  

jBeanBox的特点：  
1）简单，很少的源码(不到2000行)实现了所有的IOC/AOP功能，没有XML，只有1个注解(InjectBox)。学习曲线低、易维护、易扩充和移植。  
2）使用Java来代替XML，其实现比Spring或Guice的Java配置更简单实用，支持配置的继承重用、运行期动态创建和修改。  
3) 与Spring内核的功能重叠面多，Spring配置可以很容易移植到jBeanBox上，Spring的一些服务如声明式事务可以抽取出来在jBeanBox上使用。  
4) 是一个全功能的IOC/AOP工具，而不仅仅只是一个DI工具，旨在小项目中全部或部分取代Spring IoC/AOP内核，其主要功能有：  
*以Java初始块为基础的纯Java配置类（第一种配置方式）来代替XML，简单易用。  
*以Java方法回调为基础的Java配置类(第二种配置方式), 实现完全的Java类型安全和IDE重构支持。  
*基于注解的配置(第三种配置方式)，整个项目只有一个@InjectBox注解，易于学习。 以上三种配置各有特点，甚至可以在同一个配置中混合使用。  
*Bean实例延迟初始化（如Guice类似）  
*单例/多例支持，默认情况下所有实例为单例（与Spring类似）; 单例缓存  
*内置AOP和AspectJ支持,切点简化为正则表达式。  
*多种注射机制：  
推式注入:值注入，实例注入，构造方法注入，静态工厂注入，实例工厂注入 (与Spring传统XML注入类似)  
拉式注入：利用@InjectBox注解 (与Guice和Spring的注解注入类似），支持域成员、构造函数参数和方法参数注入，可注入常量。  
*以约定方式寻找配置，这是jBeanBOx的一个主要特点  
*多上下文支持（除了默认全局范围上下文外，支持创建多个上下文，类似于Spring中创建多个ApplicationContext实例）  
*Bean生命周期管理（postConstruction和preDestory方法回调）  

jBeanBox的缺点：  
比较新，缺少足够的测试。设置AOP时，目标类不能是final类(因采用了CGLIB代理)。  
 
如何在项目中使用jBeanBox?  
在pom.xml加入以下配置：  
```
<dependency>
    <groupId>com.github.drinkjava2</groupId>
    <artifactId>jbeanbox</artifactId>
    <version>2.4.1</version>
</dependency>
```

如何将jBeanBox项目导入Eclipse?  
1)安装JDK6以上版本、 Git bash、 Maven, 在命令行下运行：  
2)git clone https://github.com/drinkjava2/jBeanBox  
3)cd jBeanBox  
4)mvn eclipse:eclipse  
5)mvn test  
6)打开Eclipse, 按"import"->"Existing Projects into Workspace", 选中jBeanBox目录, 即可将两个子项目"jbeanbox-core"和"jbeanbox-example"导入，注意导入时不要勾选“Copy to project folder”。  

#jBeanBox使用示例：  
示例1 - HelloWorld 第一个IOC注入演示  
	下面这个简单程序演示了jBeanBox最基本的两个特点: 1)以约定方式(类名+Box)寻找配置 2)配置写在Java类初始块中。 配置类的查找方式有很多种， 将配置类写在目标类的内部只是其中一种方式。
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


示例2 - 基础jBeanBox注入

```
public class Order{ //order类
  private Company company  
  //getters & setter ...
}

public class Company{ // Company类
  private String name;  
 //getters & setters ...	
}

public class OrderBox extends BeanBox {//OrderBox为BeanBox子类，这是一个配置文件，用来代替XML
	{   
          //setPrototype(false);  //默认即为单例类，如果设为true将每次创建一个新实例
          //setClassOrValue(Order.class); //设定目标类，如果用getBean调用可以不加这句	
	  setProperty("company", CompanyBox.class); //设定要注入的对象,可以是目标类，也可以是一个BeanBox配置类	 
	}
	
    public static class CompanyBox1 extends BeanBox {
        {
            setClassOrValue(Company.class);
            setProperty("name", "Pet Store1");
        }
    }

    public static class CompanyBox extends CompanyBox1 {//配置的继承
        {
            setProperty("name", "Pet Store2");//属性的覆盖
        }
    } 
}

public class Tester {
	public static void main(String[] args) {
	   Order order = BeanBox.getBean(Order.class); //获取实例, 默认为单例
	   System.out.println("Order bean is a SingleTon? " + (order == BeanBox.getBean(Order.class)));//true
	}
} 
```
为节省篇幅，一些java类以及静态工厂、实例工厂演示未在此示例中列出，请自行翻看jbeanbox-eaxmple项目源码，下同。

 示例3： AOP & Aspectj演示，为了与Spring兼容，此项目已集成了AOP联盟接口和Aspectj接口支持，但是切点只支持Java正则表达式一种方式。
("AOPLogAdvice", "AspectjLogAdvice"源码此处略）
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
		BeanBox.defaultContext.setAspectjAfterReturning("examples.example2_aop.\\w*", "doPrint\\w*", advice2,
 "doAfterReturning");

		Tester t = new BeanBox(Tester.class).setProperty("item", ItemImpl.class).getBean();
		t.doPrintItem();
	}
}
```
BeanBox.defaultContext是个单例类全局变量，对于无需创建多个上下文实例的小型项目可以直接使用这个全局实例变量以简化编码。

示例4: @injectBox注解和上下文演示  
 此项目有且仅有一个注解@injectBox，注入1到7为注解注入，属于拉式注入，注入8和9为传统无侵入的推式注入。可以看出，注解的引入可简化源码，提高开发效率，但代价是难以理解和增加维护困难，且不支持无源码的第三方库。此示例可能比较难理解，因为配置文件比较多而且这里没有列出，请详见jbeanbox-example/src/main/java/examples/example3_annotation目录。基本原理是在注入时，首先在类的内外部、配置文件中先找到对应的BeanBox配置类并注入，如找不到配置将默认按无参构造子创建实例。

```
public class Tester {
	@InjectBox(A.StrBox.class)
	String s1;// Use StrBox.class, 推荐

	@InjectBox(A.class)
	String s2;// Use A.StringBox.class (or A.StringBox2.class, 2 to 8 depends context setting)

	@InjectBox(B.class)
	String s3;// Use B$S3Box.class

	@InjectBox
	C c4;// Use CBox.class, 推荐, 一个类配一个Box

	@InjectBox
	String s5;// Use TesterBox$StringBox.class

	@InjectBox(required = false)
	D d6;// Use Config$DBox.class (or Config2$DBox2)

	@InjectBox(required = false)
	E e7;// Use Config$E7Box.class (or Config2$E7Box2)

	private String s8; // injected by field, not suitable for Proxy bean

	private String s9; // injected by setter method, 推荐

	public void setS9(String s9) {
		this.s9 = s9;
	}

	public void print() {
		System.out.println(s1);
		System.out.println(s2);
		System.out.println(s3);
		System.out.println((c4 == null) ? null : c4.value);
		System.out.println(s5);
		System.out.println((d6 == null) ? null : d6.value);
		System.out.println((e7 == null) ? null : e7.value);
		System.out.println(s8);
		System.out.println(s9);
		System.out.println(this);
	}

	public static void main(String[] args) {
		Tester t = BeanBox.getBean(Tester.class);
		t.print();

		BeanBoxContext ctx = new BeanBoxContext(Config2.class).setBoxIdentity("Box2");
		Tester t3 = ctx.getBean(Tester.class);
		t3.print();//不同的配置输出内容不同
	}
}
```

示例5: Bean的生命周期管理(PostConstructor和PreDestory方法回调)  
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
		BeanBox.defaultContext.close();// 打印 Bye Sam
	}
}
```

示例6: 利用jBeanBox取代Spring内核实现无XML的声明式事务  
 声明式事务是AOP的典型运用场合，基本原理是利用线程局部变量来管理连接，AOP的特点就是服务和内核是插拔式设计，内核和服务可以单独使用。Spring中提供的一些业务支持理论上都可以抽取出来在其它IOC/AOP工具上使用，如果抽取不出来，说明它绑死在Spring内核上了，这与它的设计理念是不符的。本着不重新发明轮子的原则，此示例将Spring中的声明式事务服务抽取出来，与jBeanBox整合，也就是说这一次的整合只利用了Spring的事务服务，而不使用它的IOC/AOP内核 ，很诡异的组合，但目的很明确：取消XML配置。以下是jBeanBox整合了c3p0数据池+JDBCTemplate+Spring声明式事务的一个例子，实测通过:

```
public class TesterBox extends BeanBox {//用于取代XML的JAVA配置类
	static {//在默认全局单例上下文上设置AOP事务切面
		BeanBox.defaultContext.setAOPAround("examples.example5_transaction.Test\\w*", "insert\\w*", 
new TxInterceptorBox(), "invoke");
	}

	static class DSPoolBeanBox extends BeanBox {//C3P0数据池配置，为单例，下同
		{
			setClassOrValue(ComboPooledDataSource.class);
			setProperty("jdbcUrl", "jdbc:mysql://127.0.0.1:3306/test?user=root&password=你的密码&
characterEncoding=UTF-8");
			setProperty("driverClass", "com.mysql.jdbc.Driver");// your jdbc driver name
			setProperty("maxPoolSize", 10);
		}
	}

	static class TxManagerBox extends BeanBox {//事务管理器配置，从Spring中抽取的
		{
			setClassOrValue(DataSourceTransactionManager.class);
			setProperty("dataSource", DSPoolBeanBox.class);
		}
	}

	static class TxInterceptorBox extends BeanBox {//AOP事务切面处理类，从Spring中抽取的
		{
			Properties props = new Properties();
			props.put("insert*", "PROPAGATION_REQUIRED");
			setConstructor(TransactionInterceptor.class, TxManagerBox.class, props);
		}
	}

	public static class JdbcTemplateBox extends BeanBox {//JdbcTemplate模板配置，可换成dbUtils等
		{
			setConstructor(JdbcTemplate.class, DSPoolBeanBox.class);
		}
	}
}

public class Tester {//测试类
	@InjectBox
	private JdbcTemplate dao;//注入JdbcTemplateBox配置类生成的实例

	public void insertUser() {
		dao.execute("insert into users values ('User1')");
		int i = 1 / 0; //抛出运行期错误，导致事务回滚
		dao.execute("insert into users values ('User2')");
	}

	public static void main(String[] args) {
		Tester tester = BeanBox.getBean(Tester.class);//从默认上下文获取类实例
		tester.insertUser();
	}
}
```
此示例中需要额外用到C3P0、Mysql驱动(须安装MySQL并配置)以及Spring的一些包，运行"mvn test"可自动下载并测试。  

示例7: 利用Java方法来手工生成实例。这种方式和Spring的Java配置类似，优点是实现了传统注入方式不支持的方法名重构，缺点是灵活性略差，在根据参数动态创建、修改配置和配置的继承重用上有局限性。jBeanBox支持Java方法回调和普通注入配置方式的混用。下面示例与示例5一样实现了同样的功能，但是用create回调方法来手工创建实例，用config回调方法来手工注入属性，(如运行在JAVA8下，强制类型转换可以省略)：
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
			ds.setPassword("root888");// change to your PWD
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
示例8 演示用注解来注入属性、构造函数参数和方法参数  
目前jBeanBox有三种配置方式，初始块、Java方法、注解，这三种方式各有特点，初始块最灵活，可完全替代XML，但不支持方法名重构;Java方法回调是类型安全但灵活性差，不支持配置的继承和动态修改;注解最简洁但仅适用于有源码的场合。这三种配置方法可以同时混合使用，互相补充。 参数用代号加数字指定，从0开始，如s0表示第一个String参数, i1表示第二个Integer参数,box2表示第三个BeanBox参数
```
public class Tester {
	String name1;
	String name2;

	@InjectBox(s0 = "name3")
	String name3;

	AA a4, a5;

	@InjectBox(s0 = "name1")
	public Tester(String name1, AA a4) {//a4将自动找到配置类AABox
		this.name1 = name1;
		this.a4 = a4;
	}

	@InjectBox(s0 = "name2", box1 = A5Box.class)
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

示例9 是一个简单的构造一个对象图的Benchmark测试，详细的测试已移到新项目[di-benchmark](https://github.com/drinkjava2/di-benchmark) 中，简单测试了一下，以下是测试结果：
```
Split Starting up DI containers & instantiating a dependency graph 100 times:
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

Runtime benchmark

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

配置为单例后的测试结果(只比较jBeanBox和Spring):
```
Runtime benchmark, fetch bean for 100000 times:
--------------------------------------------------
                               jBeanBoxNormal|    47ms
                             jBeanBoxTypeSafe|    31ms
                           jBeanBoxAnnotation|    78ms
                      SpringJavaConfiguration|    94ms
                      SpringAnnotationScanned|    78ms
```
作为一个源码只有2000行的小项目， 以上即为jBeanBox全部文档，如有疑问，请下载示例运行或查看源码。
