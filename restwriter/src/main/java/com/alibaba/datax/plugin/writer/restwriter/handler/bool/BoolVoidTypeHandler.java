package com.alibaba.datax.plugin.writer.restwriter.handler.bool;

import com.alibaba.datax.plugin.writer.restwriter.handler.TypeHandler;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2023/8/24 21:46
 * @description:
 **/

public class BoolVoidTypeHandler implements TypeHandler<Object> {
    /**
     * underlying type is Boolean
     */
    @Override
    public Object convert(final Object object) {
        return object;
    }
}
