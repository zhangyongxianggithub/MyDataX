package com.alibaba.datax.plugin.writer.restwriter.conf;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Data;

/**
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 **/

@Data
public class Process {
    
    private boolean concurrent;
    
    private List<Operation> operations = Lists.newArrayList();
}
