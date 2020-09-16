/*
 * Copyright 2016-2020 the original author
 *
 * @D8GER(https://github.com/caofanCPU).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xyz.caofancpu.mvc.interceptor;

import com.xyz.caofancpu.constant.SymbolConstantUtil;
import com.xyz.caofancpu.core.CollectionUtil;
import com.xyz.caofancpu.core.JSONUtil;
import com.xyz.caofancpu.logger.D8Track;
import com.xyz.caofancpu.logger.LogIpConfigUtil;
import com.xyz.caofancpu.logger.LoggerUtil;
import com.xyz.caofancpu.logger.trace.ThreadTraceUtil;
import com.xyz.caofancpu.mvc.common.HttpStaticHandleUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Web接口日志拦截切面
 *
 * @author D8GER
 */
public class D8WebLogAspectSupport {

    /**
     * 缓存日志配置
     */
    private final Map<Method, D8Track> D8TRACK_CACHE = new ConcurrentHashMap<>();
    /**
     * INFO_LOG
     */
    private final Logger log;
    /**
     * ERROR_LOG
     */
    private final Logger errorLog;
    /**
     * TIME_OUT_LOG
     */
    private final Logger timeOutLog;
    /**
     * 超时时间设置
     */
    private int timeoutThreshold = 400;

    public D8WebLogAspectSupport(Logger log, Logger errorLog, Logger timeOutLog) {
        this.log = log;
        this.errorLog = errorLog;
        this.timeOutLog = timeOutLog;
    }

    public D8WebLogAspectSupport(Logger log, Logger errorLog, Logger timeOutLog, int timeoutThreshold) {
        this(log, errorLog, timeOutLog);
        this.timeoutThreshold = timeoutThreshold;
    }

    public void doBefore(JoinPoint joinPoint) {
        HttpServletRequest request = HttpStaticHandleUtil.loadRequest();
        String requestInterface = joinPoint.getSignature().getDeclaringTypeName()
                + SymbolConstantUtil.ENGLISH_FULL_STOP
                + joinPoint.getSignature().getName();
        // 入参为文件时, 不打印log
        Map<String, Object> originRequestParamMap = HttpStaticHandleUtil.getParameterMap(request);
        Map<String, Object> filteredFileValueMap = CollectionUtil.removeSpecifiedElement(originRequestParamMap, new Class[]{MultipartFile.class, File.class});
        String requestParam = JSONUtil.formatStandardJSON(JSONUtil.toJSONStringWithDateFormat(filteredFileValueMap));
        // 入参为文件时, 不打印log
        Object[] originBodyParamArray = joinPoint.getArgs();
        Object[] filteredFileValueArray = CollectionUtil.removeSpecifiedElement(originBodyParamArray, new Class[]{MultipartFile.class, File.class});
        String requestBody;
        try {
            requestBody = JSONUtil.formatStandardJSON(JSONUtil.toJSONStringWithDateFormat(filteredFileValueArray));
        } catch (Exception e) {
            LoggerUtil.info(log, "入参为文件(InputStreamSource)或HttpRequest等类型, 打印对象地址信息");
            requestBody = Arrays.toString(joinPoint.getArgs());
        }
        // 开始线程追踪
        ThreadTraceUtil.beginTrace();
        if (printReqLog(joinPoint)) {
            String requestSb = "\n[前端页面请求]" +
                    "\n请求IP=" + LogIpConfigUtil.getRequestSourceIp() +
                    "\n请求方式=" + request.getMethod() +
                    "\n请求地址=" + request.getRequestURL().toString() +
                    "\n请求接口=" + requestInterface +
                    "\n请求Param参数=" + requestParam +
                    "\n请求Body对象=" + requestBody +
                    "\n";
            LoggerUtil.info(log, "请求数据", "HttpRequest", requestSb);
        }
    }

    public Object doAround(ProceedingJoinPoint proceedingJoinPoint)
            throws Throwable {
        String requestInterface = getInterfaceFullName(proceedingJoinPoint);
        long startTime = System.currentTimeMillis();
        Object result;
        try {
            result = proceedingJoinPoint.proceed();
        } finally {
            logCostTime(startTime, requestInterface, proceedingJoinPoint);
        }
        return result;
    }

    public void doAfterThrowingAdvice(JoinPoint joinPoint, Throwable ex) {
        // 只记录警告级别, 将异常抛出, 由外层捕获处理
        LoggerUtil.warn(log, "接口处理异常", getInterfaceFullName(joinPoint), ex.getMessage());
    }

    public void doAfterReturning(JoinPoint joinPoint, Object returnValue) {
        String requestInterface = getInterfaceFullName(joinPoint);
        if (printRespLog(joinPoint)) {
            String responseSb = "\n[后台响应结果]:" +
                    "\n后台接口=" + requestInterface +
                    "\n响应数据结果:" + JSONUtil.formatStandardJSON(JSONUtil.toJSONStringWithDateFormat(returnValue));
            LoggerUtil.info(log, "响应数据", "HttpResponse", responseSb);
        }
        // 结束线程追踪
        ThreadTraceUtil.endTrace();
    }

    /**
     * 获取处理接口的全路径名称
     *
     * @param joinPoint
     * @return
     */
    private String getInterfaceFullName(JoinPoint joinPoint) {
        return joinPoint.getSignature().getDeclaringTypeName()
                + SymbolConstantUtil.ENGLISH_FULL_STOP
                + joinPoint.getSignature().getName();
    }

    /**
     * 打印请求处理时间
     *
     * @param startTime
     * @param methodPath
     */
    private void logCostTime(long startTime, String methodPath, JoinPoint joinPoint) {
        long costTime = System.currentTimeMillis() - startTime;
        int refer = handleTimeOutThreshold(joinPoint);
        if (costTime > refer) {
            // 超时
            timeOutLog.warn("后台接口[{}]处理【超时】: {}ms", methodPath, costTime);
        } else {
            log.info("后台接口[{}]处理耗时: {}ms", methodPath, costTime);
        }
    }

    /**
     * 是否打印响应日志
     *
     * @param joinPoint
     * @return
     */
    private boolean printRespLog(JoinPoint joinPoint) {
        Optional<D8Track> optionalD8Track = extractD8Track(joinPoint);
        return optionalD8Track.isPresent() && optionalD8Track.get().recordResp();
    }

    /**
     * 是否打印请求日志
     *
     * @param joinPoint
     * @return
     */
    private boolean printReqLog(JoinPoint joinPoint) {
        Optional<D8Track> optionalD8Track = extractD8Track(joinPoint);
        return optionalD8Track.isPresent() && optionalD8Track.get().recordReq();
    }

    /**
     * 超时阈值
     *
     * @param joinPoint
     * @return
     */
    private int handleTimeOutThreshold(JoinPoint joinPoint) {
        Optional<D8Track> optionalD8Track = extractD8Track(joinPoint);
        return optionalD8Track.map(D8Track::timeOutThreshold).orElseGet(() -> this.timeoutThreshold);
    }

    /**
     * 析取个性化日志注解
     *
     * @param joinPoint
     * @return
     */
    private Optional<D8Track> extractD8Track(JoinPoint joinPoint) {
        Optional<Method> optionalMethod = extractMethod(joinPoint);
        return optionalMethod.isPresent() ? put(optionalMethod.get()) : Optional.empty();
    }

    /**
     * 析取切面方法
     *
     * @param joinPoint
     * @return
     */
    private Optional<Method> extractMethod(JoinPoint joinPoint) {
        try {
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            return Optional.of(method);
        } catch (Exception e) {
            // do nothing
            return Optional.empty();
        }
    }

    /**
     * 缓存
     *
     * @param method
     * @return
     */
    private Optional<D8Track> put(Method method) {
        D8Track annotation = D8TRACK_CACHE.get(method);
        if (Objects.nonNull(annotation)) {
            return Optional.of(annotation);
        }
        annotation = method.getAnnotation(D8Track.class);
        if (Objects.nonNull(annotation)) {
            D8TRACK_CACHE.put(method, annotation);
        }
        return Optional.empty();
    }

}