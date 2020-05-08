package com.xyz.caofancpu.result;

/**
 * 错误信息顶层接口
 *
 * @author D8GER
 */
public interface ErrorInfoInterface {

    String getCode();

    String getMsg();

    /**
     * 兼容底层框架内部记录异常信息
     *
     * @return
     */
    String getMessage();
}
