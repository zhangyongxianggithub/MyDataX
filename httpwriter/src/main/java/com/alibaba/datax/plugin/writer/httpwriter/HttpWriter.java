package com.alibaba.datax.plugin.writer.httpwriter;

import java.util.List;

import com.alibaba.datax.common.plugin.RecordReceiver;
import com.alibaba.datax.common.spi.Writer;
import com.alibaba.datax.common.util.Configuration;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by zhangyongxiang on 2023-08-23.
 */
@Slf4j
public class HttpWriter extends Writer {
    
    @Slf4j
    public static class Job extends Writer.Job {
        
        @Override
        public void preCheck() {
            log.info("pre check");
        }
        
        @Override
        public void prepare() {
            log.info("pre check");
        }
        
        @Override
        public void post() {
            log.info("pre check");
        }
        
        @Override
        public void init() {
            log.info("pre check");
        }
        
        @Override
        public void destroy() {
            log.info("pre check");
        }
        
        @Override
        public List<Configuration> split(final int mandatoryNumber) {
            final Configuration configurations = this.getPluginJobConf();
            return Lists.newArrayList(configurations);
        }
    }
    
    @Slf4j
    public static class Task extends Writer.Task {
        
        @Override
        public void init() {
            log.info("task init");
        }
        
        @Override
        public void destroy() {
            log.info("task destroy");
        }
        
        @Override
        public void startWrite(final RecordReceiver lineReceiver) {
            log.info(lineReceiver.getFromReader().toString());
            System.out.println(lineReceiver.getFromReader().toString());
        }
    }
}
