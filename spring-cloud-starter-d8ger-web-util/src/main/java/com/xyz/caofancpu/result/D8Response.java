package com.xyz.caofancpu.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

/**
 * 响应基类
 *
 * @author D8GER
 */
@ApiModel
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
        D8Response<T> d8Response = new D8Response<>();
        d8Response.setCode(GlobalErrorInfoEnum.GLOBAL_MSG.getCode());
        d8Response.setMsg(StringUtils.isBlank(errorMsg) ? GlobalErrorInfoEnum.GLOBAL_MSG.getMsg() : errorMsg);
        return d8Response;
    }

    public static <T> D8Response<T> fail(ErrorInfoInterface errorInfo) {
        D8Response<T> d8Response = new D8Response<>();
        d8Response.setCode(errorInfo.getCode());
        d8Response.setMsg(errorInfo.getMsg());
        return d8Response;
    }

    public static <T> D8Response<T> fail(String code, String errorMsg) {
        D8Response<T> d8Response = new D8Response<>();
        d8Response.setCode(StringUtils.isBlank(code) ? GlobalErrorInfoEnum.GLOBAL_MSG.getCode() : code);
        d8Response.setMsg(StringUtils.isBlank(errorMsg) ? GlobalErrorInfoEnum.GLOBAL_MSG.getMsg() : errorMsg);
        return d8Response;
    }

    /**
     * 接口调用成功, 返回指定数据
     *
     * @param data
     * @return
     */
    public static <T> D8Response<T> success(T data) {
        D8Response<T> d8Response = new D8Response<>();
        d8Response.setCode(GlobalErrorInfoEnum.SUCCESS.getCode());
        d8Response.setMsg(GlobalErrorInfoEnum.SUCCESS.getMsg());
        d8Response.setData(data);
        return d8Response;
    }

    /**
     * 接口调用成功, 返回指定数据及提示消息
     *
     * @param data
     * @param successMsg
     * @return
     */
    public static <T> D8Response<T> success(T data, String successMsg) {
        D8Response<T> d8Response = new D8Response<>();
        d8Response.setCode(GlobalErrorInfoEnum.SUCCESS.getCode());
        d8Response.setData(data);
        d8Response.setMsg(successMsg);
        return d8Response;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Boolean ifSuccess() {
        return GlobalErrorInfoEnum.SUCCESS.getCode().equals(this.code);
    }

}
