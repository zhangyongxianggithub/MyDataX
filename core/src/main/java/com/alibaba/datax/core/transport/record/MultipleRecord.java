package com.alibaba.datax.core.transport.record;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.datax.common.element.Column;
import com.alibaba.datax.common.element.Record;

/**
 * Created by zhangyongxiang on 2023/8/24 12:34 AM
 **/
public class MultipleRecord implements Record {
    
    private final List<Record> records = new ArrayList<>();
    
    public void addRecord(Record record) {
        records.add(record);
    }
    
    public List<Record> getRecords() {
        return records;
    }
    
    @Override
    public void addColumn(final Column column) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setColumn(final int i, final Column column) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Column getColumn(final int i) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int getColumnNumber() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int getByteSize() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int getMemorySize() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setMeta(final Map<String, String> meta) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Map<String, String> getMeta() {
        throw new UnsupportedOperationException();
    }
}
