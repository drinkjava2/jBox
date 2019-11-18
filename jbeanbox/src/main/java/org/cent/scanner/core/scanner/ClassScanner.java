package org.cent.scanner.core.scanner;

import org.cent.scanner.core.anno.Scannable;
import org.cent.scanner.core.callback.ScannerCallback;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author: cent
 * @email: 292462859@qq.com
 * @date: 2019/1/7.
 * @description:
 */
public interface ClassScanner {

    /**
     * 扫描某个包下的Class
     *
     * @param scanBasePackages
     * @return
     */
    List<Class> scan(String... scanBasePackages);

    /**
     * 扫描某个包下带有注解的Class
     *
     * @param anno
     * @param scanBasePackages
     * @return
     */
    List<Class> scanByAnno(Class<? extends Annotation> anno, String... scanBasePackages);

    /**
     * 扫描某个包下的Class，并执行回调
     *
     * @param callback
     * @param scanBasePackages
     */
    void scanAndCallback(ScannerCallback callback, String... scanBasePackages);

    /**
     * 扫描某个包下特定注解的Class，并执行回调
     *
     * @param anno
     * @param callback
     * @param scanBasePackages
     */
    void scanAndCallbackByAnno(ScannerCallback callback, Class<? extends Annotation> anno, String... scanBasePackages);
}
