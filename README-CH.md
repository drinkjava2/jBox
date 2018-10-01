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
这个例子的输出结果是依次打印出“User1” 、“User2”...到“User9”。下面遂一解释：
1. 利用了@VALUE("User1")注解，进行了构造器注入
2. UserBox是一个jBeanBox的纯Java配置类，这个Java类是一个纯粹的Java类（不象Spring中的Java配置类是一个非常特殊的类，它在运行期会产生一个代理类），可以运用类的继承、方法重写等常见设计模式。 在这个示例里它的create方法手工生成了一个User("User2")对象。
3. 第三个是动态生成一个BeanBox配置，动态配置它的构造器注入，注入值为"User3"。
4. 第四个也是动态配置，演示了字段注入，注入值为常量"User4".
5. 第五个是方法注入的演示，注入参数依次为：方法名、参数类型们、实际参数们。
6. 第六个是setPostConstruct注入，等效于@PostConstruct注解，即Bean生成后立即执行的方法为init()方法。
7. 第七个比较复杂，ctx是一个新的上下文实例，它先获取User.class的固定配置，然后给它的setName方法添加一个AOP切面，然后注入"name"字段为autowired类型，也就是说String类型，不过在此之前String类被绑定到字符串"7",字符串"7"又绑定到H2.class，H7又继承于UserBox，UserBox又返回"User2"，然而都是浮云，因为H7本身被配置成一个值类型"User7"，于是最后输出结果是“User7”。
8. 第八个比较简单，因为setName方法被添加了一个AOP拦截器，参数被改成了"User8"。
9. 第九个是因为ctx这个上下文结束，所有单例被@PreDestroy标注的方法会执行，这是一个标准JSR330注解。

上例除了一头一尾外，主要演示了jBeanBox的Java方法配置，Java方法即可以动态执行，也可以在定义好的BeanBox类中作为固定配置执行，固定的配置可以打下配置的基调，当固定配置需要变动时可以用同样的Java方法来进行调整(因为本来就是同一个BeanBox对象)甚至临时创建出新的配置，所以jBeanBox同时具有了固定配置和动态配置的优点。另外当没有源码时，例如配置第三方库的实例，这时所有的注解方式配置都用不上，唯一能用的只有Java配置方式。

上例中的value()方法是从JBEANBOX类中静态引入的全局方法，这个示例的源码位于单元测试目录下的HelloWorld.java。  

### jBeanBox注解方式配置
jBeanBox不光支持Java方式配置，还支持注解方式配置,它支持以下注解：  
@INJECT  类似于JSR中的@Inject注解，但允许添加目标类作为参数  
@POSTCONSTRUCT  等同于JSR中的@PostConstruct注解  
@PREDESTROY  等同于JSR中的@PreDestroy注解  
@VALUE  类似于Spring中的@Value注解  
@PROTOTYPE  等同于Spring中的@Prototype注解  
@AOP 用于自定义AOP注解，详见AOP一节  

jBeanBox还能自动识别并支持以下JSR及Spring的注解：  
JSR的注解：@PostConstruct, @PreDestroy, @Inject, @Singleton, @scope(“prototype”), @scope(“singleton”)  
Spring的注解：@Autowired @Prototype  

因为注解方式配置大家都非常熟悉，这里就不作详细介绍了，在jBeanBox\test目录下能找到一个"AnnotationInjectTest.java"文件，演示了各种注解方式配置的使用。另外还可以调用ctx.setAllowSpringJsrAnnotation(false)去禁用JSR、Spring注解，也可以调用ctx.setAllowAnnotation(false)去禁用所有注解(也就是说只能用Java方式配置了)。  

关于注解方式配置，jBeanBox与其它IOC工具不同点在于：它不支持@Qualifer、@Name、@Provider这三个JSR330注解，这是因为笔者认为这3个注解在jBeanBox中可以用已有注解实现，如：  
```
@Inject @Named("JDBC-URL")  private String url;
在jBeanBox中可以用以下方式替代:
@INJECT(JDBC_URL.class)  private String url; //其中JDBC_URL.class是一个BeanBox类  
或
@VALUE("$JDBC-URL")  private String url; //$JDBC-URL值可以通过配置BeanBoxContext中的ValueTranslator来解释。 

又如：
@Named("p") public class Person {}
在jBeanBox中看来，Person类已经有了唯一的ID: Person.class, 无需再定义一个多余的“P”作为ID，所有静态定义的类，它的类本身就是唯一的ID。jBeanBox对于静态定义的类，默认均为单例类，所以每次ctx.getBean(Person.class)都会获得同一个单例对象。  
```
@Named的问题是它是字符串类型的，无法利用IDE快速定位到配置文件，当项目配置很多时，不利于维护。  
jBeanBox是一个无需定义Bean ID的IOC工具，注意：如果是手工动态创建的BeanBox配置，默认均为非单例类。如果用setSingleton(true)方法硬改成单例，那问题来了，它的ID是什么? 很简单，它的唯一ID就是这个动态创建的配置实例本身。BeanBox box1=new BeanBox(A.class).setSingeton(true), 则每次ctx.getBeanBox(box1)就会获得同一个A类型的单例对象。当然，也可以用ctx.bind("id1",box1)，则相当于手工给它绑定了一个ID值"id1",可以用getBean("id1")来获取它。jBeanBox没有自动扫描、预创建单例之类的功能，所以它的启动非常快速。如果有人有自动扫描、预创建单例、预绑定ID名之类的需求，必须手工编写一个工具类来实现这个目的(jBeanBox暂不提供)，例如在程序运行开始时调用一下ctx.getBean(A.class)就会在上下文中暂存一个A的单例类，下次访问时会直接从缓存中取。

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

		public Object create(Caller caller) {
			A a = new A();
			a.field1 = caller.getBean(B.class);
			return a;
		}

		public void config(Object o, Caller caller) {
			((A) o).field2 = caller.getBean(C.class);
		}
	}
```
上例表示DemoBox中创建的Bean是由create方法来生成，由config方法来修改。create和config方法中的Caller参数可以省略，如果不需要利用这个Caller参数进行加载其它Bean的话。


### jBeanBox的AOP(面向切面编程)
jBeanBox功能大都可以用Java配置或注解配置两种方式来实现，同样地，它对AOP的支持也有两种方式：

#### Java方式AOP配置
* someBeanBox.addMethodAop(Object, String, Class<?>...) 对某个方法添加AOP，参数分别是AOP类或实例、方法名、参数类型们
* someBeanBox.addBeanAop(Object, String) 对整个Bean添加AOP,参数分别是AOP类或实例、方法规则(如"setUser＊")
* someBeanBoxContext.addGlobalAop(Object, Object, String);对整个上下文添加AOP规则，参数分别是AOP类或实例、类或类名规则、方法名规则。  
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
上面的命名规则采用“*”做为模糊匹配字符，代表任意长度、任意字符。

#### 注解方式AOP配置
注解方式AOP只有两种类型，针对方法的和针对类的，没有针对上下文的。
注解方式需要用到一个特殊的注解@AOP，它是用来自定义自已的AOP注解用的，使用示例如下：
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
本文所说的AOP是针对Aop alliance联盟标准的接口来说的，它已经被包含在jBeanBox中，无需再单独引入(当然重复引入也不会有问题)。Aop alliance联盟标准是比较有用的一个接口，实现了各种AOP实现之间的互换性，基于它，jBeanBox可以替换掉Spring的内核而使用它的声明式事务，这种互换性能够实现的前提就是因为Spring的声明式事务实现(如TransactionInterceptor)也实现了Aop alliance联盟标准接口MethodInterceptor。

jBeanBox从2.4.8版本起，AOP功能大副削减，去掉了不常用的前置、后置、异常切面功能，只保留了支持AOP alliance联盟标准接口MethodInterceptor的功能（注意在CGLIB也有一个同名的接口，不要混淆）。实现了MethodInterceptor接口的类，通常称为Interceptor,但在jBeanBox中图省事，也把它称为AOP，毕竟写成addBeanAop要比写成addBeanInterceptor简洁一些。


### 关于循环依赖
jBeanBox具备循环依赖检测功能，如果发现循环依赖注入(如A构造器中注入B,B的构造器中又需要注入A），将会抛出BeanBoxException运行时异常。
但是，以下这种字段或方法中出现的循环依赖注入在jBeanBox中是允许的：
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
### jBeanBox支持多上下文和Bean生命周期
jBeanBox支持多个上下文实例(BeanBoxContext)，每个上下文实例都是互不干拢的。例如一个User.class可以在3个上下文中各自用不同的配置方式(注解、Java)生成3个单例，这3个“单例”都是相对于当前上下文唯一的，它们的属性与各自的配置有关。  
  
JBEANBOX.getBean()方法是利用了一个缺省的全局上下文，可以用JBEANBOX.bctx()方法来获取，所以如果一个项目中不需要用到多个上下文，可以直接使用JBEANBOX.getBean()方法来获取实例，这样可以节省一行创建一个新上下文的代码。  

BeanBoxContext的每个实例都在内部维护着配置信息、单例缓存等，在BeanBoxContext实例的close方法被调用后，它的配置信息和单例被清空，当然，在清空之前，所有单例类的PreDestroy方法（如果有的话)被调用运行。所以对于需要回调PreDestroy方法的上下文来说，在关闭时不要忘了调用close方法。对于缺省的全局上下文来说就是JBEANBOX.close()方法。

BeanBoxContext的常用方法详解：
* reset() 这个静态方法重置所有静态全局配置，并调用缺省上下文实例的close方法。
* close() 先调用当前上下文缓存中单例实例的PreDestroy方法(如果有的话)，然后清空当前上下文的缓存。
* getBean(Object) 根据目标对象（可以是任意对象类型），返回一个Bean，如果找不到则抛出异常
* getInstance(Class<T>) 根据目标类T,返回一个T类型的实例, 如果找不到则抛出异常
* getBean(Object, boolean) 根据目标对象，返回一个Bean， 第二个参数为false时如果找不到则返回Empty.class
* getInstance(Class<T>, boolean) 根据目标类T,返回一个T类型的实例, 第二个参数为false时如果找不到则返回Empty.class
* bind(Object, Object) 给目标类绑定一个ID，例如：ctx.bind("A","B").bind("B".C.class)，则以后可以用getBean("A")获取C的实例
* addGlobalAop(Object, String, String) 在当前上下文环境添加一个AOP(详见AOP一节),第二个参数为类名模糊匹配规则，如"com.tom.＊"或"＊.tom"等，＊号只允许出现在头尾（可以同时出现，也可以一个不出现), 第三个参数为方法名模糊匹配规则，如"setUser＊"或"＊user"等。
* addGlobalAop(Object, Class<?>, String) 在当前上下文环境添加一个AOP，第二个参数为指定类(会匹配所有与指定类名称开头相同的类，例如指定类为a.b.C.class, 则a.b.CXX.class也会被匹配)，第三个参数为方法名模糊匹配规则。
* getBeanBox(Class<?>) 获取一个类的BeanBox实例，例如一个注解标注的类，可以用这个方法获取BeanBox实例，然后再添加、修改它的配置，这就是固定配置和动态配置的结合运用。
* setAllowAnnotation(boolean) 设定是否允许读取类中的注解，如果设为flase的话，则jBeanBox只允行使用纯Java配置方式。默认true。
* setAllowSpringJsrAnnotation(boolean) 设定是否允先读取类中JSR330/JSR350和Spring的部分注解，以实现兼容性。默认true。
* setValueTranslator(ValueTranslator) 设定对于@VALUE注解中的内容，如何解析它，例如@VALUE("#user")，系统默认返回"#user"字符串，如果需要不同的解析，例如读取property文本中的值，则需要自已设定一个实现了ValueTranslator接口的实例。  

### jBeanBox的性能
以下为jBeanBox的性能与其它IOC工具的对比(只对比DI注入功能，搭建一个由6个对象组成的实例树），可见jBeanBox创建非单例的速度比Guic慢一倍、比Spring快45倍左右。测试程序详见：[di-benchmark]（https://github.com/drinkjava2/di-benchmark)
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


以上就是对jBeanBox的介绍，没有别的文档了，因为毕竟它的核心源码也只有1千多行(第三方工具如CGLIB、JSR接口等不算在内)，有问题去看看它的源码可能更简单一些。  

更多关于jBeanBox的用法还可以在jSqlBox项目中看到它的运用(数据源的配置、声明式事务示例等)。