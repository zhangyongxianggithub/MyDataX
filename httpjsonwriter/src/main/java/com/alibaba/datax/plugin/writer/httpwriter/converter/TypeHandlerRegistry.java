package com.alibaba.datax.plugin.writer.httpwriter.converter;

import java.time.LocalDateTime;

import com.alibaba.datax.common.element.Column;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2023/8/24 21:03
 * @description:
 **/

public class TypeHandlerRegistry {
    
    private final Table<Column.Type, Class<?>, TypeHandler<?>> handlers = HashBasedTable
            .create();
    
    public TypeHandlerRegistry() {
        registerDefault(Column.Type.INT, new IntVoidTypeHandler());
        registerDefault(Column.Type.LONG, new LongVoidTypeHandler());
        registerDefault(Column.Type.NULL, new NullVoidTypeHandler());
        registerDefault(Column.Type.DOUBLE, new DoubleVoidTypeHandler());
        registerDefault(Column.Type.STRING, new StringVoidTypeHandler());
        registerDefault(Column.Type.BOOL, new BoolVoidTypeHandler());
        registerDefault(Column.Type.DATE, new DateVoidTypeHandler());
        registerDefault(Column.Type.BYTES, new BytesVoidTypeHandler());
        register(Column.Type.DATE, LocalDateTime.class,
                new DateLocalDateTimeTypeHandler());
    }
    
    // BAD, NULL, INT, LONG, DOUBLE, STRING, BOOL, DATE, BYTES
    
    <T> void register(final Column.Type type, final Class<T> targetClass,
            final TypeHandler<T> typeHandler) {
        handlers.put(type, targetClass, typeHandler);
    }
    
    void registerDefault(final Column.Type type,
            final TypeHandler<Object> typeHandler) {
        handlers.put(type, Void.class, typeHandler);
    }
    
    <T> boolean hasTypeHandler(final Column.Type type,
            final Class<T> targetClass) {
        return handlers.contains(type, targetClass);
    }
    
    <T> TypeHandler<T> getTypeHandler(final Column.Type type,
            final Class<T> targetClass) {
        return (TypeHandler<T>) handlers.get(type, targetClass);
    }
    
}
