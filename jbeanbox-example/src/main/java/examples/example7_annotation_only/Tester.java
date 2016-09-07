package examples.example7_annotation_only;

import net.sf.jbeanbox.BeanBox;

/**
 * This example show how to only use annotation to build object graph <br/>
 * In this tool there are 3 kind of configurations and can mixed use at same time:<br/>
 * 
 * 1. Normal BeanBox configuration <br/>
 * 2. Java Type Safe configuration <br/>
 * 3. Annotation configuration <br/>
 * 
 * @author Yong Zhu
 * @since 2.4
 */

public class Tester {

	public static void main(String[] args) {
		AA a = BeanBox.getBean(AA.class);
		a.print();
	}
}