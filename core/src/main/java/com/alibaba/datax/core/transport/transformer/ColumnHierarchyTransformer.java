package com.alibaba.datax.core.transport.transformer;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.datax.common.element.Record;
import com.alibaba.datax.transformer.Transformer;

/**
 * Created by zhangyongxiang on 2023/8/24 12:04 AM
 **/

public class ColumnHierarchyTransformer extends Transformer {
    
    private static final Logger logger = LoggerFactory
            .getLogger(ColumnHierarchyTransformer.class);
    
    public ColumnHierarchyTransformer() {
        setTransformerName("dx_column_hierarchy");
    }
    
    @Override
    public Record evaluate(final Record record, final Object... paras) {
        if (paras.length < 1) {
            return record;
        } else {
            Map<String, List<String>> columnHierarchy= (Map<String, List<String>>) paras[0];

        }
        
        final Column column = record.getColumn(columnIndex);
        try {
            final String oriValue = column.asString();
            if (oriValue == null) {
                return record;
            }
            if (column.getType() == Column.Type.STRING) {
                final EncryptUtil encryptUtil = EncryptUtil.getInstance();
                final String newValue = encryptUtil.AESencode(oriValue,
                        ENCRYPT_KEY);
                record.setColumn(columnIndex, new StringColumn(newValue));
            }
        } catch (final Exception e) {
            throw DataXException.asDataXException(
                    TransformerErrorCode.TRANSFORMER_RUN_EXCEPTION,
                    e.getMessage(), e);
        }
        return record;
    }
}
