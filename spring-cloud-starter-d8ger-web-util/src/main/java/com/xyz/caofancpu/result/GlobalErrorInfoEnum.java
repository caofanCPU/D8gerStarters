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

import lombok.Getter;

/**
 * 全局统一错误枚举
 *
 * @author D8GER
 */
public enum GlobalErrorInfoEnum implements ErrorInfoInterface {
    SUCCESS("200", "成功"),
    INTERNAL_ERROR("500", "服务器内部错误"),
    NOT_FOUND("404", "资源不存在"),
    PARA_ERROR("501", "请求参数错误"),
    REMOTE_INVOKE_FAILED_MSG("998", "远程调用失败, 请重试"),
    OPERATE_FAILED_MSG("999", "请求处理失败, 请重试"),

    ;

    @Getter
    private final String code;

    @Getter
    private final String msg;

    GlobalErrorInfoEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return this.getMsg();
    }
}
