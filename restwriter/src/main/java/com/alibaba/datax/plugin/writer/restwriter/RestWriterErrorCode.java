package com.alibaba.datax.plugin.writer.restwriter;

import com.alibaba.datax.common.spi.ErrorCode;

/**
 * @author: zhangyongxiang
 * @date 2023/8/24 11:34
 **/
public enum RestWriterErrorCode implements ErrorCode {
    /**
     * runtime exception
     */
    RUNTIME_EXCEPTION("RestWriter-00", "运行时异常"),
    
    EMPTY_RECORD_EXCEPTION("RestWriter-01", "空record数据"),
    
    EMPTY_FIELD_EXCEPTION("RestWriter-02", "你需要配置至少一个field"),
    
    FIELD_MISMATCH_WITH_COLUMN_EXCEPTION("RestWriter-03",
            "field数量与column数量不匹配"),
    
    FIELD_CLASS_NOT_FOUND_EXCEPTION("RestWriter-04", "配置的field class不存在"),
    
    URL_INVALID_EXCEPTION("RestWriter-05", "您填写的URL参数值不合法"),
    
    METHOD_INVALID_EXCEPTION("RestWriter-06", "您填写的method参数值不合法"),
    
    RATE_PER_TASK_INVALID_EXCEPTION("RestWriter-07", "您填写的rate-per-task参数值不合法"),
    
    BATCH_SIZE_INVALID_EXCEPTION("RestWriter-08", "您填写的batchSize参数值不合法"),
    
    MAX_RETRIES_INVALID_EXCEPTION("RestWriter-09", "您填写的maxRetries参数值不合法");
    
    private final String code;
    
    private final String description;
    
    RestWriterErrorCode(final String code, final String description) {
        this.code = code;
        this.description = description;
    }
    
    @Override
    public String getCode() {
        return this.code;
    }
    
    @Override
    public String getDescription() {
        return this.description;
    }
}
