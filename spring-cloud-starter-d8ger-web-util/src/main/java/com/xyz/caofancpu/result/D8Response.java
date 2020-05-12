package com.xyz.caofancpu.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

/**
 * 响应基类
 *
 * @author D8GER
 */
@ApiModel
@Data
@Accessors(chain = true)
public class D8Response<T> {

    @ApiModelProperty(value = "状态码", example = "200", position = 1)
    private String code;
    @ApiModelProperty(value = "报错信息", example = "请求参数错误", position = 2)
    private String msg;
    @ApiModelProperty(value = "结果数据", position = 3)
    private T data;

    public D8Response() {
    }

    /**
     * 接口调用失败时, 用此方法向前端返回对应失败原因
     *
     * @param errorMsg
     * @return
     */
    public static <T> D8Response<T> fail(String errorMsg) {
        return new D8Response<T>().setCode(GlobalErrorInfoEnum.GLOBAL_MSG.getCode()).setMsg(StringUtils.isBlank(errorMsg) ? GlobalErrorInfoEnum.GLOBAL_MSG.getMsg() : errorMsg);
    }

    public static <T> D8Response<T> fail(ErrorInfoInterface errorInfo) {
        return new D8Response<T>().setCode(errorInfo.getCode()).setMsg(errorInfo.getMsg());
    }

    public static <T> D8Response<T> fail(String code, String errorMsg) {
        return new D8Response<T>().setCode(StringUtils.isBlank(code) ? GlobalErrorInfoEnum.GLOBAL_MSG.getCode() : code).setMsg(StringUtils.isBlank(errorMsg) ? GlobalErrorInfoEnum.GLOBAL_MSG.getMsg() : errorMsg);
    }

    /**
     * 接口调用成功, 返回指定数据
     *
     * @param data
     * @return
     */
    public static <T> D8Response<T> success(T data) {
        return new D8Response<T>().setCode(GlobalErrorInfoEnum.SUCCESS.getCode()).setMsg(GlobalErrorInfoEnum.SUCCESS.getMsg()).setData(data);
    }

    /**
     * 接口调用成功, 返回指定数据及提示消息
     *
     * @param data
     * @param successMsg
     * @return
     */
    public static <T> D8Response<T> success(T data, String successMsg) {
        return new D8Response<T>().setCode(GlobalErrorInfoEnum.SUCCESS.getCode()).setData(data).setMsg(successMsg);
    }

    public Boolean ifSuccess() {
        return GlobalErrorInfoEnum.SUCCESS.getCode().equals(this.code);
    }

}
