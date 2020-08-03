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

import com.xyz.caofancpu.excel.core.face.Column;
import com.xyz.caofancpu.excel.core.face.Titleable;
import com.xyz.caofancpu.excel.util.PoiUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class PoiColumnItems<T, V> extends Node implements Titleable, Column<T> {
    protected ItemFunction<V, String[]> titles;
    protected ItemFunction<T, Collection<V>> itemFunction;
    private int dataIndex = 1;
    /**
     * 标题样式
     */
    private PoiStyle titleStyle;
    /**
     * Column属性的全部定义
     */
    private List<Column<V>> columns = new ArrayList<>();

    /**
     * 无标题列
     *
     * @param itemFunction 获取值的Function, 空去默认, 无默认值为""
     */
    public PoiColumnItems(ItemFunction<T, Collection<V>> itemFunction) {
        this.titles = PoiUtil.emptyTitle();
        this.itemFunction = itemFunction;
    }

    public PoiColumnItems(ItemFunction<T, Collection<V>> itemFunction, String... title) {
        this.titles = t1 -> title;
        this.itemFunction = itemFunction;
    }

    /**
     * @param itemFunction 获取值的Function, 空去默认, 无默认值为""
     * @param titles       标题,可以返回多行,每个是一行
     */
    public PoiColumnItems(ItemFunction<T, Collection<V>> itemFunction, ItemFunction<V, String[]> titles) {
        this.titles = titles;
        this.itemFunction = itemFunction;
    }

    public <V2> PoiColumnItems<V, V2> addColumns(ItemFunction<V, Collection<V2>> valueFunction, ItemFunction<V2, String[]> titles) {
        return addColumns(new PoiColumnItems<>(valueFunction, titles));
    }

    public <V2> PoiColumn<V> addColumn(ItemFunction<V, V2> valueFunction, ItemFunction<V, String[]> titles) {
        return addColumn(new PoiColumn<>(valueFunction, titles));
    }

    public <V2> PoiColumn<V> addColumn(ItemFunction<V, V2> valueFunction, String... titles) {
        return addColumn(new PoiColumn<>(valueFunction, item -> titles));
    }

    public <V2> PoiColumnItems<V, V2> addColumns(PoiColumnItems<V, V2> poiColumns) {
        poiColumns.parent(this);
        poiColumns.setDataIndex(dataIndex);
        columns.add(poiColumns);
        return poiColumns;
    }

    public PoiColumn<V> addColumn(PoiColumn<V> column) {
        column.parent(this);
        column.setDataIndex(dataIndex);
        columns.add(column);
        return column;
    }
}
