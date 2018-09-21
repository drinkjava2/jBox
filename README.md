# jBeanBox 
**License:** [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)  

jBeanBox是一个微形但功能较齐全的IOC/AOP工具，除了引入的第三方库之外，它的核心只有十多个类，源码只有1500行左右。它运用了“Box”编程模式，利用纯粹的Java类作为配置。jBeanBox运行于JDK1.6或以上。  
jBeanBox的开发目的是要克服其它IOC/AOP工具的一些问题：   
1. Spring: 源码臃肿，Java方式的配置不灵活，在动态配置、配置的继承上有问题、启动慢、非单例模式时性能极差。  
2. Guice: 源码略臃肿(200个类)，使用不太方便，对Bean的生命周期支持不好。  
3. Feather:源码极简(几百行)，但功能不全，只是一个DI工具，不支持AOP。  
4. Dagger: 源码略臃肿(300个类)，编译期静态注入，使用略不便,不支持AOP。
5. Genie: 这是ActFramework的内核，只是DI工具，不支持AOP。  

### 如何在项目中使用jBeanBox?  
手工下载jbeanbox-2.4.8.jar放到项目的类目录，或在pom.xml中加入以下配置：  
```
<dependency>
    <groupId>com.github.drinkjava2</groupId>
    <artifactId>jbeanbox</artifactId>
    <version>2.4.8</version> <!--或Maven最新版-->
</dependency>
``` 
jBeanBox不依赖于任何第三方库，为避免包冲突，它将用到的CGLIB等第三方库以源码内嵌方式包含在项目中。
jBeanBox的jar包尺寸较大，约为750K, 如果用不到AOP功能，可以只使用它的DI内核，称为"jBeanBoxDI", 只有49k大小，将上面artifactId中的jbeanbox改成jbeanboxdi即可。jBeanBoxDI项目详见jbeanboxdi子目录。

### 第一个jBeanBox演示：  
以下演示了9种不同的注入方式：
```
public class HelloWorld {
  public static class User {
    String name;    
    
    public User() {   }    
    
    @VALUE("User1")  
    public User(String name) { this.name = name; }   
    
    void setName(String name) { this.name = name; } 
    
    void init() {this.name = "User6";}    
    
    @PreDestroy 
    void end() {this.name= "User9";}
  }

  public static class UserBox extends BeanBox {
     Object create() {return new User("User2");}
  }

  public static class H7 extends UserBox {{setAsValue("User7");}}
 
  public static void main(String[] args) {
    User u1 = JBEANBOX.getInstance(User.class);
    User u2 = JBEANBOX.getBean(UserBox.class);
    User u3 = JBEANBOX.getBean(new BeanBox().injectConstruct(User.class, String.class, value("User3")));
    User u4 = JBEANBOX.getBean(new BeanBox(User.class).injectValue("name", "User4" ));
    User u5 = JBEANBOX
        .getBean(new BeanBox(User.class).injectMethod("setName", String.class, value("User5")));
    User u6 = JBEANBOX.getBean(new BeanBox().setBeanClass(User.class).setPostConstruct("init"));
    
    BeanBoxContext ctx = new BeanBoxContext(); 
    Interceptor aop=new MethodInterceptor() { 
      public Object invoke(MethodInvocation invocation) throws Throwable { 
        invocation.getArguments()[0]="User8";
        return invocation.proceed();
      }
    };
    User u7 = ctx.bind(String.class, "7").bind("7", H7.class)
      .getBean(ctx.getBeanBox(User.class).addMethodAop(aop, "setName",String.class).injectField("name", autowired())); 
    System.out.println(u1.name); //Result: User1
    System.out.println(u2.name); //Result: User2
    System.out.println(u3.name); //Result: User3
    System.out.println(u4.name); //Result: User4
    System.out.println(u5.name); //Result: User5
    System.out.println(u6.name); //Result: User6
    System.out.println(u7.name); //Result: User7
    u7.setName("");
    System.out.println(u7.name); //Result: User8
    ctx.close();
    System.out.println(u7.name); //Result: User9 
  }
} 
```
这个例子的输出结果是依次打印出“User1” 、“User2”...到“User9”。下面遂一解说：
1. 利用了@VALUE("User1")注解，进行了构造器注入
2. UserBox是一个纯jBeanBox的Java配置类，它的create方法手工生成了一个User对象。
3. 第三个是动态生成一个BeanBox配置，动态配置它的构造器注入，注入值为"User3"。
4. 第四个也是动态配置，演示了字段注入，注入值为常量"User4".
5. 第五个是方法注入的演示，注入参数依次为：方法名、参数类型们、实际参数们。
6. 第六个是setPostConstruct注入，等效于@PostConstruct注解，即Bean生成后立即执行的方法为init()方法。
7. 第七个比较复杂，ctx是一个新的上下文实例，它先获取User.class的固定配置，然后给它的setName方法添加一个AOP切面，然后注入"name"字段为autowired类型，也就是说String类型，不过在此之前String类被绑定到字符串"7",字符串"7"又绑定到H2.class，H7又继承于UserBox，UserBox又返回"User2"，然而都是浮云，因为H7本身被配置成一个值类型"User7"，于是最后输出结果是“User7”。
8. 第八个比较简单，因为setName方法被添加了一个AOP拦截器，参数被改成了"User8"。
9. 第九个是因为ctx这个上下文结束，所有单例被@PreDestroy标注的方法会执行，这是一个标准JSR330注解。

上例除了一头一尾外，主要演示了jBeanBox的Java方法配置，Java方法即可以动态执行，也可以在定义好的BeanBox类中作为固定配置执行，固定的配置可以打下配置的基调，当固定配置需要变动时可以用同样的Java方法来进行调整(因为本来就是同一个BeanBox对象)甚至临时创建出新的配置，所以jBeanBox同时具有了固定配置和动态配置的优点。  

### jBeanBox注解方式配置
jBeanBox不光支持Java方式配置，还支持注解方式配置,它支持以下注解：
@INJECT  类似于JSR中的@Inject注解，但允许添加目标类作为参数， 
@POSTCONSTRUCT  等同于JSR中的@PostConstruct注解  
@PREDESTROY  等同于JSR中的@PreDestroy注解  
@VALUE  类似于Spring中的@Value注解  
@PROTOTYPE  等同于Spring中的@Prototype注解  
@AOP 用于自定义AOP注解，详见AOP一节  

jBeanBox还能自动识别并支持以下JSR及Spring的注解：  
JSR的注解：@PostConstruct, @PreDestroy, @Inject, @Singleton, @scope(“prototype”), @scope(“singleton”)  
Spring的注解：@Autowired @Prototype  

因为注解方式配置大家都非常熟悉，这里就不作详细介绍了，在jBeanBox\test目录下能找到一个"AnnotationInjectTest.java"文件，演示了各种注解方式配置的使用。另外还可以调用BeanBoxContext.setGlobalNextAllowSpringJsrAnnotation(false)去禁用JSR、Spring注解，可以调用BeanBoxContext.setGlobalNextAllowAnnotation(false)去禁用所有注解(也就是说只允许Java方式配置了)。

关于注解方式配置，jBeanBox与其它IOC工具不同点在于：它不支持@Qualifer、@Name、@Provider这三个JSR330注解，这是因为作者认为这3个注解是一种反模式，在jBeanBox中可以用更简单的方式替代，例如：  
```
@Inject @Named("JDBC-URL")  private String url;
在jBeanBox中可以用以下方式替代:
@INJECT(JDBC_URL.class)  private String url; //其中JDBC_URL.class是一个BeanBox类

又如：
@Named("p") public class Person {}
在jBeanBox中看来，Person类已经有了唯一的ID: Person.class, 无需再定义一个多余的“P”作为ID，所有静态定义的类，它的类本身就是唯一的ID。jBeanBox对于静态定义的类，默认均为单例类，所以每次ctx.getBean(Person.class)都会获得同一个单例对象。  
```
@Named的问题是它是字符串类型的，无法利用IDE快速定位到配置文件，当项目配置很多时，不利于维护。  
要注意的是，如果是手工动态创建的BeanBox配置，默认均为非单例类，如果用setSingleton(true)方法硬改成单例，问题来了，它的ID是什么? 很简单，它的唯一ID就是这个动态创建的配置本身。BeanBox box1=new BeanBox(A.class).setSingeton(true), 则每次ctx.getBeanBox(box1)就会获得同一个A类型的单例对象。jBeanBox是一个无需定义Bean ID的IOC工具。当然，也可以用ctx.bind("id1",box1)，则相当于手工给它绑定了一个ID。jBeanBox没有自动扫描、预装配之类的功能，所以它的启动非常快速。但是如果有人需要自动扫描、预装配单例之类的功能，必须手工编写这样的工具类，并利用bind方法来给这些单例赋予ID，ID可以为任意对象类型，但必须全局唯一。 

### jBeanBox的Java方式配置
示例一只是笼统演示了一下jBeanBox的Java方式配置，现在再回过头来详细介绍一下它的Java方式配置：
* setAsValue(Object) 将当前BeanBox配置成一个常量值，等同于setTarget(Obj)+setPureVale(true)
* setPrototype(boolean) 如参数为true时表示它是一个非单例，与setSingleton方法作用相反
* injectConstruct(Class<?>, Object...) 设定构造器注入，参数分别是类、构造器参数类型们、参数们
* injectMethod(String, Object...) 设定某个方法注入，参数分别是方法名、参数类型们、参数们
* addAopToMethod(Object, Method) 对某个方法添加AOP，参数分别是AOP类或实例、方法
* addMethodAop(Object, String, Class<?>...) 对某个方法添加AOP，参数分别是AOP类或实例、方法名、参数类型们
* addBeanAop(Object, String) 对整个Bean添加AOP,参数分别是AOP类或实例、方法规则(如"setUser*")，
* setPostConstruct(String) 设定一个PostConstruct方法名，效果等同与@PostConstruct注解
* setPreDestroy(String) 设定一个PreDestroy方法名，效果等同与@PreDestroy注解
* injectField(String, BeanBox) 注入一个字段，参数是字段名、BeanBox实例，它的等效注解是@INJECT 
* setProperty(String, Object) 等同于injectValue方法
* injectValue(String, Object) 注入一个字段，参数是字段名、对象实例，可与它类比的注解是@VALUE 
* setTarget(Object) 注定当前Bean的目标，另外当bind("7",User.class)时，setTarget("7")就等同于setTarget(User.class)
* setPureValue(boolean) 表示target不再是目标了，而是作为纯值返回，上行的"7"就会返回字符串"7"
* setBeanClass(Class<?>) 设定当前BeanBox的最终目标类，所有的配置都是基于这个类展开
* setSingleton(Boolean) 与setPrototype作用相反
* setConstructor(Constructor<?>) 设定一个构造器
* setConstructorParams(BeanBox[]) 设定构造器的参数，与上行联用
* setPostConstruct(Method) 设定一个PostConstruct方法，效果等同与@PostConstruct注解
* setPreDestroy(Method) 设定一个PreDestroy方法名，效果等同与@PreDestroy注解 

Java方式配置，对于BeanBox来说，还有两个特殊的方法create和config，如下示例：
```
public static class DemoBox extends BeanBox {

		public Object create(Caller v) {
			A a = new A();
			a.field1 = v.getBean(B.class);
			return c2;
		}

		public void config(Object o, Caller v) {
			(A) c).field2 = v.getBean(C.class);
		}
	}
```
上例表示DemoBox中创建的Bean是由create方法来生成，由config方法来修改。上例中的两个Caller参数可以省略，如果不需要利用这个Caller参数进行加载其它Bean的话。


### jBeanBox的AOP
jBeanBox功能大都可以用Java配置或注解配置两种方式来实现，同样地，它对AOP的支持也有两种方式：

#### Java方式AOP配置
someBeanBox.addMethodAop(Object, String, Class<?>...) 对某个方法添加AOP，参数分别是AOP类或实例、方法名、参数类型们
someBeanBox.addBeanAop(Object, String) 对整个Bean添加AOP,参数分别是AOP类或实例、方法规则(如"setUser*")
someBeanBoxContext.addGlobalAop(Object, Object, String);对整个上下文添加AOP规则，参数分别是AOP类或实例、类或类名规则、方法名规则。
以上三个方法分别对应三种不同级别的AOP规则，第一个方法只针对方法，第二个方法针对整个类，第三个方法针对整个上下文。以下是一个AOP的Java配置示例：
```
public static class AopDemo1 {
		String name;
		String address;
		String email;
        //getter & setters...
	}

	public static class MethodAOP implements MethodInterceptor { 
		public Object invoke(MethodInvocation invocation) throws Throwable {
			invocation.getArguments()[0] = "1";
			return invocation.proceed();
		}
	}

	public static class BeanAOP implements MethodInterceptor { 
		public Object invoke(MethodInvocation invocation) throws Throwable {
			invocation.getArguments()[0] = "2";
			return invocation.proceed();
		}
	}

	public static class GlobalAOP implements MethodInterceptor {
		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			invocation.getArguments()[0] = "3";
			return invocation.proceed();
		}
	}

	public static class AopDemo1Box extends BeanBox {
		{
			this.injectConstruct(AopDemo1.class, String.class, value("0"));
			this.addMethodAop(MethodAOP.class, "setName", String.class);
			this.addBeanAop(BeanAOP.class, "setAddr*");
		}
	}

	@Test
	public void aopTest1() {
		JBEANBOX.bctx().bind("3", GlobalAOP.class);
		JBEANBOX.bctx().addGlobalAop("3", AopDemo1.class, "setEm*");
		AopDemo1 demo = JBEANBOX.getBean(AopDemo1Box.class);
		demo.setName("--");
		Assert.assertEquals("1", demo.name);
		demo.setAddress("--");
		Assert.assertEquals("2", demo.address);
		demo.setEmail("--");
		Assert.assertEquals("3", demo.email);
	}
```
#### 注解方式AOP配置
注解方式AOP只有两种类型，针对方法的和针对类的，没有针对上下文的。
注解方式需要用到一个特殊的注解@AOP，它是用来自定义自已的注解用的，使用示例如下：
```
public static class Interceptor1 implements MethodInterceptor {
		public Object invoke(MethodInvocation invocation) throws Throwable {
			invocation.getArguments()[0] = "1";
			return invocation.proceed();
		}
	}

	public static class Interceptor2 implements MethodInterceptor {
		public Object invoke(MethodInvocation invocation) throws Throwable {
			invocation.getArguments()[0] = "2";
			return invocation.proceed();
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE })
	@AOP
	public static @interface MyAop1 {
		public Class<?> value() default Interceptor1.class;

		public String method() default "setNa*";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	@AOP
	public static @interface MyAop2 {
		public Class<?> value() default Interceptor2.class;
	}

	@MyAop1
	public static class AopDemo1 {
		String name;
		String address;

		public void setName(String name) {
			this.name = name;
		}

		@MyAop2
		public void setAddress(String address) {
			this.address = address;
		}
	}

	@Test
	public void aopTest1() {
		AopDemo1 demo = JBEANBOX.getBean(AopDemo1.class);
		demo.setName("--");
		Assert.assertEquals("1", demo.name);
		demo.setAddress("--");
		Assert.assertEquals("2", demo.address);
	}
```
本文所说的AOP是针对Aop alliance联盟标准的接口来说的，它已经被包含在jBeanBox中，无需再单独引入(当然重复引入也不会有问题)。Aop alliance联盟标准是比较有用的一个接口，实现了各种AOP实现之间的互换性，基于它，jBeanBox可以替换掉Spring的内核而使用它的声明式事务，这种互换性能够实现的前提就是因为Spring的声明式事务实现(TransactionInterceptor)也实现了Aop alliance联盟标准接口。

### 关于循环依赖
jBeanBox具备循环依赖检测功能，如果发现循环依赖注入(如A构造器中注入B,B的构造器中又需要注入A），将会抛出运行时异常。
但是，以下这种字段或方法中出现的循环依赖注入在jBeanBox中是支持的：
```
public static class A {
		@Inject
		public B b;
	}

public static class B {
		@Inject
		public A a;
	}

A a = JBEANBOX.getBean(A.class);
Assert.assertTrue(a == a.b.a);//true
```

### jBeanBox的性能
以下为jBeanBox的性能与其它IOC工具的对比(只对比DI注入功能，搭建一个由6个对象组成的实例树），可见jBeanBox创建非单例的速度大约为Guice的一半，但依然要比Spring快得多，是Spring的45倍左右。测试程序详见：[di-benchmark]（https://github.com/drinkjava2/di-benchmark)
```
Runtime benchmark, fetch new bean for 500000 times:
---------------------------------------------------------
                     Vanilla|    31ms
                       Guice|  1154ms
                     Feather|   624ms
                      Dagger|   312ms
                       Genie|   609ms
                        Pico|  4555ms
              jBeanBoxNormal|  2075ms
            jBeanBoxTypeSafe|  2371ms
          jBeanBoxAnnotation|  2059ms
     SpringJavaConfiguration| 92149ms
     SpringAnnotationScanned| 95504ms
     
     
Split Starting up DI containers & instantiating a dependency graph 4999 times:
-------------------------------------------------------------------------------
                     Vanilla| start:     0ms   fetch:     0ms
                       Guice| start:  1046ms   fetch:  1560ms
                     Feather| start:     0ms   fetch:   109ms
                      Dagger| start:    46ms   fetch:   173ms
                        Pico| start:   376ms   fetch:   217ms
                       Genie| start:   766ms   fetch:   247ms
              jBeanBoxNormal| start:    79ms   fetch:   982ms
            jBeanBoxTypeSafe| start:     0ms   fetch:   998ms
          jBeanBoxAnnotation| start:     0ms   fetch:   468ms
     SpringJavaConfiguration| start: 51831ms   fetch:  1834ms
     SpringAnnotationScanned| start: 70712ms   fetch:  4155ms

Runtime benchmark, fetch singleton bean for 5000000 times:
---------------------------------------------------------
                     Vanilla|    47ms
                       Guice|  1950ms
                     Feather|   624ms
                      Dagger|  2746ms
                       Genie|   327ms
                        Pico|  3385ms
              jBeanBoxNormal|   188ms
            jBeanBoxTypeSafe|   187ms
          jBeanBoxAnnotation|   171ms
     SpringJavaConfiguration|  1061ms
     SpringAnnotationScanned|  1045ms
```
虽然IOC工具大多应用在单例场合，性能大家都差不多（因为从缓存中取)，但是如果遇到必须生成非单例的场合，例如每次访问生成一个新的页面实例，这时候Spring就不够看了, 至于启动速度，则更是慢到离谱了。


以上就是对jBeanBox的介绍，没有别的文档了，因为毕竟它的核心源码也只有1千多行，有问题去看看它的源码可能更简单一些。  

更多关于jBeanBox的用法还可以在jSqlBox项目中看到它的运用(数据源的配置、声明式事务示例等)。