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
import com.xyz.caofancpu.excel.core.face.Column;
import com.xyz.caofancpu.excel.core.face.Styleable;
import com.xyz.caofancpu.excel.core.face.Titleable;
import com.xyz.caofancpu.excel.tmp.Tmp;
import com.xyz.caofancpu.excel.util.PoiUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 表格列
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Getter()
@Accessors(chain = true)
public abstract class PoiTable<T> extends Node implements Area, Styleable, Titleable {

    /**
     * Column属性的全部定义
     */
    protected List<Column> columns = new ArrayList<>();
    @Setter
    private Boolean showTitle = true;
    /**
     * 样式
     */
    @Setter
    private PoiStyle style;
    /**
     * 标题样式
     */
    @Setter
    private PoiStyle titleStyle;
    /**
     * 数据类型小数精度, 默认1位
     */
    @Setter
    private Integer scale = 1;

    /**
     * 设置值
     *
     * @param row         起始行号
     * @param cel         起始列
     * @param celFunction 设置值
     * @return 返回行号
     */
    abstract int setCellValue(int row, int cel, PoiBuilder.CelValueFunction celFunction);

    /**
     * 获取Item
     */
    abstract DataItem<T> newItem();

    /**
     * 获取标题Item
     */
    abstract DataItem getLeafItem();


    abstract DataItem getItem(DataItem<Object> leafItem, int index);


    /**
     * 设值一行
     *
     * @param cel     起始列
     * @param data    数据
     * @param columns 列
     * @param merge1  设值
     * @return 结束列
     */
    int setCellValue(int cel, DataItem<Object> data, List<Column> columns, Map<Integer, Tmp<Object, PoiStyle>> merge1) {
        while (data.loadNext()) {
            for (Column iColumn : columns) {
                if (iColumn instanceof PoiColumn) {
                    PoiColumn column = (PoiColumn) iColumn;
                    merge1.put(cel++, new Tmp<>(getColumnValue(column, data), PoiUtil.getStyle(column)));
                } else if (iColumn instanceof PoiColumnItems) {
                    PoiColumnItems poiColumns = (PoiColumnItems) iColumn;
                    ItemFunction<Object, Collection<Object>> valueFunction = poiColumns.getItemFunction();
                    cel = setCellValue(cel, data.child(valueFunction), poiColumns.getColumns(), merge1);
                }
            }
        }
        return cel;
    }

    /**
     * 设值一行
     *
     * @param row         行号
     * @param cel         起始列
     * @param data        数据
     * @param columns     列
     * @param celFunction 设值的方法
     * @return 结束列
     */
    int setCellValue(int row, int cel, DataItem<Object> data, List<Column> columns, PoiBuilder.CelValueFunction celFunction) {
        while (data.loadNext()) {
            for (Column iColumn : columns) {
                if (iColumn instanceof PoiColumn) {
                    PoiColumn column = (PoiColumn) iColumn;
                    celFunction.setCelValue(row, cel++, getColumnValue(column, data), PoiUtil.getStyle(column));
                } else if (iColumn instanceof PoiColumnItems) {
                    PoiColumnItems poiColumns = (PoiColumnItems) iColumn;
                    ItemFunction<Object, Collection<Object>> valueFunction = poiColumns.getItemFunction();
                    cel = setCellValue(row, cel, data.child(valueFunction), poiColumns.getColumns(), celFunction);
                }
            }
        }
        return cel;
    }

    Object getColumnValue(PoiColumn column, DataItem<?> data) {
        Object object = column.getValueFunction().apply(data);
        if (object == null) {
            return column.getDefaultValue();
        }
        // 特殊数据类型处理
        if (object instanceof Float) {
            double excelNumber = ((Float) object).doubleValue();
            return BigDecimal.valueOf(excelNumber).setScale(this.scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        if (column.getFormat() != null) {
            return String.format(column.getFormat(), object);
        }

        if (column.getEnumFormat() != null) {
            return column.getEnumFormat().get(object.toString());
        }

        if (column.getDateFormat() != null) {
            return column.getDateFormat().format(object);
        }

        PoiStyle style = PoiUtil.getStyle(column);
        if (style != null && style.getStyleFormat() == null) {
            return object.toString();
        }
        return object;
    }
}
