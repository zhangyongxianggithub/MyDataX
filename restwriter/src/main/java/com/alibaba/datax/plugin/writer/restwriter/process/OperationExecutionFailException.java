package com.alibaba.datax.plugin.writer.restwriter.process;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2023/10/12 15:48
 * @description:
 **/

public class OperationExecutionFailException extends RuntimeException {
    
    private static final long serialVersionUID = 2848134252562605007L;
    
    public OperationExecutionFailException(final String message,
            final Throwable cause) {
        super(message, cause);
    }
}
