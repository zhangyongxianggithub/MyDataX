package com.alibaba.datax.plugin.writer.httpwriter;

import com.alibaba.datax.common.spi.ErrorCode;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2023/8/24 11:34
 * @description:
 **/

public enum HttpWriterErrorCode implements ErrorCode {
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
    CONFIG_INVALID_EXCEPTION("HttpWriter-02", "您的参数配置错误.");
    
    private final String code;
    private final String description;
    
    HttpWriterErrorCode(final String code, final String description) {
        this.code = code;
        this.description = description;
    }
    
    @Override
    public String getCode() {
        return code;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
}
