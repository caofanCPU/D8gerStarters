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
import com.xyz.caofancpu.excel.core.face.Styleable;
import com.xyz.caofancpu.excel.core.face.Titleable;
import com.xyz.caofancpu.excel.util.PoiUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * Excel的列映射对象
 *
 * @author D8GER
 * @author guanxiaochen
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class PoiColumn<T> extends Node implements Column<T>, Styleable, Titleable {
    private int dataIndex = 1;
    /**
     * 标题
     */
    private ItemFunction<T, String[]> titles;
    private ItemFunction<T, ?> valueFunction;

    /**
     * String.format
     */
    private String format;
    /**
     * 日期格式,如果设置的类型不是date,注册时,会抛出异常
     */
    private SimpleDateFormat dateFormat;
    /**
     * 枚举值,例如(1:男,2:女)表示,值为1,取 (男)作为value ,2则取 (女)作为value
     */
    private Map<String, String> enumFormat;

    /**
     * cell的宽度
     */
    private Float columnWidth;

    /**
     * 样式
     */
    private PoiStyle style;

    /**
     * 标题样式
     */
    private PoiStyle titleStyle;

    /**
     * 当值为空时,字段的默认值
     */
    private String defaultValue;

    /**
     * 无标题列
     *
     * @param valueFunction 获取值的Function, 空去默认, 无默认值为""
     */
    public <V> PoiColumn(ItemFunction<T, V> valueFunction) {
        this.titles = PoiUtil.emptyTitle();
        this.valueFunction = valueFunction;
    }

    /**
     * @param valueFunction 获取值的Function, 空去默认, 无默认值为""
     * @param titles        标题,可以返回多行,每个是一行
     */
    public <V> PoiColumn(ItemFunction<T, V> valueFunction, String... titles) {
        this.titles = item -> titles;
        this.valueFunction = valueFunction;
    }

    /**
     * @param valueFunction 获取值的Function, 空去默认, 无默认值为""
     * @param titles        标题,可以返回多行,每个是一行
     */
    public <V> PoiColumn(ItemFunction<T, V> valueFunction, ItemFunction<T, String[]> titles) {
        this.titles = titles;
        this.valueFunction = valueFunction;
    }

}
