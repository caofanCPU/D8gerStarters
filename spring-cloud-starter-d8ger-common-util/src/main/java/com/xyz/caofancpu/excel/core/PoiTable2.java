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
import com.xyz.caofancpu.excel.tmp.Tmp;
import com.xyz.caofancpu.excel.util.PoiUtil;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

@SuppressWarnings({"rawtypes", "unchecked"})
@Getter
@Accessors(chain = true)
public class PoiTable2<T1, T2> extends PoiTable1<T1> {
    protected final Function<T1, Collection<T2>> groupFunction2;

    public PoiTable2(List<T1> dataList1, Function<T1, Collection<T2>> vFunction) {
        super(dataList1);
        this.groupFunction2 = vFunction;
    }

    public <V> PoiColumnItems<T2, V> addColumnItems2(ItemFunction<T2, Collection<V>> valueFunction, ItemFunction<V, String[]> titles) {
        return addColumns2(new PoiColumnItems<>(valueFunction, titles));
    }

    public <V> PoiColumnItems<T2, V> addColumns2(ValueFunction<T2, Collection<V>> valueFunction, Function<V, String> title) {
        return addColumns2(new PoiColumns<>(valueFunction, title));
    }

    public <V> PoiColumn<T2> addColumnItem2(ItemFunction<T2, V> valueFunction, ItemFunction<T2, String[]> titles) {
        return addColumn2(new PoiColumn<>(valueFunction, titles));
    }

    public <V> PoiColumn<T2> addColumnItem2(ItemFunction<T2, V> valueFunction, String... titles) {
        return addColumn2(new PoiColumn<>(valueFunction, item -> titles));
    }

    public <V> PoiColumn<T2> addColumn2(ValueFunction<T2, V> valueFunction, Function<T2, String> titles) {
        return addColumn2(new PoiColumn<>(valueFunction, item -> new String[]{titles.apply(item.value())}));
    }

    public <V> PoiColumn<T2> addColumn2(ValueFunction<T2, V> valueFunction, String... titles) {
        return addColumn2(new PoiColumn<>(valueFunction, item -> titles));
    }

    public <V> PoiColumnItems<T2, V> addColumns2(PoiColumnItems<T2, V> poiColumns) {
        poiColumns.parent(this);
        poiColumns.setDataIndex(2);
        columns.add(poiColumns);
        return poiColumns;
    }

    public PoiColumn<T2> addColumn2(PoiColumn<T2> column) {
        column.parent(this);
        column.setDataIndex(2);
        columns.add(column);
        return column;
    }

    @Override
    int setCellValue(int row, int celNum, PoiBuilder.CelValueFunction celFunction) {
        DataItem<T1> item1 = newItem();
        while (item1.loadNext()) {
            int num1 = 0;
            int row1 = row;
            boolean merged1 = false;
            Map<Integer, Tmp<Object, PoiStyle>> merge1 = new TreeMap<>();
            DataItem<T2> item2 = item1.child(groupFunction2);
            while (item2.loadNext()) {
                int cel = celNum;
                for (Column iColumn : columns) {
                    if (iColumn instanceof PoiColumn) {
                        PoiColumn column = (PoiColumn) iColumn;
                        if (column.getDataIndex() == 1) {
                            if (!merged1) {
                                merge1.put(cel++, new Tmp<>(getColumnValue(column, item1), PoiUtil.getStyle(column)));
                            } else {
                                cel++;
                            }
                        } else {
                            celFunction.setCelValue(row, cel++, getColumnValue(column, item2), PoiUtil.getStyle(column));
                        }
                    } else if (iColumn instanceof PoiColumnItems) {
                        PoiColumnItems poiColumns = (PoiColumnItems) iColumn;
                        if (poiColumns.getDataIndex() == 1) {
                            if (!merged1) {
                                ItemFunction<T1, Collection<Object>> valueFunction = poiColumns.getItemFunction();
                                cel = setCellValue(cel, item1.child(valueFunction), poiColumns.getColumns(), merge1);
                            } else {
                                //noinspection StatementWithEmptyBody
                                while (merge1.containsKey(++cel)) {
                                }
                            }
                        } else {
                            ItemFunction<T2, Collection<Object>> valueFunction = poiColumns.getItemFunction();
                            cel = setCellValue(row, cel, item2.child(valueFunction), poiColumns.getColumns(), celFunction);
                        }
                    }
                }
                //最后一层
                merged1 = true;
                row++;
                num1++;
            }

            for (Map.Entry<Integer, Tmp<Object, PoiStyle>> entry : merge1.entrySet()) {
                Tmp<Object, PoiStyle> value = entry.getValue();
                celFunction.setCelValue(row1, row1 + num1 - 1, entry.getKey(), entry.getKey(), value.getKey(), value.getValue());
            }
        }
        return row;
    }

    @Override
    DataItem<T1> newItem() {
        return new DataItem<>(dataCollection);
    }

    @Override
    DataItem getLeafItem() {
        DataItem<T1> item1 = newItem();
        if (item1.loadNext()) {
            return item1.child(groupFunction2);
        }
        return null;
    }

    @Override
    DataItem getItem(DataItem<Object> leafItem, int index) {
        return index == 2 ? leafItem : leafItem.parent();
    }
}

