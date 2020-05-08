package com.xyz.caofancpu.result;

import lombok.Getter;

/**
 * 自定义错误封装信息
 *
 * @author D8GER
 */
public class CustomerErrorInfo implements ErrorInfoInterface {

    @Getter
    private final String code;

    @Getter
    private final String msg;

    public CustomerErrorInfo(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public CustomerErrorInfo(String msg) {
        this.code = GlobalErrorInfoEnum.GLOBAL_MSG.getCode();
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return this.getMsg();
    }
}
