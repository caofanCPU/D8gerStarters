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

package com.xyz.caofancpu.excel.xml.config.field;

import lombok.Data;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Excel字段定义
 */
@Data
public class DecimalField extends FieldConfig {
    /**
     * DecimalFormat pattern 只对Number类型有效
     */
    private String decimalFormatPattern;
    /**
     * DecimalFormat实例,不可配置,它的创建规则基于decimalFormatPattern属性
     */
    private DecimalFormat decimalFormat;
    /**
     * DecimalFormat实例,RoundingMode ,当处理字符时,假设保留2位小数,那么遇到3位甚至更多的位数如何处理？通过该配置可以指定处理方式,默认向下取整
     */
    private RoundingMode roundingMode = RoundingMode.DOWN;

}
