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

import static com.alibaba.datax.plugin.writer.restwriter.RestWriterErrorCode.EMPTY_FIELD_EXCEPTION;
import static com.alibaba.datax.plugin.writer.restwriter.RestWriterErrorCode.FIELD_CLASS_BOT_FOUND_EXCEPTION;
import static com.alibaba.datax.plugin.writer.restwriter.RestWriterErrorCode.FIELD_MISMATCH_WITH_COLUMN_EXCEPTION;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author: zhangyongxiang
 * @date 2023/8/24 14:25
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
        } else {
            throw DataXException.asDataXException(EMPTY_FIELD_EXCEPTION,
                    "you should configure at least one field");
        }
    }
    
    @Override
    public Map<String, Object> convert(final Record record) {
        if (fields.size() != record.getColumnNumber()) {
            throw DataXException.asDataXException(
                    FIELD_MISMATCH_WITH_COLUMN_EXCEPTION,
                    "number of fields is not same as number of columns of record");
        }
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
