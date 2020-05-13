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
