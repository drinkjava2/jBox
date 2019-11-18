package org.cent.scanner.core.scanner.impl;

import org.cent.scanner.core.anno.Scannable;
import org.cent.scanner.core.callback.ScannerCallback;
import org.cent.scanner.core.scanner.ClassScanner;
import org.cent.scanner.core.util.EmptyUtil;
import org.cent.scanner.core.util.ScannerUtil;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: cent
 * @email: 292462859@qq.com
 * @date: 2019/1/7.
 * @description:
 */
public class DefaultClassScanner implements ClassScanner {


    @Override
    public List<Class> scan(String... scanBasePackages) {
        List<Class> classList = new LinkedList<Class>();
        if (scanBasePackages.length == 0) {
            return classList;
        }

        Arrays.asList(scanBasePackages)
                .stream()
                .filter(pkg -> EmptyUtil.isNotEmpty(pkg))
                .forEach(pkg -> classList.addAll(ScannerUtil.scan(pkg)));

        return classList;
    }

    @Override
    public List<Class> scanByAnno(Class<? extends Annotation> anno, String... scanBasePackages) {
        List<Class> classList = this.scan(scanBasePackages);
        return classList.parallelStream()
                .filter(clz -> {
                    Annotation clzAnno = clz.getAnnotation(anno);
                    if (clzAnno == null) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void scanAndCallback(ScannerCallback callback, String... scanBasePackages) {
        List<Class> classList = this.scan(scanBasePackages);
        callback.callback(classList);
    }

    @Override
    public void scanAndCallbackByAnno(ScannerCallback callback, Class<? extends Annotation> anno, String... scanBasePackages) {
        List<Class> classList = this.scanByAnno(anno, scanBasePackages);
        callback.callback(classList);
    }
}
