package com.alibaba.datax.plugin.writer.restwriter.validator;

import com.alibaba.datax.common.exception.DataXException;
import com.alibaba.datax.common.util.Configuration;

import static com.alibaba.datax.plugin.writer.restwriter.Key.ADDITIONAL_CONCURRENT;
import static com.alibaba.datax.plugin.writer.restwriter.RestWriterErrorCode.CONCURRENT_INVALID_EXCEPTION;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

/**
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 **/

public class ProcessValidator implements ParameterValidator<Configuration> {
    
    @Override
    public void validateImmediateValue(final Configuration parameter) {
        if (nonNull(parameter)) {
            final String concurrent = parameter
                    .getString(ADDITIONAL_CONCURRENT);
            if (nonNull(concurrent) && !equalsIgnoreCase(concurrent, "true")
                    && !equalsIgnoreCase(concurrent, "false")) {
                throw DataXException.asDataXException(
                        CONCURRENT_INVALID_EXCEPTION,
                        String.format(
                                "parameter concurrent %s is invalid, allow values: true,false",
                                concurrent));
            }
            // parameter.getListWithJson(ADDITIONAL_OPERATIONS,
            // Operation.class);
        }
    }
    
    @Override
    public void validate(final Configuration config, final String path) {
        validateImmediateValue(config.getConfiguration(path));
    }
}
