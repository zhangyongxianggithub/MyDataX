package com.alibaba.datax.plugin.writer.restwriter.handler.typelong;

import com.alibaba.datax.plugin.writer.restwriter.handler.TypeHandler;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2023/8/24 21:23
 * @description:
 **/

public class LongVoidTypeHandler implements TypeHandler<Object> {

    /**
     * underlying type is BigInteger
     */
    @Override
    public Object convert(final Object object) {
        return object;
    }
}
