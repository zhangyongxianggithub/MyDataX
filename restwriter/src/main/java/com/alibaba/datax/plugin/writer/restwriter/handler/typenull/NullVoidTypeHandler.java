package com.alibaba.datax.plugin.writer.restwriter.handler.typenull;

import com.alibaba.datax.plugin.writer.restwriter.handler.TypeHandler;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2023/8/24 21:20
 * @description:
 **/

public class NullVoidTypeHandler implements TypeHandler<Object> {
    
    /**
     * unknown underlying type
     */
    @Override
    public Object convert(final Object object) {
        return null;
    }
}
