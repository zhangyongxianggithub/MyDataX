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
            
            log.info("job pre check, {}, {}, {}, {}", this.getPluginJobConf(),
                    this.getDescription(), this.getDeveloper(),
                    this.getPluginName());
        }
        
        @Override
        public void prepare() {
            log.info("job prepare, {}, {}, {}, {}", this.getPluginJobConf(),
                    this.getDescription(), this.getDeveloper(),
                    this.getPluginName());
        }
        
        @Override
        public void post() {
            log.info("job post, {}, {}, {}, {}", this.getPluginJobConf(),
                    this.getDescription(), this.getDeveloper(),
                    this.getPluginName());
        }
        
        @Override
        public void init() {
            log.info("job init, {}, {}, {}, {}", this.getPluginJobConf(),
                    this.getDescription(), this.getDeveloper(),
                    this.getPluginName());
        }
        
        @Override
        public void destroy() {
            log.info("job destroy, {}, {}, {}, {}", this.getPluginJobConf(),
                    this.getDescription(), this.getDeveloper(),
                    this.getPluginName());
        }
        
        @Override
        public List<Configuration> split(final int mandatoryNumber) {
            final Configuration configurations = this.getPluginJobConf();
            log.info("job split, {}, {}, {}, {}, {}",
                    this.getPluginJobConf(), this.getDescription(),
                    this.getDeveloper(), this.getPluginName(), mandatoryNumber);
            return Lists.newArrayList(configurations);
        }
    }
    
    @Slf4j
    public static class Task extends Writer.Task {
        
        @Override
        public void init() {
            log.info("task init, {}, {}, {}, {}", this.getPluginJobConf(),
                    this.getDescription(), this.getDeveloper(),
                    this.getPluginName());
        }
        
        @Override
        public void destroy() {
            log.info("task destroy, {}, {}, {}, {}", this.getPluginJobConf(),
                    this.getDescription(), this.getDeveloper(),
                    this.getPluginName());
        }

        @Override
        public void preCheck() {
            log.info("task check, {}, {}, {}, {}", this.getPluginJobConf(),
                    this.getDescription(), this.getDeveloper(),
                    this.getPluginName());
        }

        @Override
        public void prepare() {
            log.info("task prepare, {}, {}, {}, {}", this.getPluginJobConf(),
                    this.getDescription(), this.getDeveloper(),
                    this.getPluginName());
        }

        @Override
        public void post() {
            log.info("task post, {}, {}, {}, {}", this.getPluginJobConf(),
                    this.getDescription(), this.getDeveloper(),
                    this.getPluginName());
        }

        @Override
        public void startWrite(final RecordReceiver lineReceiver) {
            lineReceiver.getFromReader()
            log.info(lineReceiver.getFromReader().toString());
            log.info("task startWrite, {}, {}, {}, {}", this.getPluginJobConf(),
                    this.getDescription(), this.getDeveloper(),
                    this.getPluginName());
            System.out.println(lineReceiver.getFromReader().toString());
        }
    }
}
