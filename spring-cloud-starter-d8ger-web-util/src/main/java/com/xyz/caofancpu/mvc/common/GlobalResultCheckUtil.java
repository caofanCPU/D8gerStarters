package com.xyz.caofancpu.mvc.common;

import com.xyz.caofancpu.result.D8Response;
import com.xyz.caofancpu.result.GlobalErrorInfoException;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 全局结果检查处理工具
 *
 * @author D8GER
 */
@Slf4j
public class GlobalResultCheckUtil {
    /**
     * 微服务接口调用统一处理
     * 响应成功, 不做任何处理
     * 响应失败, 优先返回微服务的提示信息, 微服务没有提示信息时, 统一提示信息为"接口调用失败"
     *
     * @param d8Response
     * @throws GlobalErrorInfoException
     */
    public static <T> void handleMSResultBody(D8Response<T> d8Response)
            throws GlobalErrorInfoException {
        if (Objects.isNull(d8Response) || d8Response.ifSuccess()) {
            return;
        }
        throw new GlobalErrorInfoException(d8Response.getCode(), d8Response.getMsg());
    }

}
