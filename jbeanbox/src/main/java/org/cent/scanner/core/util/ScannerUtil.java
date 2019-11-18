package org.cent.scanner.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.cent.scanner.core.consts.CommonConsts;
import org.cent.scanner.core.consts.ProtocolTypes;

/**
 * @author: cent
 * @email: 292462859@qq.com
 * @date: 2019/1/8.
 * @description:
 */
public enum ScannerUtil {
	;

	/**
	 * 扫描某个包下所有Class类
	 *
	 * @param pkg
	 *            扫描报名
	 * @return Class列表
	 */
	public static List<Class> scan(String pkg) {
		List<Class> classList = new LinkedList<Class>();
		try {
			// 包名转化为路径名
			String pathName = ClassUtil.package2Path(pkg);
			// 获取路径下URL
			Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(pathName);
			// 循环扫描路径
			classList = scanUrls(pkg, urls);
		} catch (IOException e) {
			log.error("扫描包路径出错：{}", pkg, e);
		}

		return classList;
	}

	/**
	 * 扫描多个Url路径，找出符合包名的Class类
	 *
	 * @param pkg
	 * @param urls
	 * @return Class列表
	 * @throws IOException
	 */
	private static List<Class> scanUrls(String pkg, Enumeration<URL> urls) throws IOException {
		List<Class> classList = new LinkedList<Class>();
		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			// 获取协议
			String protocol = url.getProtocol();

			if (ProtocolTypes.file.name().equals(protocol)) {
				// 文件
				String path = URLDecoder.decode(url.getFile(), CommonConsts.DEFAULT_CHARSET);
				classList.addAll(recursiveScan4Path(pkg, path));

			} else if (ProtocolTypes.jar.name().equals(protocol)) {
				// jar包
				String jarPath = URLUtil.getJarPathFormUrl(url);
				classList.addAll(recursiveScan4Jar(pkg, jarPath));
			}
		}
		return classList;
	}

	/**
	 * 递归扫描指定文件路径下的Class文件
	 *
	 * @param pkg
	 * @param filePath
	 * @return Class列表
	 */
	private static List<Class> recursiveScan4Path(String pkg, String filePath) {
		List<Class> classList = new LinkedList<Class>();

		File file = new File(filePath);
		if (!file.exists() || !file.isDirectory()) {
			return classList;
		}

		// 处理类文件
		File[] classes = file.listFiles(child -> ClassUtil.isClass(child.getName()));
		Arrays.asList(classes).forEach(child -> {
			String className = ClassUtil.classFile2SimpleClass(
					new StringBuilder().append(pkg).append(PACKAGE_SEPARATOR).append(child.getName()).toString());

			try {
				Class clz = Thread.currentThread().getContextClassLoader().loadClass(className);
				classList.add(clz);
			} catch (ClassNotFoundException | LinkageError e) {
				log.warn("can load class:{}", className);
			}
		});

		// 处理目录
		File[] dirs = file.listFiles(child -> child.isDirectory());
		Arrays.asList(dirs).forEach(child -> {
			String childPackageName = new StringBuilder().append(pkg).append(PACKAGE_SEPARATOR).append(child.getName())
					.toString();
			String childPath = new StringBuilder().append(filePath).append(PATH_SEPARATOR).append(child.getName())
					.toString();
			classList.addAll(recursiveScan4Path(childPackageName, childPath));
		});

		return classList;
	}

	/**
	 * 递归扫描Jar文件内的Class类
	 *
	 * @param pkg
	 * @param jarPath
	 * @return Class列表
	 * @throws IOException
	 */
	private static List<Class> recursiveScan4Jar(String pkg, String jarPath) throws IOException {
		List<Class> classList = new LinkedList<Class>();

		JarInputStream jin = new JarInputStream(new FileInputStream(jarPath));
		JarEntry entry = jin.getNextJarEntry();
		while (entry != null) {
			String name = entry.getName();
			entry = jin.getNextJarEntry();

			if (!name.contains(ClassUtil.package2Path(pkg))) {
				continue;
			}
			if (ClassUtil.isClass(name)) {
				if (ClassUtil.isAnonymousInnerClass(name)) {
					// 是匿名内部类，跳过不作处理
					continue;
				}

				String className = ClassUtil.classFile2SimpleClass(ClassUtil.path2Package(name));
				try {
					Class clz = Thread.currentThread().getContextClassLoader().loadClass(className);
					classList.add(clz);
				} catch (ClassNotFoundException | LinkageError e) {
					log.warn("can load class:{}", name);
				}
			}
		}

		return classList;
	}
}
