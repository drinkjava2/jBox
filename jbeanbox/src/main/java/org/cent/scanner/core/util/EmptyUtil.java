package org.cent.scanner.core.util;

/**
 * @author: cent
 * @email: 292462859@qq.com
 * @date: 2019/1/7.
 * @description:
 */
public enum EmptyUtil {
    ;

    /**
     * 字符串判空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        } else if (str.trim().length() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 字符串非空
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}
