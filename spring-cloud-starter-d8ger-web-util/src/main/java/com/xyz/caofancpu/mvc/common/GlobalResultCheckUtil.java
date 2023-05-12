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

package com.xyz.caofancpu.mvc.common;

import com.xyz.caofancpu.result.D8Response;
import com.xyz.caofancpu.result.GlobalErrorInfoEnum;
import com.xyz.caofancpu.result.GlobalErrorInfoException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 全局结果检查处理工具
 *
 * @author D8GER
 */
@Slf4j
public class GlobalResultCheckUtil {
    /**
     * 远程调用接口调用统一处理
     * 响应成功, 不做任何处理
     * 响应失败, 优先返回远程调用的提示信息, 远程调用没有提示信息时, 统一提示信息为"接口调用失败"
     *
     * @param d8Response
     * @throws GlobalErrorInfoException
     */
    public static <T> void handleResult(D8Response<T> d8Response)
            throws GlobalErrorInfoException {
        if (Objects.isNull(d8Response) || d8Response.ifSuccess()) {
            return;
        }
        if (StringUtils.isEmpty(d8Response.getCode())) {
            d8Response.setCode(GlobalErrorInfoEnum.OPERATE_FAILED.getCode());
        }
        if (StringUtils.isEmpty(d8Response.getMsg())) {
            d8Response.setMsg(GlobalErrorInfoEnum.OPERATE_FAILED.getMsg());
        }
        throw new GlobalErrorInfoException(d8Response.getCode(), d8Response.getMsg());
    }

}
