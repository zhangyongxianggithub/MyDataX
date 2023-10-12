package com.alibaba.datax.plugin.writer.restwriter.conf;

import lombok.Data;

import static kong.unirest.Config.DEFAULT_MAX_CONNECTIONS;
import static kong.unirest.Config.DEFAULT_MAX_PER_ROUTE;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2023/9/5 12:28
 * @description:
 **/
@Data
public class ClientConfig {
    
    private int maxTotal = DEFAULT_MAX_CONNECTIONS;
    
    private int maxPerRoute = DEFAULT_MAX_PER_ROUTE;
    
}
