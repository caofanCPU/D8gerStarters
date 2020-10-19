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

package com.xyz.caofancpu.excel.core;

import com.xyz.caofancpu.excel.core.face.Area;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 分隔符对象
 *
 * @author D8GER
 * @author guanxiaochen
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class Split extends Node implements Area {
    private Integer split;
    /**
     * cell的宽度
     */
    private Float columnWidth;

    /**
     * @param split 分隔空白的行或者列数
     * @see Align
     */
    public Split(Integer split) {
        this.split = split;
    }
}
