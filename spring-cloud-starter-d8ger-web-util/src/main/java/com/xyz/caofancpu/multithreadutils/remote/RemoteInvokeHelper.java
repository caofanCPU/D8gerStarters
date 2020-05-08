package com.xyz.caofancpu.multithreadutils.remote;

import com.xyz.caofancpu.logger.LoggerUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 远程调用处理封装
 *
 * @author D8GER
 */
@Data
@Accessors(chain = true)
@Slf4j
public class RemoteInvokeHelper<M> implements RemoteInvoke<M> {

    private Object serviceInstance;
    private Object params;
    private String methodName;
    private Class<?>[] paramClass;

    public RemoteInvokeHelper(Object serviceInstance, String methodName, Object param, Class<?>... paramClass) {
        this.serviceInstance = serviceInstance;
        this.methodName = methodName;
        this.paramClass = paramClass;
        this.params = param;
    }

    @Override
    @SuppressWarnings("unchecked")
    public M invoke() {
        Method method = getInvokeMethod();
        try {
            Object obj = method.invoke(serviceInstance, params);
            return (M) obj;
        } catch (IllegalAccessException | InvocationTargetException e) {
            LoggerUtil.error(log, "远程调用反射出错", e);
        }
        return null;
    }

    private Method getInvokeMethod() {
        try {
            return serviceInstance.getClass().getMethod(methodName, paramClass);
        } catch (NoSuchMethodException e) {
            LoggerUtil.error(log, "远程调用反射出错", e);
            throw new RuntimeException("远程调用反射出错");
        }
    }
}
