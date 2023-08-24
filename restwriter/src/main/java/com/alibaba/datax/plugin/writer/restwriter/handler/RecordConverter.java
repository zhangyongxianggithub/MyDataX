package com.alibaba.datax.plugin.writer.restwriter.handler;

import com.alibaba.datax.common.element.Record;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2023/8/24 14:24
 * @description:
 **/
@FunctionalInterface
public interface RecordConverter<T> {
    
    T convert(Record record);
    
}
