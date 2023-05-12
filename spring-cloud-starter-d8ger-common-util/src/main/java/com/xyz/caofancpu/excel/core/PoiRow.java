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
import com.xyz.caofancpu.excel.core.face.Styleable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel的行映射对象
 *
 * @author D8GER
 * @author guanxiaochen
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class PoiRow extends Node implements Area, Styleable {
    private Integer row = 1;

    /**
     * 样式
     */
    private PoiStyle style;

    /**
     * Column属性的全部定义
     */
    private List<D8Cell> d8Cells = new ArrayList<>();

    public PoiRow() {}

    public PoiRow(int row) {
        this.row = row;
    }

    public PoiRow(int rowCount, int columnCount, String value) {
        this(rowCount);
        addCell(new D8Cell(value).setCel(columnCount));
    }

    public PoiRow(int row, String... values) {
        this(row);
        for (String value : values) {
            addCell(value);
        }
    }

    public PoiRow(int row, int cel, String... values) {
        this(row);
        for (String value : values) {
            addCell(cel, value);
        }
    }

    public D8Cell addCell(D8Cell d8Cell) {
        d8Cell.parent(this);
        d8Cells.add(d8Cell);
        return d8Cell;
    }

    public D8Cell addCell(String value) {
        return addCell(new D8Cell(value));
    }

    public D8Cell addCell(int cel, String value) {
        return addCell(new D8Cell(value).setCel(cel));
    }

    public PoiRow addCells(String... values) {
        for (String value : values) {
            addCell(new D8Cell(value));
        }
        return this;
    }

    public PoiRow addCells(int cel, String... values) {
        for (String value : values) {
            addCell(cel, value);
        }
        return this;
    }


    @Getter
    @Accessors(chain = true)
    public static class D8Cell extends Node implements Styleable {
        private final Object value;
        @Setter
        private Integer cel = 1;
        /**
         * 样式
         */
        @Setter
        private PoiStyle style;
        /**
         * cell的宽度
         */
        @Setter
        private Float columnWidth;

        public D8Cell(String value) {
            this.value = value;
        }
    }
}
