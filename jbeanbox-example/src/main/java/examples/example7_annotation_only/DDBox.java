package examples.example7_annotation_only;

import net.sf.jbeanbox.BeanBox;

public class DDBox extends BeanBox {
	{
		this.setClassOrValue(DD.class);
		this.setProperty("name", "Jerry");
	}
}
