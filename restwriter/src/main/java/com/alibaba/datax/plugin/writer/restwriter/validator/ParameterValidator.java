package com.alibaba.datax.plugin.writer.restwriter.validator;

import com.alibaba.datax.common.util.Configuration;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2023/8/24 18:02
 * @description:
 **/

public interface ParameterValidator<T> {
    
    void validateImmediateValue(T parameter);
    
    void validate(Configuration config, String path);
}
