package examples.example7_annotation_only;

import net.sf.jbeanbox.BeanBox;

/**
 * This example show how to only use annotation to build object graph <br/>
 * So far, in this project there are 3 kind of configurations:<br/>
 * 
 * 1. Normal BeanBox configuration (Example 1)
 * 2. Java Type Safe configuration (use create & config method, see example 6)
 * 3. Annotation only configuration
 * 
 * @author Yong Zhu
 * @since 2016-8-28
 */

public class Tester {

	// TODO
	public static void main(String[] args) {
		AA aa = BeanBox.getBean(AA.class);
		aa.bb.cc.d1.aa.bb.cc.d2.aa.bb.cc.d1.print();
	}
}