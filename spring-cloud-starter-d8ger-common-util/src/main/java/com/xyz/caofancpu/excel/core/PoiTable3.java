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
public class PoiTable3<T1, T2, T3> extends PoiTable2<T1, T2> {
    protected final Function<T2, Collection<T3>> groupFunction3;

    public PoiTable3(List<T1> dataList1, Function<T1, Collection<T2>> v2Function, Function<T2, Collection<T3>> v3Function) {
        super(dataList1, v2Function);
        this.groupFunction3 = v3Function;
    }

    public <V> PoiColumnItems<T3, V> addColumnItems3(ItemFunction<T3, Collection<V>> valueFunction, ItemFunction<V, String[]> titles) {
        return addColumns3(new PoiColumnItems<>(valueFunction, titles));
    }

    public <V> PoiColumnItems<T3, V> addColumns3(ValueFunction<T3, Collection<V>> valueFunction, Function<V, String> title) {
        return addColumns3(new PoiColumns<>(valueFunction, title));
    }

    public <V> PoiColumn<T3> addColumnItem3(ItemFunction<T3, V> valueFunction, String... titles) {
        return addColumn3(new PoiColumn<>(valueFunction, item -> titles));
    }

    public <V> PoiColumn<T3> addColumnItem3(ItemFunction<T3, V> valueFunction, ItemFunction<T3, String[]> titles) {
        return addColumn3(new PoiColumn<>(valueFunction, titles));
    }

    public <V> PoiColumn<T3> addColumn3(ValueFunction<T3, V> valueFunction, Function<T3, String> titles) {
        return addColumn3(new PoiColumn<>(valueFunction, item -> new String[]{titles.apply(item.value())}));
    }

    public <V> PoiColumn<T3> addColumn3(ValueFunction<T3, V> valueFunction, String... titles) {
        return addColumn3(new PoiColumn<>(valueFunction, item -> titles));
    }

    public <V> PoiColumnItems<T3, V> addColumns3(PoiColumnItems<T3, V> poiColumns) {
        poiColumns.parent(this);
        poiColumns.setDataIndex(3);
        columns.add(poiColumns);
        return poiColumns;
    }

    public PoiColumn<T3> addColumn3(PoiColumn<T3> column) {
        column.parent(this);
        column.setDataIndex(3);
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
                int num2 = 0;
                int row2 = row;
                boolean merged2 = false;
                Map<Integer, Tmp<Object, PoiStyle>> merge2 = new TreeMap<>();
                DataItem<T3> item3 = item2.child(groupFunction3);
                while (item3.loadNext()) {
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
                            } else if (column.getDataIndex() == 2) {
                                if (!merged2) {
                                    merge2.put(cel++, new Tmp<>(getColumnValue(column, item2), PoiUtil.getStyle(column)));
                                } else {
                                    cel++;
                                }
                            } else {
                                celFunction.setCelValue(row, cel++, getColumnValue(column, item3), PoiUtil.getStyle(column));
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
                            } else if (poiColumns.getDataIndex() == 2) {
                                if (!merged2) {
                                    ItemFunction<T2, Collection<Object>> valueFunction = poiColumns.getItemFunction();
                                    cel = setCellValue(cel, item2.child(valueFunction), poiColumns.getColumns(), merge2);
                                } else {
                                    //noinspection StatementWithEmptyBody
                                    while (merge2.containsKey(++cel)) {
                                    }
                                }
                            } else {
                                ItemFunction<T3, Collection<Object>> valueFunction = poiColumns.getItemFunction();
                                cel = setCellValue(row, cel, item3.child(valueFunction), poiColumns.getColumns(), celFunction);
                            }
                        }
                    }
                    //最后一层
                    row++;
                    merged1 = true;
                    num1++;
                    merged2 = true;
                    num2++;
                }

                for (Map.Entry<Integer, Tmp<Object, PoiStyle>> entry : merge2.entrySet()) {
                    Tmp<Object, PoiStyle> value = entry.getValue();
                    celFunction.setCelValue(row2, row2 + num2 - 1, entry.getKey(), entry.getKey(), value.getKey(), value.getValue());
                }
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
            DataItem<T2> item2 = item1.child(groupFunction2);
            if (item2.loadNext()) {
                return item2.child(groupFunction3);
            }
        }
        return null;
    }

    @Override
    DataItem getItem(DataItem<Object> leafItem, int index) {
        return index == 3 ? leafItem : (index == 2 ? leafItem.parent() : leafItem.parent().parent());
    }
}

