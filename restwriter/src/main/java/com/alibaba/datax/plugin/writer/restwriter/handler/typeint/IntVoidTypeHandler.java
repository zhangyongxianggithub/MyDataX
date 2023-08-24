package com.alibaba.datax.plugin.writer.restwriter.handler.typeint;

import com.alibaba.datax.plugin.writer.restwriter.handler.TypeHandler;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2023/8/24 21:22
 * @description:
 **/

public class IntVoidTypeHandler implements TypeHandler<Object> {

    /**
     * underlying type is BigInteger
     */

    @Override
    public Object convert(final Object object) {
        return object;
    }
}
