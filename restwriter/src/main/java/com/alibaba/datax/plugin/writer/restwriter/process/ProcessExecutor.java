package com.alibaba.datax.plugin.writer.restwriter.process;

import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.datax.common.exception.DataXException;
import com.alibaba.datax.plugin.writer.restwriter.conf.Operation;
import com.alibaba.datax.plugin.writer.restwriter.conf.Process;

import kong.unirest.HttpRequest;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;
import lombok.extern.slf4j.Slf4j;

import static com.alibaba.datax.plugin.writer.restwriter.RestWriterErrorCode.PREPROCESS_OPERATION_ERROR;
import static java.util.Objects.nonNull;
import static kong.unirest.ContentType.APPLICATION_JSON;
import static kong.unirest.HeaderNames.CONTENT_TYPE;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.MapUtils.emptyIfNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2023/10/12 15:01
 * @description:
 **/
@Slf4j
public class ProcessExecutor {
    
    private static final UnirestInstance UNIREST;
    
    private static final Executor executor = Executors
            .newWorkStealingPool(Runtime.getRuntime().availableProcessors());
    
    static {
        UNIREST = Unirest.spawnInstance();
        UNIREST.config().addShutdownHook(true);
        UNIREST.config().verifySsl(false);
        UNIREST.config().automaticRetries(true);
        UNIREST.config().connectTimeout((int) Duration.ofHours(1).toMillis());
        UNIREST.config().socketTimeout((int) Duration.ofHours(1).toMillis());
    }
    
    public static void execute(final Process process,
            final ProcessCategory category) {
        if (nonNull(process) && isNotEmpty(process.getOperations())) {
            if (process.isConcurrent()) {
                CompletableFuture
                        .allOf(process.getOperations().stream()
                                .map(operation -> CompletableFuture.runAsync(
                                        () -> execute(operation, category),
                                        executor))
                                .toArray(CompletableFuture[]::new))
                        .exceptionally(e -> {
                            throw DataXException.asDataXException(
                                    PREPROCESS_OPERATION_ERROR, e.getMessage(),
                                    e);
                        }).join();
            } else {
                process.getOperations()
                        .forEach(operation -> execute(operation, category));
            }
        }
    }
    
    public static void execute(final Operation operation,
            final ProcessCategory category) {
        HttpRequestWithBody requestBuilder = UNIREST
                .request(operation.getMethod(), operation.getUrl());
        if (MapUtils.isNotEmpty(operation.getHeaders())) {
            for (final String header : operation.getHeaders().keySet()) {
                requestBuilder = requestBuilder.header(header,
                        operation.getHeaders().get(header));
            }
        }
        if (emptyIfNull(operation.getHeaders()).containsKey(CONTENT_TYPE)) {
            UNIREST.config().addDefaultHeader(CONTENT_TYPE,
                    APPLICATION_JSON.getMimeType());
        }
        HttpRequest<?> request = requestBuilder;
        if (!StringUtils.equals(operation.getMethod(), "get")
                && isNotBlank(operation.getBody())) {
            if (operation.isBase64()) {
                request = requestBuilder
                        .body(Base64.getDecoder().decode(operation.getBody()));
            } else {
                request = requestBuilder.body(operation.getBody());
            }
        }
        final long startTime = System.nanoTime();
        request.asString().ifSuccess(response -> log.info(
                "operation {} category: {} execute successfully,response: {}, body: {}, consume time: {}",
                operation.getUrl(), category, response.getStatusText(),
                response.getBody(),
                Duration.ofNanos(System.nanoTime() - startTime)))
                .ifFailure(response -> {
                    log.error("operation {} category: {} execute failed, "
                            + "http code: {}, message: {} , optional reason: {}",
                            operation.getUrl(), category.name().toLowerCase(),
                            response.getStatus(), response.getStatusText(),
                            response.getBody());
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
                });
    }
}
