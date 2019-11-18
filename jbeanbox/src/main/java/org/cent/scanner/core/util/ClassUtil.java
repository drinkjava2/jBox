package org.cent.scanner.core.util;

import java.util.regex.Pattern;

import static org.cent.scanner.core.consts.CommonConsts.PACKAGE_SEPARATOR;
import static org.cent.scanner.core.consts.CommonConsts.PATH_SEPARATOR;

/**
 * @author: cent
 * @email: 292462859@qq.com
 * @date: 2019/1/7.
 * @description:
 */
public enum ClassUtil {
    ;

    /**
     * 匿名内部类匹配表达式
     */
    public static final Pattern ANONYMOUS_INNER_CLASS_PATTERN = Pattern.compile("^[\\s\\S]*\\${1}\\d+\\.class$");

    public static String package2Path(String packageName) {
        return packageName.replace(PACKAGE_SEPARATOR, PATH_SEPARATOR);
    }

    public static String path2Package(String pathName) {
        return pathName.replaceAll(PATH_SEPARATOR, PACKAGE_SEPARATOR);
    }

    public static boolean isClass(String fileName) {
        if (EmptyUtil.isEmpty(fileName)) {
            return false;
        }
        return fileName.endsWith(".class");
    }

    public static String classFile2SimpleClass(String classFileName) {
        return classFileName.replace(".class", "");
    }

    public static boolean isAnonymousInnerClass(String className) {
        return ANONYMOUS_INNER_CLASS_PATTERN.matcher(className).matches();
    }
}
