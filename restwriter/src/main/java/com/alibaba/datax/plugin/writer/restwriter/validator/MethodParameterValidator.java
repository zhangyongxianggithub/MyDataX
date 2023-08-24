package com.alibaba.datax.plugin.writer.restwriter.validator;

import com.alibaba.datax.common.util.Configuration;

/**
 * @author: zhangyongxiang
 * @date 2023/8/24 18:39
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
