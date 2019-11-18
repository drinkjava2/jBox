package org.cent.scanner.core.callback;

import java.util.List;

/**
 * @author: cent
 * @email: 292462859@qq.com
 * @date: 2019/1/7.
 * @description: Callback函数接口
 */
public interface ScannerCallback {

    /**
     * 回调方法
     *
     * @param clzs
     */
    void callback(List<Class> clzs);
}
