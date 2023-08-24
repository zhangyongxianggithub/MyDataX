package com.alibaba.datax.plugin.writer.restwriter;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.datax.common.element.Column;
import com.alibaba.datax.common.element.Record;
import com.alibaba.datax.common.exception.DataXException;
import com.alibaba.datax.common.plugin.RecordReceiver;
import com.alibaba.datax.common.spi.Writer;
import com.alibaba.datax.common.util.Configuration;
import com.alibaba.datax.plugin.writer.restwriter.handler.ObjectRecordConverter;
import com.alibaba.datax.plugin.writer.restwriter.handler.TypeHandlerRegistry;
import com.alibaba.datax.plugin.writer.restwriter.validator.ConfigurationValidator;
import com.google.common.collect.Lists;

import kong.unirest.HttpMethod;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import static com.alibaba.datax.plugin.writer.restwriter.Key.BATCH_MODE;
import static com.alibaba.datax.plugin.writer.restwriter.Key.BATCH_SIZE;
import static com.alibaba.datax.plugin.writer.restwriter.Key.FAIL_FAST;
import static com.alibaba.datax.plugin.writer.restwriter.Key.FIELDS;
import static com.alibaba.datax.plugin.writer.restwriter.Key.HTTP_HEADERS;
import static com.alibaba.datax.plugin.writer.restwriter.Key.HTTP_METHOD;
import static com.alibaba.datax.plugin.writer.restwriter.Key.HTTP_QUERY;
import static com.alibaba.datax.plugin.writer.restwriter.Key.HTTP_SSL;
import static com.alibaba.datax.plugin.writer.restwriter.Key.MAX_RETRIES;
import static com.alibaba.datax.plugin.writer.restwriter.Key.PRINT;
import static com.alibaba.datax.plugin.writer.restwriter.Key.RATE_PER_TASK;
import static com.alibaba.datax.plugin.writer.restwriter.Key.TASK_INDEX;
import static com.alibaba.datax.plugin.writer.restwriter.Key.URL;
import static com.alibaba.datax.plugin.writer.restwriter.RestWriterErrorCode.RUNTIME_EXCEPTION;
import static org.apache.commons.collections4.MapUtils.emptyIfNull;

/**
 * Created by zhangyongxiang on 2023-08-23.
 */
@Slf4j
public class RestWriter extends Writer {
    
    @Slf4j
    @EqualsAndHashCode(callSuper = true)
    public static class Job extends Writer.Job {
        
        private Configuration originalConfig;
        
        private long startTime;
        
        private long endTime;
        
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
                new ConfigurationValidator().validate(this.originalConfig,
                        null);
            } catch (final Exception se) {
                throw DataXException.asDataXException(RUNTIME_EXCEPTION,
                        "an exception has occurred when validating parameters",
                        se);
            }
        }
        
        @Override
        public void preCheck() {
            
            log.info("job {} pre check will not be called",
                    this.getPluginName());
        }
        
        @Override
        public void prepare() {
            this.startTime = System.currentTimeMillis();
            log.info("{} job prepared, job conf: {}", this.getPluginName(),
                    this.originalConfig);
        }
        
        @Override
        public List<Configuration> split(final int mandatoryNumber) {
            int finalMandatoryNumber = mandatoryNumber;
            if (finalMandatoryNumber < 1) {
                log.warn(
                        "mandatory number {} less than one, reset it to be one",
                        mandatoryNumber);
                finalMandatoryNumber = 1;
            }
            final List<Configuration> configurations = Lists
                    .newArrayListWithExpectedSize(finalMandatoryNumber);
            
            for (int index = 0; index < finalMandatoryNumber; index++) {
                final Configuration taskConf = this.originalConfig.clone();
                taskConf.set(TASK_INDEX, index);
                configurations.add(taskConf);
                log.info(
                        "{} job split into {} tasks, current task: {}, desc: {}, developer: {}, task conf: {}",
                        this.getPluginName(), finalMandatoryNumber, index,
                        this.getDescription(), this.getDeveloper(), taskConf);
            }
            return configurations;
        }
        
        @Override
        public void post() {
            this.endTime = System.currentTimeMillis();
            log.info(
                    "job {} execute to end, start from {}, end to {}, total time: {}",
                    this.getPluginName(),
                    Instant.ofEpochMilli(this.startTime)
                            .atZone(ZoneId.systemDefault()).toLocalDateTime(),
                    Instant.ofEpochMilli(this.endTime)
                            .atZone(ZoneId.systemDefault()).toLocalDateTime(),
                    Duration.ofMillis(this.endTime - this.startTime));
            
        }
        
        @Override
        public void destroy() {
            log.info("job {} destroy, nothing to clean up",
                    this.getPluginName());
        }
    }
    
    @Slf4j
    @EqualsAndHashCode(callSuper = true)
    public static class Task extends Writer.Task {
        
        private long startTime;
        
        private long endTime;
        
        private int successCount;
        
        private int failCount;
        
        private UnirestInstance unirest;
        
        private ObjectRecordConverter converter;
        
        private Configuration writerSliceConfig;
        
        private Integer taskIndex;
        
        private String url;
        
        private HttpMethod method;
        
        private Boolean ssl;
        
        private Map<String, String> headers;
        
        private Map<String, Object> query;
        
        private Integer maxRetries;
        
        private boolean batchMode;
        
        private Integer batchSize;
        
        private List<Field> fields;
        
        private boolean print;
        
        private boolean failFast;
        
        private Integer ratePerTask;
        
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
            this.maxRetries = this.writerSliceConfig.getInt(MAX_RETRIES, 3);
            this.batchMode = this.writerSliceConfig.getBool(BATCH_MODE, false);
            this.batchSize = this.writerSliceConfig.getInt(BATCH_SIZE, 1000);
            this.fields = this.writerSliceConfig.getList(FIELDS, Field.class);
            this.print = this.writerSliceConfig.getBool(PRINT, false);
            this.failFast = this.writerSliceConfig.getBool(FAIL_FAST, true);
            this.ratePerTask = this.writerSliceConfig.getInt(RATE_PER_TASK);
            log.info(
                    "{} task {} initialized, desc: {}, developer: {}, task conf: {}",
                    this.getPluginName(), this.taskIndex, this.getDescription(),
                    this.getDeveloper(), this.writerSliceConfig);
        }
        
        @Override
        public void prepare() {
            this.unirest = Unirest.spawnInstance();
            if (!emptyIfNull(this.headers).isEmpty()) {
                this.headers.forEach(this.unirest.config()::addDefaultHeader);
            }
            this.unirest.config().addShutdownHook(true);
            this.unirest.config().defaultBaseUrl(this.url);
            this.converter = new ObjectRecordConverter(
                    new TypeHandlerRegistry(), this.fields);
            this.startTime = System.currentTimeMillis();
            this.successCount = 0;
            this.failCount = 0;
            log.info(
                    "{} task {} prepared, desc: {}, developer: {}, task conf: {}",
                    this.getPluginName(), this.taskIndex, this.getDescription(),
                    this.getDeveloper(), this.writerSliceConfig);
        }
        
        @Override
        public void preCheck() {
            log.info(
                    "{} task {} check will not be called, desc: {}, developer: {}, task conf {}",
                    this.getPluginName(), this.taskIndex, this.getDescription(),
                    this.getDeveloper(), this.writerSliceConfig);
        }
        
        @Override
        public void startWrite(final RecordReceiver lineReceiver) {
            final List<Record> writerBuffer = new ArrayList<>(this.batchSize);
            Record recordItem = null;
            while ((recordItem = lineReceiver.getFromReader()) != null) {
                if (this.batchMode) {
                    writerBuffer.add(recordItem);
                    if (writerBuffer.size() >= this.batchSize) {
                        this.doWrite(writerBuffer);
                        writerBuffer.clear();
                    }
                    
                } else {
                    this.doWrite(recordItem);
                }
                if (this.print) {
                    final int bound = recordItem.getColumnNumber();
                    for (int index = 0; index < bound; index++) {
                        final Column column = recordItem.getColumn(index);
                        log.info(
                                "colum type: {} column type class: {}, raw data: {}, raw data class: {}, byte size: {}",
                                column.getType(),
                                column.getType().getClass().getName(),
                                column.getRawData(),
                                column.getRawData().getClass().getName(),
                                column.getByteSize());
                    }
                }
            }
            if (this.batchMode && !writerBuffer.isEmpty()) {
                this.doWrite(writerBuffer);
                writerBuffer.clear();
            }
            
        }
        
        @Override
        public void post() {
            this.endTime = System.currentTimeMillis();
            this.unirest.close();
            log.info(
                    "job {} task {} execute to end, start from {}, end to {}, total time: {}, count: {}",
                    this.getPluginName(), this.taskIndex,
                    Instant.ofEpochMilli(this.startTime)
                            .atZone(ZoneId.systemDefault()).toLocalDateTime(),
                    Instant.ofEpochMilli(this.endTime)
                            .atZone(ZoneId.systemDefault()).toLocalDateTime(),
                    Duration.ofMillis(this.endTime - this.startTime),
                    this.successCount);
            if (this.failCount > 0) {
                log.error("job {} task {} execute to end, fail count: {}",
                        this.getPluginName(), this.taskIndex, this.failCount);
            }
        }
        
        @Override
        public void destroy() {
            
            log.info("job {} task {} destroy", this.getPluginName(),
                    this.taskIndex);
        }
        
        private void doWrite(final Record item) {
            final Map<String, Object> body = this.converter.convert(item);
            final HttpResponse<JsonNode> response = this.unirest
                    .request(this.method.name(), "").queryString(this.query)
                    .body(body).asJson();
            if (response.isSuccess()) {
                this.successCount += 1;
                log.info("the {}th record has been written successfully",
                        this.successCount);
            } else {
                // append fail data to file
                this.failCount += 1;
                log.error(
                        "data write failed, http code: {}, message: {} , optional reason: {},  data info: {} ",
                        response.getStatus(), response.getStatusText(),
                        response.getBody(), body);
            }
            
        }
        
        private void doWrite(final List<Record> records) {
            final List<Map<String, Object>> body = records.stream()
                    .map(this.converter::convert).collect(Collectors.toList());
            final HttpResponse<JsonNode> response = this.unirest
                    .request(this.method.name(), "").queryString(this.query)
                    .body(body).asJson();
            if (response.isSuccess()) {
                this.successCount += records.size();
                log.info("the {}th record has been written successfully",
                        this.successCount);
            } else {
                this.failCount += records.size();
                log.error(
                        "data write failed, http code: {}, message: {} , optional reason: {},  data info: {} ",
                        response.getStatus(), response.getStatusText(),
                        response.getBody(), body);
            }
        }
    }
}
