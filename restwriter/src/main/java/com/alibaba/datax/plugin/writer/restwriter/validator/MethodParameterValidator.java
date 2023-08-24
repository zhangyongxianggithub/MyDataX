package com.alibaba.datax.plugin.writer.restwriter.validator;

import com.alibaba.datax.common.util.Configuration;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2023/8/24 18:39
 * @description:
 **/

public class MethodParameterValidator implements ParameterValidator<String> {

    @Override
    public void validateImmediateValue(final String parameter) {
        
    }
    
    @Override
    public void validate(final Configuration config, final String path) {
        validateImmediateValue(config.getString(path));
    }
}
