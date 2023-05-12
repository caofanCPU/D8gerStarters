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
