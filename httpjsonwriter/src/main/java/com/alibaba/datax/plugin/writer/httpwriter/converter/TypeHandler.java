package com.alibaba.datax.plugin.writer.httpwriter.converter;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2023/8/24 21:03
 * @description:
 **/

public interface TypeHandler<T> {
    T convert(Object object);
}
