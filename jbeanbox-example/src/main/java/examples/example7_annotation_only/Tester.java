package examples.example7_annotation_only;

import net.sf.jbeanbox.BeanBox;

/**
 * This example show how to only use annotation to build object graph <br/>
 * So far, in this project there are 3 kind of configurations:<br/>
 * 
 * 1. Normal BeanBox configuration (Example 1) <br/>
 * 2. Java Type Safe configuration (use create & config method, see example 6) <br/>
 * 3. Annotation configuration
 * 
 * @author Yong Zhu
 * @since 2016-8-28
 */

public class Tester {

	public static void main(String[] args) {
		AA a = BeanBox.getBean(AA.class);
		a.print();
	}
}