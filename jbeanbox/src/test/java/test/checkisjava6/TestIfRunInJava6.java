package test.checkisjava6;

import org.junit.Test;

import com.github.drinkjava2.jbeanbox.springsrc.StringUtils;

/**
 *
 *
 * @author Yong Zhu
 * @since 2.4
 */
public class TestIfRunInJava6 {

	@Test
	public void testIfRunInJava6() {
		String javaVersion = System.getProperty("java.version");
		System.out.println("Java version=" + javaVersion);
		if (StringUtils.startsWithIgnoreCase(javaVersion, "1.6"))
			return;
		throw new AssertionError(
				"jBeanBox is forced to compile in Java6, to avoid compile in Java7 or Java8 but cause runtime mistake when run on Java6, added this unit test to force run on Java6.");
	}
}