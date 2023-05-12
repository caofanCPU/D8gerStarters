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

package com.xyz.caofancpu.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 响应基类
 *
 * @author D8GER
 */
@ApiModel
@Data
@Accessors(chain = true)
public class D8Response<T> implements Serializable {
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
        return new D8Response<T>().setCode(GlobalErrorInfoEnum.OPERATE_FAILED.getCode()).setMsg(StringUtils.isBlank(errorMsg) ? GlobalErrorInfoEnum.OPERATE_FAILED.getMsg() : errorMsg);
    }

    public static <T> D8Response<T> fail(ErrorInfoInterface errorInfo) {
        return new D8Response<T>().setCode(errorInfo.getCode()).setMsg(errorInfo.getMsg());
    }

    public static <T> D8Response<T> fail(String code, String errorMsg) {
        return new D8Response<T>().setCode(StringUtils.isBlank(code) ? GlobalErrorInfoEnum.OPERATE_FAILED.getCode() : code).setMsg(StringUtils.isBlank(errorMsg) ? GlobalErrorInfoEnum.OPERATE_FAILED.getMsg() : errorMsg);
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
        return GlobalErrorInfoEnum.SUCCESS.getCode().equals(this.code) || GlobalErrorInfoEnum.REMOTE_INVOKE_SUCCESS.getCode().equals(this.code);
    }

}
