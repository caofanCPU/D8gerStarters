package com.xyz.caofancpu.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 全局统一运行时异常
 *
 * @author D8GER
 */
@Data
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class GlobalErrorInfoRuntimeException extends RuntimeException implements ErrorInfoInterface {

    private String code;

    private String msg;

    public GlobalErrorInfoRuntimeException() {
        this.code = GlobalErrorInfoEnum.GLOBAL_MSG.getCode();
        this.msg = GlobalErrorInfoEnum.GLOBAL_MSG.getMsg();
    }

    public GlobalErrorInfoRuntimeException(String msg) {
        this.code = GlobalErrorInfoEnum.GLOBAL_MSG.getCode();
        this.msg = msg;
    }

    /**
     * 兼容底层框架内部记录异常信息
     *
     * @return
     */
    @Override
    public String getMessage() {
        return this.getMsg();
    }
}
