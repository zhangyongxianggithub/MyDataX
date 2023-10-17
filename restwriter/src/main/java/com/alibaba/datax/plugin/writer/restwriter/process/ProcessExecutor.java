package com.alibaba.datax.plugin.writer.restwriter.process;

import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

import org.apache.commons.collections4.MapUtils;

import com.alibaba.datax.common.exception.DataXException;
import com.alibaba.datax.plugin.writer.restwriter.conf.Operation;
import com.alibaba.datax.plugin.writer.restwriter.conf.Process;

import kong.unirest.HttpMethod;
import kong.unirest.HttpRequest;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;
import lombok.extern.slf4j.Slf4j;

import static com.alibaba.datax.plugin.writer.restwriter.RestWriterErrorCode.POSTPROCESS_OPERATION_ERROR;
import static com.alibaba.datax.plugin.writer.restwriter.RestWriterErrorCode.PREPROCESS_OPERATION_ERROR;
import static java.util.Objects.nonNull;
import static java.util.concurrent.ForkJoinPool.defaultForkJoinWorkerThreadFactory;
import static kong.unirest.ContentType.APPLICATION_JSON;
import static kong.unirest.HeaderNames.CONTENT_TYPE;
import static kong.unirest.HttpMethod.GET;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.MapUtils.emptyIfNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 **/
@Slf4j
public class ProcessExecutor {
    
    private final UnirestInstance unirest;
    
    private final Executor executor;
    
    public ProcessExecutor() {
        this(new ForkJoinPool(Runtime.getRuntime().availableProcessors(),
                defaultForkJoinWorkerThreadFactory, null, true));
    }
    
    public ProcessExecutor(final Executor executor) {
        this.executor = executor;
        this.unirest = Unirest.spawnInstance();
        this.unirest.config().addShutdownHook(true);
        this.unirest.config().verifySsl(false);
        this.unirest.config().automaticRetries(true);
        this.unirest.config()
                .connectTimeout((int) Duration.ofHours(1).toMillis());
        this.unirest.config()
                .socketTimeout((int) Duration.ofHours(1).toMillis());
    }
    
    public void execute(final Process process) {
        if (nonNull(process) && isNotEmpty(process.getOperations())) {
            if (process.isConcurrent()) {
                CompletableFuture
                        .allOf(process.getOperations().stream()
                                .map(operation -> CompletableFuture.runAsync(
                                        () -> execute(operation,
                                                process.getCategory()),
                                        this.executor))
                                .toArray(CompletableFuture[]::new))
                        .exceptionally(e -> {
                            if (process
                                    .getCategory() == ProcessCategory.PREPROCESS) {
                                throw DataXException.asDataXException(
                                        PREPROCESS_OPERATION_ERROR,
                                        e.getMessage(), e);
                            } else {
                                throw DataXException.asDataXException(
                                        POSTPROCESS_OPERATION_ERROR,
                                        e.getMessage(), e);
                            }
                        }).join();
            } else {
                process.getOperations().forEach(
                        operation -> execute(operation, process.getCategory()));
            }
        }
    }

    /**
     *  TODO check json value to determine if the request succeeds
     * @param operation
     * @param category
     * @return
     */
    public HttpResponse<String> execute(final Operation operation,
            final ProcessCategory category) {
        HttpRequestWithBody requestBuilder = this.unirest
                .request(operation.getMethod(), operation.getUrl());
        if (MapUtils.isNotEmpty(operation.getHeaders())) {
            for (final String header : operation.getHeaders().keySet()) {
                requestBuilder = requestBuilder.header(header,
                        operation.getHeaders().get(header));
            }
        }
        if (!emptyIfNull(operation.getHeaders()).containsKey(CONTENT_TYPE)) {
            requestBuilder = requestBuilder.header(CONTENT_TYPE,
                    APPLICATION_JSON.getMimeType());
        }
        HttpRequest<?> request = requestBuilder;
        if (HttpMethod.valueOf(operation.getMethod()) != GET
                && isNotBlank(operation.getBody())) {
            if (operation.isBase64()) {
                request = requestBuilder
                        .body(Base64.getDecoder().decode(operation.getBody()));
            } else {
                request = requestBuilder.body(operation.getBody());
            }
            if (operation.isDebug()) {
                log.info(
                        "request {} method {} has body: {}, base64 encoded?{}, decoded body: {}",
                        operation.getUrl(), operation.getMethod(),
                        operation.getBody(), operation.isBase64(),
                        operation.isBase64()
                                ? Base64.getDecoder()
                                        .decode(operation.getBody())
                                : EMPTY);
            }
        }
        final long startTime = System.nanoTime();
        return request.asString().ifSuccess(response -> log.info(
                "operation {} category: {} execute successfully,response: {}, body: {}, consume time: {}",
                operation.getUrl(), category, response.getStatusText(),
                response.getBody(),
                Duration.ofNanos(System.nanoTime() - startTime)))
                .ifFailure(response -> {
                    response.getParsingError().ifPresent(e -> {
                        log.error(
                                "operation {} category: {} execute failed, original body: {}, parsing exception",
                                operation.getUrl(),
                                category.name().toLowerCase(),
                                e.getOriginalBody(), e);
                        throw new OperationExecutionFailException(String.format(
                                "operation %s category: %s execute failed",
                                operation.getUrl(),
                                category.name().toLowerCase()), e);
                    });
                    log.error("operation {} category: {} execute failed, "
                            + "http code: {}, message: {} , optional reason: {}",
                            operation.getUrl(), category.name().toLowerCase(),
                            response.getStatus(), response.getStatusText(),
                            response.getBody());
                    throw new OperationExecutionFailException(String.format(
                            "operation %s category: %s http execute failed, http code: %d, message: %s, optional reason: %s",
                            operation.getUrl(), category.name().toLowerCase(),
                            response.getStatus(), response.getStatusText(),
                            response.getBody()), null);
                });
    }
}
