package com.xyz.caofancpu.mvc.interceptor;

import com.xyz.caofancpu.constant.SymbolConstantUtil;
import com.xyz.caofancpu.core.CollectionUtil;
import com.xyz.caofancpu.core.JSONUtil;
import com.xyz.caofancpu.logger.LogIpConfigUtil;
import com.xyz.caofancpu.mvc.common.HttpStaticHandleUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Arrays;
import java.util.Map;

/**
 * Web接口日志拦截切面
 */
@Component
@Aspect
@Order(2)
@Slf4j
public class WebLogAspect {
    /**
     * 日志切面
     */
    @Pointcut("execution(public * com.xyz..*.controller..*Controller.*(..))")
    public void webLog() {
        // do something
        /**
         try {
         try {
         // 对应@Before注解的方法切面逻辑
         doBefore();
         method.invoke();
         } finally {
         // 对应@After注解的方法切面逻辑
         doAfter();
         }
         // 对应@AfterReturning注解的方法切面逻辑
         doAfterReturning();
         } catch (Exception e) {
         //对应@AfterThrowing注解的方法切面逻辑
         doAfterThrowing();
         }
         */
    }

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        HttpServletRequest request = HttpStaticHandleUtil.loadRequest();
        String requestInterface = joinPoint.getSignature().getDeclaringTypeName()
                + "."
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
            log.info("入参为文件(InputStreamSource)或HttpRequest等类型, 打印对象地址信息");
            requestBody = Arrays.toString(joinPoint.getArgs());
        }
        StringBuilder requestSb = new StringBuilder();
        requestSb.append("\n[前端页面请求]")
                .append("\n请求IP=").append(LogIpConfigUtil.getIpAddress())
                .append("\n请求方式=").append(request.getMethod())
                .append("\n请求地址=").append(request.getRequestURL().toString())
                .append("\n请求接口=").append(requestInterface)
                .append("\n请求Param参数=").append(requestParam)
                .append("\n请求Body对象=").append(requestBody)
                .append("\n");
        log.info(requestSb.toString());
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
        log.error("后台接口[{}]抛出异常:{}", getInterfaceFullName(joinPoint), ex.getMessage());
    }

    @AfterReturning(value = "webLog()", returning = "returnValue")
    public void doAfterReturning(JoinPoint joinPoint, Object returnValue) {
        String requestInterface = getInterfaceFullName(joinPoint);
        StringBuilder responseSb = new StringBuilder();
        responseSb.append("\n[后台响应结果]:")
                .append("\n后台接口=").append(requestInterface)
                .append("\n响应数据结果:").append(JSONUtil.formatStandardJSON(JSONUtil.toJSONStringWithDateFormat(returnValue)));
        log.info(responseSb.toString());
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