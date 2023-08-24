package com.alibaba.datax.plugin.writer.httpwriter;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.alibaba.datax.common.exception.DataXException;
import com.alibaba.datax.common.plugin.RecordReceiver;
import com.alibaba.datax.common.spi.Writer;
import com.alibaba.datax.common.util.Configuration;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import kong.unirest.HttpMethod;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import static com.alibaba.datax.plugin.writer.httpwriter.HttpWriterErrorCode.RUNTIME_EXCEPTION;
import static com.alibaba.datax.plugin.writer.httpwriter.Key.BATCH_SIZE;
import static com.alibaba.datax.plugin.writer.httpwriter.Key.HTTP_HEADERS;
import static com.alibaba.datax.plugin.writer.httpwriter.Key.HTTP_METHOD;
import static com.alibaba.datax.plugin.writer.httpwriter.Key.HTTP_QUERY;
import static com.alibaba.datax.plugin.writer.httpwriter.Key.HTTP_SSL;
import static com.alibaba.datax.plugin.writer.httpwriter.Key.MAX_RETRIES;
import static com.alibaba.datax.plugin.writer.httpwriter.Key.TASK_INDEX;
import static com.alibaba.datax.plugin.writer.httpwriter.Key.URL;
import static java.util.Objects.isNull;

/**
 * Created by zhangyongxiang on 2023-08-23.
 */
@Slf4j
public class HttpWriter extends Writer {
    
    @Slf4j
    @EqualsAndHashCode(callSuper = true)
    public static class Job extends Writer.Job {
        
        private Configuration originalConfig;
        
        @Override
        public void init() {
            this.originalConfig = super.getPluginJobConf();
            this.validateParameter();
            log.info(
                    "{} job initialized, desc: {}, developer: {}, job conf: {}",
                    this.getPluginName(), this.getDescription(),
                    this.getDeveloper(), this.getPluginJobConf());
        }
        
        private void validateParameter() {
            try {
                // TODO validate parameters
            } catch (final Exception se) {
                throw DataXException.asDataXException(RUNTIME_EXCEPTION,
                        se.getMessage(), se);
            }
        }
        
        @Override
        public void preCheck() {
            
            log.info("job pre check will not be called, {}, {}, {}, {}",
                    this.originalConfig, this.getDescription(),
                    this.getDeveloper(), this.getPluginName());
        }
        
        @Override
        public void prepare() {
            log.info("{} job prepared, desc: {}, developer: {}, job conf: {}",
                    this.getPluginName(), this.getDescription(),
                    this.getDeveloper(), this.getPluginJobConf());
        }
        
        @Override
        public List<Configuration> split(final int mandatoryNumber) {
            int finalMandatoryNumber = mandatoryNumber;
            if (finalMandatoryNumber < 1) {
                log.warn(
                        "mandatoryNumber less than 1, reset it to be at least 1");
                finalMandatoryNumber = 1;
            }
            final List<Configuration> configurations = Lists
                    .newArrayListWithExpectedSize(finalMandatoryNumber);
            
            final int finalMandatoryNumber1 = finalMandatoryNumber;
            IntStream.range(0, finalMandatoryNumber).forEach(index -> {
                final Configuration taskConf = this.originalConfig.clone();
                taskConf.set(TASK_INDEX, index);
                configurations.add(taskConf);
                log.info(
                        "{} job split into {} task, desc: {}, developer: {}, task conf: {}",
                        this.getPluginName(), finalMandatoryNumber1,
                        this.getDescription(), this.getDeveloper(), taskConf);
            });
            return configurations;
        }
        
        @Override
        public void post() {
            log.info("job post, {}, {}, {}, {}", this.getPluginJobConf(),
                    this.getDescription(), this.getDeveloper(),
                    this.getPluginName());
        }
        
        @Override
        public void destroy() {
            log.info("job destroy, {}, {}, {}, {}", this.getPluginJobConf(),
                    this.getDescription(), this.getDeveloper(),
                    this.getPluginName());
        }
    }
    
    @Slf4j
    @EqualsAndHashCode(callSuper = true)
    public static class Task extends Writer.Task {
        
        private UnirestInstance unirest;
        
        private Configuration writerSliceConfig;
        
        private Integer taskIndex;
        
        private String url;
        
        private HttpMethod method;
        
        private Boolean ssl;
        /**
         * use list
         */
        private Map<String, String> headers;
        
        private Map<String, Object> query;
        
        private Integer maxRetries;
        
        private Integer batchSize;
        
        @Override
        public void init() {
            this.writerSliceConfig = this.getPluginJobConf();
            this.taskIndex = this.writerSliceConfig.getInt(TASK_INDEX);
            this.url = this.writerSliceConfig.getString(URL);
            this.method = HttpMethod
                    .valueOf(this.writerSliceConfig.getString(HTTP_METHOD));
            this.ssl = this.writerSliceConfig.getBool(HTTP_SSL);
            this.headers = this.writerSliceConfig.getMap(HTTP_HEADERS,
                    String.class);
            this.query = this.writerSliceConfig.getMap(HTTP_QUERY);
            this.maxRetries = this.writerSliceConfig.getInt(MAX_RETRIES);
            this.batchSize = this.writerSliceConfig.getInt(BATCH_SIZE);
            log.info(
                    "{} task {} initialized, desc: {}, developer: {}, task conf: {}",
                    this.getPluginName(), this.taskIndex, this.getDescription(),
                    this.getDeveloper(), this.writerSliceConfig);
        }
        
        @Override
        public void prepare() {
            unirest = Unirest.spawnInstance();
            if (!emptyIfNull(headers).isEmpty()) {
                headers.forEach(unirest.config()::addDefaultHeader);
            }
            unirest.config().automaticRetries(true);
            unirest.config().addShutdownHook(true);
            unirest.config().defaultBaseUrl(this.url);
            log.info(
                    "{} task {} prepared, desc: {}, developer: {}, task conf: {}",
                    this.getPluginName(), this.taskIndex, this.getDescription(),
                    this.getDeveloper(), this.writerSliceConfig);
        }
        
        private <K, V> Map<K, V> emptyIfNull(final Map<K, V> map) {
            if (isNull(map)) {
                return Maps.newHashMap();
            }
            return map;
        }
        
        @Override
        public void preCheck() {
            log.info(
                    "{} task {} check is ignored, desc: {}, developer: {}, task conf{}",
                    this.getPluginName(), this.taskIndex, this.getDescription(),
                    this.getDeveloper(), this.writerSliceConfig);
        }
        
        @Override
        public void startWrite(final RecordReceiver lineReceiver) {
            
//            final HttpResponse<JsonNode> response = this.unirest
//                    .request(this.method.name(), "").queryString(this.query)
//                    .asJson();
            
            log.info(lineReceiver.getFromReader().toString());
            log.info("task startWrite, {}, {}, {}, {}", this.getPluginJobConf(),
                    this.getDescription(), this.getDeveloper(),
                    this.getPluginName());
            System.out.println(lineReceiver.getFromReader().toString());
            
        }
        
        @Override
        public void post() {
            log.info("task post, {}, {}, {}, {}", this.getPluginJobConf(),
                    this.getDescription(), this.getDeveloper(),
                    this.getPluginName());
        }
        
        @Override
        public void destroy() {
            log.info("task destroy, {}, {}, {}, {}", this.getPluginJobConf(),
                    this.getDescription(), this.getDeveloper(),
                    this.getPluginName());
        }
    }
}
