package com.alibaba.datax.plugin.writer.restwriter.conf;

import java.util.Map;

import lombok.Data;

/**
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 **/
@Data
public class Operation {
    
    private String url;
    
    private String method;
    
    private Map<String, String> headers;
    
    private String body;
    
    private boolean debug;
    
}
