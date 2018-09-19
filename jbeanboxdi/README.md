## jBeanBoxDI
**License:** [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)  

jBeanBoxDI is a pure DI (dependency inject) tool, it does not support AOP, it's lightweight only few Java source code files and ~35kb jar size.  
jBeanBoxDI runs on JDK1.6 and above.

## How to use jBeanBoxDI:
Add below in pom.xml:  
```
   <dependency>
      <groupId>com.github.drinkjava2</groupId>
      <artifactId>jbeanboxdi</artifactId>
      <version>2.4.8</version> <!-- Or newest version -->
   </dependency>   
``` 


## Relationship of jBeanBoxDi and jBeanBox:  
jBeanBox = jBeanBoxDi + AOP  

jBeanBox support AOP, this makes it more useful but much fat(~500kbs) because it included CGLib and ASM source code files.  
To use jBeanBox, add below in pom.xml:  
```
   <dependency>
      <groupId>com.github.drinkjava2</groupId>
      <artifactId>jbeanbox</artifactId>
      <version>2.4.8</version> <!-- Or newest version -->
   </dependency> 
```
If you added jBeanBox in pom, no need add jBeanBoxDI again because jBeanBoxDI is a core part of jBeanBox.

## Any user manual of jBeanBoxDI?
No, jBeanBoxDI is the core fo jBeanBox, you can read jBeanBox's user manual, but skip over "AOP" chapter.

## Why I can not find any source code in jbeanboxdi folder? 
You need run "JumpOutSourceCode.bat", then source code will jump out.