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
import com.xyz.caofancpu.logger.LoggerUtil;
import com.xyz.caofancpu.result.D8Response;
import com.xyz.caofancpu.result.GlobalErrorInfoEnum;
import com.xyz.caofancpu.result.GlobalErrorInfoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.List;

/**
 * Controller全局异常处理拦截器
 *
 * @author D8GER
 */
@Slf4j
public class D8GlobalExceptionSupport<T> {
    /**
     * For params which is failed by verification, commonly used for param messaging
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class, HttpMessageNotReadableException.class, MethodArgumentNotValidException.class, MissingServletRequestParameterException.class, ConstraintViolationException.class})
    public D8Response<T> handleParamTypeJSONParseException(Exception ex, HttpServletRequest request) {
        LoggerUtil.info(log, "请求参数异常", "url", request.getServletPath(), "异常原因", ex.getMessage());
        String errorMsg = GlobalErrorInfoEnum.PARA_ERROR.getMessage();
        if (ex instanceof HttpRequestMethodNotSupportedException) {
            errorMsg = "接口调用方式错误";
        } else if (ex instanceof HttpMessageNotReadableException) {
            errorMsg = "请求参数错误, 请检查JSON数据格式";
        } else if (ex instanceof MissingServletRequestParameterException) {
            errorMsg = "缺少必要的URL请求参数";
        } else if (ex instanceof ConstraintViolationException) {
            errorMsg = "请求参数不满足约束条件";
        } else if (ex instanceof MethodArgumentNotValidException) {
            List<String> messageList = CollectionUtil.transToList(((MethodArgumentNotValidException) ex).getBindingResult().getFieldErrors(), FieldError::getDefaultMessage);
            errorMsg = CollectionUtil.join(messageList, SymbolConstantUtil.ENGLISH_SEMICOLON);
        }
        return D8Response.fail(GlobalErrorInfoEnum.PARA_ERROR.getCode(), errorMsg);
    }

    /**
     * For business exception, commonly used for messaging
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = GlobalErrorInfoException.class)
    public D8Response<T> handleCustomerException(GlobalErrorInfoException ex, HttpServletRequest request) {
        LoggerUtil.info(log, "业务异常", "url", request.getServletPath(), "异常原因", ex.getMessage());
        return D8Response.fail(ex.getCode(), ex.getMsg());
    }

    /**
     * For internal error, commonly used for unexpected cases
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = {RuntimeException.class, Throwable.class})
    public D8Response<T> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        LoggerUtil.info(log, "服务器内部错误", "url", request.getServletPath(), "错误原因", ex.getMessage());
        return D8Response.fail(GlobalErrorInfoEnum.INTERNAL_ERROR);
    }
}
