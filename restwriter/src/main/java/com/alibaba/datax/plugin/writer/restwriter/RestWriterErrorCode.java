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
    RUNTIME_EXCEPTION("HttpWriter-00", "运行时异常"),
    /**
     * parameter value is illegal
     */
    ILLEGAL_VALUE("HttpWriter-01", "您填写的参数值不合法."),
    /**
     * parameter config error
     */
    CONFIG_INVALID_EXCEPTION("HttpWriter-02", "您的参数配置错误."),
    
    EMPTY_RECORD_EXCEPTION("HttpWriter-03", "空数据"),
    
    FIELD_CLASS_BOT_FOUND_EXCEPTION("HttpWriter-04", "空数据"),
    
    URL_INVALID_EXCEPTION("HttpWriter-04", "您填写的URL参数值不合法");
    
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
