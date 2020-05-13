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

import com.xyz.caofancpu.result.D8Response;
import com.xyz.caofancpu.result.GlobalErrorInfoEnum;
import com.xyz.caofancpu.result.GlobalErrorInfoException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Controller全局异常处理拦截器
 *
 * @author D8GER
 */
@RestControllerAdvice
public class GlobalErrorInfoHandler {
    /**
     * For params which is failed by verification, commonly used for param messaging
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = {HttpMessageNotReadableException.class, MethodArgumentNotValidException.class})
    public D8Response<Object> handleParamTypeJSONParseException(HttpMessageNotReadableException ex) {
        return D8Response.fail(GlobalErrorInfoEnum.PARA_ERROR);
    }

    /**
     * For business exception, commonly used for messaging
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = GlobalErrorInfoException.class)
    public D8Response<Object> handleCustomerException(GlobalErrorInfoException ex) {
        return D8Response.fail(ex.getCode(), ex.getMsg());
    }

    /**
     * For internal error, commonly used for unexpected cases
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = RuntimeException.class)
    public D8Response<Object> handleRuntimeException(RuntimeException ex) {
        return D8Response.fail(GlobalErrorInfoEnum.INTERNAL_ERROR);
    }
}
