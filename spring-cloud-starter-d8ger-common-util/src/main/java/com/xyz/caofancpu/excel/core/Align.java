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
import com.xyz.caofancpu.excel.enums.ListAlign;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel定义
 */
@SuppressWarnings("rawtypes")
@EqualsAndHashCode(callSuper = true)
@Data
public class Align extends Node implements Area {
    private ListAlign align;
    private List<Area> areas = new ArrayList<>();

    /**
     * @param align 排列方式,水平或上下
     */
    public Align(ListAlign align) {
        this.align = align;
    }

    protected <T extends PoiTable> T addTable(T table) {
        table.parent(this);
        areas.add(table);
        return table;
    }

    protected PoiRow addRow(String... values) {
        return addRow(new PoiRow(1, values));
    }

    protected PoiRow addRow(PoiRow row) {
        row.parent(this);
        areas.add(row);
        return row;
    }

    protected Split addSplit(Integer split) {
        return addSplit(new Split(split));
    }

    protected Split addSplit(Split split) {
        split.parent(this);
        areas.add(split);
        return split;
    }

    protected Align addAlign(Align align) {
        align.parent(this);
        areas.add(align);
        return align;
    }
}
