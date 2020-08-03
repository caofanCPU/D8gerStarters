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
import com.xyz.caofancpu.excel.util.PoiUtil;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Collection;
import java.util.function.Function;

@SuppressWarnings({"rawtypes", "unchecked"})
@Getter
@Accessors(chain = true)
public class PoiTable1<T1> extends PoiTable<T1> {
    protected final Collection<T1> dataCollection;

    public PoiTable1(Collection<T1> dataCollection) {
        this.dataCollection = dataCollection;
    }

    public <V> PoiColumnItems<T1, V> addColumnItems1(ItemFunction<T1, Collection<V>> valueFunction, ItemFunction<V, String[]> titles) {
        return addColumns1(new PoiColumnItems<>(valueFunction, titles));
    }

    public <V> PoiColumnItems<T1, V> addColumns1(ValueFunction<T1, Collection<V>> valueFunction, Function<V, String> title) {
        return addColumns1(new PoiColumns<>(valueFunction, title));
    }

    public <V> PoiColumn<T1> addColumnItem1(ItemFunction<T1, V> valueFunction, ItemFunction<T1, String[]> titles) {
        return addColumn1(new PoiColumn<>(valueFunction, titles));
    }

    public <V> PoiColumn<T1> addColumnItem1(ItemFunction<T1, V> valueFunction, String... titles) {
        return addColumn1(new PoiColumn<>(valueFunction, item -> titles));
    }

    public <V> PoiColumn<T1> addColumn1(ValueFunction<T1, V> valueFunction, Function<T1, String> titles) {
        return addColumn1(new PoiColumn<>(valueFunction, item -> new String[]{titles.apply(item.value())}));
    }

    public <V> PoiColumn<T1> addColumn1(ValueFunction<T1, V> valueFunction, String... titles) {
        return addColumn1(new PoiColumn<>(valueFunction, item -> titles));
    }

    public <V> PoiColumnItems<T1, V> addColumns1(PoiColumnItems<T1, V> poiColumns) {
        poiColumns.parent(this);
        poiColumns.setDataIndex(1);
        columns.add(poiColumns);
        return poiColumns;
    }

    public PoiColumn<T1> addColumn1(PoiColumn<T1> column) {
        column.parent(this);
        column.setDataIndex(1);
        columns.add(column);
        return column;
    }

    @Override
    int setCellValue(int row, int celNum, PoiBuilder.CelValueFunction celFunction) {
        DataItem<T1> item = newItem();
        while (item.loadNext()) {
            int cel = celNum;
            for (Column iColumn : columns) {
                if (iColumn instanceof PoiColumn) {
                    PoiColumn column = (PoiColumn) iColumn;
                    celFunction.setCelValue(row, cel++, getColumnValue(column, item), PoiUtil.getStyle(column));
                } else if (iColumn instanceof PoiColumnItems) {
                    PoiColumnItems poiColumns = (PoiColumnItems) iColumn;
                    ItemFunction<T1, Collection<Object>> valueFunction = poiColumns.getItemFunction();
                    cel = setCellValue(row, cel, item.child(valueFunction), poiColumns.getColumns(), celFunction);
                }
            }
            row++;
        }
        return row;
    }

    @Override
    DataItem<T1> newItem() {
        return new DataItem<>(dataCollection);
    }

    @Override
    DataItem getLeafItem() {
        return newItem();
    }

    @Override
    DataItem getItem(DataItem<Object> leafItem, int index) {
        return leafItem;
    }

}

