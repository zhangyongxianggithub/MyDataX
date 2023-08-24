package com.alibaba.datax.plugin.writer.httpwriter.validator;

import com.alibaba.datax.common.util.Configuration;

import static com.alibaba.datax.plugin.writer.httpwriter.Key.URL;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2023/8/24 18:32
 * @description:
 **/

public class ConfigurationValidator
        implements ParameterValidator<Configuration> {
    
    private final ParameterValidator<String> urlValidator;
    
    public ConfigurationValidator() {
        urlValidator = new UrlParameterValidator();
    }
    
    @Override
    public void validateImmediateValue(final Configuration parameter) {
        urlValidator.validate(parameter, URL);
        // final String url = this.originalConfig.getString(URL);
        //
        // final String method = this.originalConfig
        // .getString(HTTP_METHOD);
        // final boolean ssl = this.originalConfig.getBool(HTTP_SSL,
        // false);
        // final Map<String, String> headers = this.originalConfig
        // .getMap(HTTP_HEADERS, String.class);
        // final Map<String, Object> query = this.originalConfig
        // .getMap(HTTP_QUERY);
        // final int maxRetries = this.originalConfig.getInt(MAX_RETRIES,
        // 3);
        // final boolean batchMode = this.originalConfig
        // .getBool(BATCH_MODE, false);
        // final int batchSize = this.originalConfig.getInt(BATCH_SIZE,
        // 1000);
        // final List<String> fields = this.originalConfig.getList(FIELDS,
        // String.class);
    }
    
    @Override
    public void validate(final Configuration config, final String path) {
        validateImmediateValue(config);
    }
}
