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

package com.xyz.caofancpu.excel.xml.config;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel定义
 *
 * @author D8GER
 * @author guanxiaochen
 */
@Data
@Accessors(chain = true)
public class RowConfig implements IAreaConfig {
    private Integer row = 1;

    /**
     * 校验是否展示
     **/
    private String filter;

    /**
     * 样式
     */
    private PoiStyleConfig style;

    /**
     * Field属性的全部定义
     */
    private List<D8Cell> cellList = new ArrayList<>();

    public void setRow(Integer row) {
        if (row != null && row > 0) {
            this.row = row;
        }
    }

    @Data
    public class D8Cell {
        private Integer row = RowConfig.this.row;
        private Integer cell = 1;
        private String value;

        /**
         * 校验是否展示
         **/
        private String filter;

        /**
         * 样式
         */
        private PoiStyleConfig style;

        /**
         * cell的宽度
         */
        private Integer columnWidth;

        /**
         * 当值为空时,字段的默认值
         */
        private String defaultValue;

        public void setRow(Integer row) {
            if (row != null && row > 0) {
                this.row = row;
            }
        }

        public void setCell(Integer cell) {
            if (cell != null && cell > 0) {
                this.cell = cell;
            }
        }
    }
}
