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

package com.xyz.caofancpu.excel.xml.config.field;

import com.xyz.caofancpu.excel.xml.config.PoiStyleConfig;
import lombok.Data;
import lombok.experimental.Accessors;

import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * Excel字段定义
 *
 * @author D8GER
 * @author guanxiaochen
 */
@Data
@Accessors(chain = true)
public class FieldConfig implements IFieldConfig {

    /**
     * 属性名称,必须
     */
    private String name;
    /**
     * 标题
     */
    private String[] titles;
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
     * 表达式
     */
    private String eval;
    /**
     * 校验本列是否展示
     **/
    private String filter;

    /**
     * cell的宽度
     */
    private Integer columnWidth;

    /**
     * 样式
     */
    private PoiStyleConfig style;

    /**
     * 标题样式
     */
    private PoiStyleConfig titleStyle;

    /**
     * 当值为空时,字段的默认值
     */
    private String defaultValue;

    /**
     * 校验本列是否展示
     **/
    private boolean show = true;

    private Integer mergeRow;

}
