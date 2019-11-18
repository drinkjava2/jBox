package org.cent.scanner.core.anno;

import java.lang.annotation.*;

/**
 * @author: cent
 * @email: 292462859@qq.com
 * @date: 2019/1/7.
 * @description: 用于标识需要扫描的class
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Scannable {

}
