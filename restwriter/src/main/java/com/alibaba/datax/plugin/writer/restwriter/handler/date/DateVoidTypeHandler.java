package com.alibaba.datax.plugin.writer.restwriter.handler.date;

import com.alibaba.datax.plugin.writer.restwriter.handler.TypeHandler;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2023/8/24 21:47
 * @description:
 **/

public class DateVoidTypeHandler implements TypeHandler<Object> {
    /**
     * underlying type is Long
     */
    @Override
    public Object convert(final Object object) {
        return object;
    }
}
