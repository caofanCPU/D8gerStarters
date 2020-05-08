package com.xyz.caofancpu.mvc.interceptor;

import com.xyz.caofancpu.result.D8Response;
import com.xyz.caofancpu.result.GlobalErrorInfoEnum;
import com.xyz.caofancpu.result.GlobalErrorInfoException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
