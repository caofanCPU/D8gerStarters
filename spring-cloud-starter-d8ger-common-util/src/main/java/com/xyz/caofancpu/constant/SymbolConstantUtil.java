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

package com.xyz.caofancpu.constant;

import com.xyz.caofancpu.core.CollectionFunUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;

/**
 * 常用符号
 *
 * @author D8GER
 */
public class SymbolConstantUtil {

    /**
     * 中文顿号
     */
    public static final String CHINESE_STOP = "、";

    /**
     * 英文句号
     */
    public static final String ENGLISH_FULL_STOP = ".";

    /**
     * 中文逗号
     */
    public static final String CHINESE_COMMA = "，";

    /**
     * 英文逗号
     */
    public static final String ENGLISH_COMMA = ",";

    /**
     * 空串
     */
    public static final String EMPTY = StringUtils.EMPTY;

    /**
     * 单个空格符
     */
    public static final String SPACE = StringUtils.SPACE;

    /**
     * 标准分隔符 ', '
     */
    public static final String NORMAL_ENGLISH_COMMA_DELIMITER = ENGLISH_COMMA + SPACE;

    /**
     * EXCEL中汉字的缩进间距，设置为4个空格
     */
    public static final String TAB = CollectionFunUtil.join(Collections.nCopies(4, SPACE), EMPTY);

    /**
     * 原始Tab符号
     */
    public static final String ORIGIN_TAB = "\t";

    /**
     * Double Tabs
     */
    public static final String DOUBLE_TAB = TAB + TAB;

    /**
     * Triple Tabs
     */
    public static final String TRIPLE_TAB = DOUBLE_TAB + TAB;

    /**
     * Quaternary Tabs
     */
    public static final String QUATERNARY_TAB = TRIPLE_TAB + TAB;

    /**
     * Penta Tabs
     */
    public static final String PENTA_TAB = QUATERNARY_TAB + TAB;

    /**
     * 填空占位符: 两条短横线
     */
    public static final String FILL_EMPTY_PLACE_HOLDER = "--";

    /**
     * 连接符: 单个短横线
     */
    public static final String JOINER = "-";

    /**
     * 连接符: 单个下划短横线
     */
    public static final String ENGLISH_UNDER_JOINER = "_";

    /**
     * 中文左括号
     */
    public static final String CHINESE_LEFT_BRACKET = "（";

    /**
     * 中文右括号
     */
    public static final String CHINESE_RIGHT_BRACKET = "）";

    /**
     * 英文左括号
     */
    public static final String ENGLISH_LEFT_BRACKET = "(";

    /**
     * 英文右括号
     */
    public static final String ENGLISH_RIGHT_BRACKET = ")";

    /**
     * 英文分号
     */
    public static final String ENGLISH_SEMICOLON = ";";

    /**
     * 英文冒号
     */
    public static final String ENGLISH_COLON = ":";

    /**
     * 英文双引号
     */
    public static final String ENGLISH_DOUBLE_QUOTES = "\"";

    /**
     * 等号
     */
    public static final String EQUAL = "=";

    /**
     * 百分号
     */
    public static final String PERCENT = "%";

    /**
     * 换行
     */
    public static final String NEXT_LINE = "\n";

    /**
     * 与符号
     */
    public static final String AND = "&";

    /**
     * 大于号
     */
    public static final String GREATER = ">";

    /**
     * 小于号
     */
    public static final String LESS_THEN = "<";

    /**
     * 英文左中括号
     */
    public static final String ENGLISH_MID_LEFT_BRACKET = "[";

    /**
     * 英文右中括号
     */
    public static final String ENGLISH_MID_RIGHT_BRACKET = "]";

    /**
     * 展示null
     */
    public static final String NULL_SHOW = "null";

    /**
     * 展示错误✖代替空串
     */
    public static final String ERROR_SHOW = "✖";

    /**
     * 省略号
     */
    public static final String ELLIPSES = "...";

    /**
     * 旧版Excel文件后缀
     */
    public static final String OLD_XLS_SUFFIX = ".xls";

    /**
     * 新版Excel文件后缀
     */
    public static final String NEW_XLSX_SUFFIX = ".xlsx";

    /**
     * 井号
     */
    public static final String HASHTAG = "#";

}
