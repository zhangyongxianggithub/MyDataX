package com.alibaba.datax.plugin.writer.httpwriter;

import java.util.List;

import com.alibaba.datax.common.plugin.RecordReceiver;
import com.alibaba.datax.common.spi.Writer;
import com.alibaba.datax.common.util.Configuration;

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
            super.preCheck();
        }
        
        @Override
        public void prepare() {
            super.prepare();
        }
        
        @Override
        public void post() {
            super.post();
        }
        
        @Override
        public void init() {
            
        }
        
        @Override
        public void destroy() {
            
        }
        
        @Override
        public List<Configuration> split(final int mandatoryNumber) {
            return null;
        }
    }
    
    @Slf4j
    public static class Task extends Writer.Task {
        
        @Override
        public void init() {
            
        }
        
        @Override
        public void destroy() {
            
        }
        
        @Override
        public void startWrite(final RecordReceiver lineReceiver) {
            
        }
    }
}
