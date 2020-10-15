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
import com.xyz.caofancpu.excel.tmp.Tmp;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Accessors(chain = true)
public class PoiSheet extends Node {

    private final String name;
    private final List<Area> areas = new ArrayList<>();
    /**
     * sheet样式: 边距
     *
     * @see org.apache.poi.ss.usermodel.Sheet#setMargin(short, double)
     */
    private final List<Tmp<Short, Double>> margins = new ArrayList<>();
    /**
     * sheet样式: 列宽
     *
     * @see org.apache.poi.ss.usermodel.Sheet#setColumnWidth(int, int)
     */
    private final List<Tmp<Integer, Integer>> columnWidths = new ArrayList<>();
    @Setter
    private ListAlign align = ListAlign.DOWN;
    /**
     * sheet打印样式
     *
     * @see org.apache.poi.ss.usermodel.PrintSetup#setScale(short)
     */
    @Setter
    private Short printSetupScale;
    /**
     * sheet打印样式
     *
     * @see org.apache.poi.ss.usermodel.PrintSetup#setPaperSize(short)
     */
    @Setter
    private Short printSetupPaperSize;

    public PoiSheet(String name) {
        this.name = name;
    }

    public PoiSheet(String name, ListAlign align) {
        this.name = name;
        if (align != null) {
            this.align = align;
        }
    }

    public <F, T extends PoiTable<F>> T addTable(T table) {
        table.parent(this);
        areas.add(table);
        return table;
    }

    public PoiRow addRow(String... values) {
        return addRow(new PoiRow(1, values));
    }

    public PoiRow addRow(PoiRow row) {
        row.parent(this);
        areas.add(row);
        return row;
    }

    public Split addSplit(Integer split) {
        return addSplit(new Split(split));
    }

    protected Split addSplit(Split split) {
        split.parent(this);
        areas.add(split);
        return split;
    }

    public Align addAlign(Align align) {
        align.parent(this);
        areas.add(align);
        return align;
    }

    public PoiSheet addMargins(short margin, double size) {
        this.margins.add(new Tmp<>(margin, size));
        return this;
    }

    public PoiSheet addColumnWidth(int columnIndex, int width) {
        this.columnWidths.add(new Tmp<>(columnIndex, width));
        return this;
    }
}
