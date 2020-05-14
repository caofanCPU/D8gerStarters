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
        this.code = GlobalErrorInfoEnum.OPERATE_FAILED_MSG.getCode();
        this.msg = GlobalErrorInfoEnum.OPERATE_FAILED_MSG.getMsg();
    }

    public GlobalErrorInfoRuntimeException(String msg) {
        this.code = GlobalErrorInfoEnum.OPERATE_FAILED_MSG.getCode();
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
