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
 * 全局统一运行时枚举
 *
 * @author D8GER
 */
public enum GlobalErrorInfoRuntimeEnum implements ErrorInfoInterface {
    NullPointerException("RE_NPE01", "空指针错误"),
    ClassCastException("RE_CCE02", "类型转换失败"),
    IllegalArgumentException("RE_IAE03", "入参非法"),
    ArithmeticException("RE_AE04", "算术运算错误"),
    ArrayStoreException("RE_ASE05", "数组元素类型不兼容"),
    IndexOutOfBoundsException("RE_IOBE06", "数组索引越界"),
    NegativeArraySizeException("RE_NASE07", "初始容量大小非负"),
    NumberFormatException("RE_NFE08", "数字格式错误"),
    SecurityException("RE_SE09", "安全异常"),
    UnsupportedOperationException("RE_UOE10", "操作不被支持"),

    ;

    @Getter
    private final String code;

    @Getter
    private final String msg;

    GlobalErrorInfoRuntimeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    @Override
    public String getMessage() {
        return this.getMsg();
    }
}
