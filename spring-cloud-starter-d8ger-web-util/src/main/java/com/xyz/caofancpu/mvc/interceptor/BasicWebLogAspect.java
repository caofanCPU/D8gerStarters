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
import com.xyz.caofancpu.logger.LogIpConfigUtil;
import com.xyz.caofancpu.logger.LoggerUtil;
import com.xyz.caofancpu.mvc.common.HttpStaticHandleUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Arrays;
import java.util.Map;

/**
 * Web接口日志拦截切面
 */
public class BasicWebLogAspect {

    private final Logger log;

    private final Logger errorLog;

    public BasicWebLogAspect(Logger log, Logger errorLog) {
        this.log = log;
        this.errorLog = errorLog;
    }

    /**
     * Http切面
     */
    public void webLog() {
        // do something
        /**
         * try {
         *     try {
         *         // 对应@Before注解的方法切面逻辑
         *         doBefore();
         *         method.invoke();
         *     } finally {
         *       // 对应@After注解的方法切面逻辑
         *         doAfter();
         *     }
         *     // 对应@AfterReturning注解的方法切面逻辑
         *     doAfterReturning();
         * } catch (Exception e) {
         *     //对应@AfterThrowing注解的方法切面逻辑
         *     doAfterThrowing();
         * }
         */
    }

    @Before("webLog()")
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
        String requestSb = "\n[前端页面请求]" +
                "\n请求IP=" + LogIpConfigUtil.getIpAddress() +
                "\n请求方式=" + request.getMethod() +
                "\n请求地址=" + request.getRequestURL().toString() +
                "\n请求接口=" + requestInterface +
                "\n请求Param参数=" + requestParam +
                "\n请求Body对象=" + requestBody +
                "\n";
        LoggerUtil.info(log, "请求数据", "HttpRequest", requestSb);
    }

    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint)
            throws Throwable {
        String requestInterface = getInterfaceFullName(proceedingJoinPoint);
        long startTime = System.currentTimeMillis();
        Object result;
        try {
            result = proceedingJoinPoint.proceed();
        } finally {
            logCostTime(startTime, requestInterface);
        }
        return result;
    }

    @AfterThrowing(value = "webLog()", throwing = "ex")
    public void doAfterThrowingAdvice(JoinPoint joinPoint, Throwable ex) {
        LoggerUtil.error(errorLog, "接口处理异常", getInterfaceFullName(joinPoint), ex.getMessage());
    }

    @AfterReturning(value = "webLog()", returning = "returnValue")
    public void doAfterReturning(JoinPoint joinPoint, Object returnValue) {
        String requestInterface = getInterfaceFullName(joinPoint);
        String responseSb = "\n[后台响应结果]:" +
                "\n后台接口=" + requestInterface +
                "\n响应数据结果:" + JSONUtil.formatStandardJSON(JSONUtil.toJSONStringWithDateFormat(returnValue));
        LoggerUtil.info(log, "请求数据", "HttpResponse", responseSb);
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
    private void logCostTime(long startTime, String methodPath) {
        long costTime = System.currentTimeMillis() - startTime;
        log.info("后台接口[{}]处理耗时: {}ms", methodPath, costTime);
    }

}