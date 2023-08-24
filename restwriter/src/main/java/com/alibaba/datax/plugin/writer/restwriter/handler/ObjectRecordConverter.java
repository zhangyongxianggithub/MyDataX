package com.alibaba.datax.plugin.writer.restwriter.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ClassUtils;

import com.alibaba.datax.common.element.Column;
import com.alibaba.datax.common.element.Record;
import com.alibaba.datax.common.exception.DataXException;
import com.alibaba.datax.plugin.writer.restwriter.Field;
import com.google.common.collect.Maps;

import static com.alibaba.datax.plugin.writer.restwriter.RestWriterErrorCode.FIELD_CLASS_BOT_FOUND_EXCEPTION;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2023/8/24 14:25
 * @description:
 **/

public class ObjectRecordConverter
        implements RecordConverter<Map<String, Object>> {
    
    private final TypeHandlerRegistry registry;
    
    private final List<Field> fields;
    
    private final Map<String, Class<?>> fieldClasses;
    
    public ObjectRecordConverter(final TypeHandlerRegistry registry,
            final List<Field> fields) {
        this.registry = registry;
        this.fields = fields;
        this.fieldClasses = new HashMap<>();
        if (!fields.isEmpty()) {
            fields.forEach(field -> {
                if (isNotBlank(field.getType())) {
                    try {
                        fieldClasses.put(field.getName(),
                                ClassUtils.getClass(field.getType()));
                    } catch (final ClassNotFoundException e) {
                        throw DataXException.asDataXException(
                                FIELD_CLASS_BOT_FOUND_EXCEPTION,
                                String.format("field %s type %s not found",
                                        field.getName(), field.getType()),
                                e);
                    }
                } else {
                    fieldClasses.put(field.getName(), Void.class);
                }
            });
        }
    }
    
    @Override
    public Map<String, Object> convert(final Record record) {
        final Map<String, Object> m = Maps.newHashMap();
        IntStream.range(0, record.getColumnNumber()).forEach(num -> {
            final Column column = record.getColumn(num);
            final Class<?> clazz = fieldClasses.get(fields.get(num).getName());
            final TypeHandler<?> typeHandler = registry
                    .getTypeHandler(column.getType(), clazz);
            m.put(fields.get(num).getName(),
                    typeHandler.convert(column.getRawData()));
        });
        return m;
    }
}
