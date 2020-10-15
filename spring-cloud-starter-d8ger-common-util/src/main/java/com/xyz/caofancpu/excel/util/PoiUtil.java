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

package com.xyz.caofancpu.excel.util;


import com.xyz.caofancpu.constant.SymbolConstantUtil;
import com.xyz.caofancpu.core.CollectionUtil;
import com.xyz.caofancpu.excel.core.ItemFunction;
import com.xyz.caofancpu.excel.core.Node;
import com.xyz.caofancpu.excel.core.PoiStyle;
import com.xyz.caofancpu.excel.core.face.Styleable;
import com.xyz.caofancpu.excel.core.face.Titleable;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * poi自定义导出工具类
 */
public class PoiUtil {
    /**
     * 空标题
     */
    private static final String[] EMPTY_TITLE = new String[0];

    /**
     * 绿色字体关键字
     */
    public static final String GREEN_STYLE_KEY = "[Green]";
    /**
     * 红色字体样式关键字
     */
    public static final String RED_STYLE_KEY = "[Red]";
    /**
     * 上涨
     */
    public static final String UP_ARROW = "↑";
    /**
     * 下跌
     */
    public static final String DOWN_ARROW = "↓";

    public static Row getRow(Sheet sheet, int rowIndex) {
        Row wrongRow = sheet.getRow(rowIndex);
        if (wrongRow == null) {
            wrongRow = sheet.createRow(rowIndex);
        }
        return wrongRow;
    }

    public static Integer getColumnWidth(Float columnWidth) {
        return columnWidth > 256 ? columnWidth.intValue() : (int) (columnWidth * 256 + 182);
    }

    public static Integer getColumnWidth(Integer columnWidth) {
        return columnWidth > 256 ? columnWidth : (int) (columnWidth * 256F + 182);
    }

    /**
     * 设置带样式的单元格值
     *
     * @param row    行
     * @param colNum 列号
     * @param value  内容
     * @param style  样式
     */
    public static void setCellValue(Row row, int colNum, Object value, CellStyle style) {
        if (value instanceof Number) {
            setCellValue(row, colNum, (Number) value, style);
        } else {
            Cell cell = row.createCell(colNum);
            cell.setCellStyle(style);
            cell.setCellValue(Objects.nonNull(value) ? value.toString() : SymbolConstantUtil.EMPTY);
        }
    }

    public static void setCellValue(Row row, int colNum, Number value, CellStyle style) {
        setCellValue(row, colNum, value, 1, SymbolConstantUtil.EMPTY, style);
    }

    public static void setCellValue(Row row, int colNum, Number value, Integer decimalScale, String defaultValue, CellStyle style) {
        Cell cell = row.createCell(colNum);
        cell.setCellStyle(style);
        if (Objects.isNull(decimalScale) || decimalScale < 0) {
            decimalScale = 0;
        }
        if (Objects.nonNull(value)) {
            cell.setCellValue(BigDecimal.valueOf(value.doubleValue()).setScale(decimalScale, BigDecimal.ROUND_HALF_UP).doubleValue());
        } else {
            cell.setCellValue(defaultValue);
        }
    }

    public static void mergedRegion(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol, Object value, CellStyle centerStyle) {
        if (lastRow > firstRow || lastCol > firstCol) {
            sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
            PoiUtil.setCellValue(getRow(sheet, firstRow), firstCol, value, centerStyle);
            if (centerStyle != null) {
                for (int i = firstRow; i <= lastRow; i++) {
                    Row row = getRow(sheet, i);
                    for (int j = firstCol; j <= lastCol; j++) {
                        Cell cell = row.getCell(j);
                        if (cell == null) {
                            cell = row.createCell(j);
                        }
                        cell.setCellStyle(centerStyle);
                    }
                }
            }
        } else {
            PoiUtil.setCellValue(getRow(sheet, firstRow), firstCol, value, centerStyle);
        }
    }

    /**
     * 水平居中+垂直居中+内外所有边框的单元格
     * 如果不需要某个属性, 置为null即可
     */
    public static PoiStyle getCenterPoiStyle() {
        return new PoiStyle()
                // 水平居中
                .setAlign(HorizontalAlignment.CENTER)
                // 垂直居中
                .setVAlign(VerticalAlignment.CENTER)
                // 上边框
                .setBorderTop(BorderStyle.THIN)
                // 下边框
                .setBorderBottom(BorderStyle.THIN)
                // 左边框
                .setBorderLeft(BorderStyle.THIN)
                // 右边框
                .setBorderRight(BorderStyle.THIN)
                // 文本自动换行
                .setWrapText(true);
    }

    /**
     * 箭头(绿涨红跌) + 居中特殊样式
     * [Green]↑ 0.0;[Red]↓ 0.0;0.0
     * [Red]↑ 0.0;[Green]↓ 0.0;0.0
     *
     * @param opposite 可选参数, 传true则为红涨绿跌
     * @return
     */
    public static PoiStyle getDefaultNumberArrowCenterPoiStyle(boolean... opposite) {
        boolean reverse = CollectionUtil.isNotEmpty(opposite) && BooleanUtils.isTrue(opposite[0]);
        return getNumberDynamicArrowCenterPoiStyle(1, null, reverse);
    }

    /**
     * 箭头(绿涨红跌) + 百分号 + 居中特殊样式
     * [Green]↑ 0.0%;[Red]↓ 0.0%;0.0%
     * [Red]↑ 0.0%;[Green]↓ 0.0%;0.0%
     *
     * @param referValue 涨跌参考值
     * @param opposite 可选参数, 传true则为红涨绿跌
     *
     * @return
     */
    public static PoiStyle getDefaultNumberPercentArrowCenterPoiStyle(Number referValue, boolean... opposite) {
        boolean reverse = CollectionUtil.isNotEmpty(opposite) && BooleanUtils.isTrue(opposite[0]);
        return getNumberPercentDynamicArrowCenterPoiStyle(1, reverse);
    }

    public static PoiStyle getNumberPercentDynamicArrowCenterPoiStyle(Integer decimalScale, boolean... opposite) {
        boolean reverse = CollectionUtil.isNotEmpty(opposite) && BooleanUtils.isTrue(opposite[0]);
        return getNumberDynamicArrowCenterPoiStyle(decimalScale, SymbolConstantUtil.PERCENT, reverse);
    }

    /**
     * @param decimalScale
     * @param excelFlagSuffix
     * @param reverse
     * @return
     */
    public static PoiStyle getNumberDynamicArrowCenterPoiStyle(Integer decimalScale, String excelFlagSuffix, boolean reverse) {
        if (Objects.isNull(decimalScale)) {
            decimalScale = 0;
        }
        String excelReferFlag = BigDecimal.valueOf(0).setScale(decimalScale, BigDecimal.ROUND_HALF_UP).toString();
        if (StringUtils.isNotBlank(excelFlagSuffix)) {
            excelReferFlag += excelFlagSuffix;
        }
        String format = GREEN_STYLE_KEY + (reverse ? DOWN_ARROW : UP_ARROW)
                + SymbolConstantUtil.SPACE + excelReferFlag + SymbolConstantUtil.ENGLISH_SEMICOLON
                + RED_STYLE_KEY + (reverse ? UP_ARROW : DOWN_ARROW)
                + SymbolConstantUtil.SPACE + excelReferFlag + SymbolConstantUtil.ENGLISH_SEMICOLON + excelReferFlag;
        return getCenterPoiStyle().setStyleFormat(format);
    }

    /**
     * 红字
     *
     * @return
     */
    public static PoiStyle getRedCenterPoiStyle() {
        return getCenterPoiStyle().setFontColor(IndexedColors.RED);
    }

    /**
     * 绿字
     *
     * @return
     */
    public static PoiStyle getGreenCenterPoiStyle() {
        return getCenterPoiStyle().setFontColor(IndexedColors.GREEN);
    }

    /**
     * 填充黄底红字
     *
     * @return
     */
    public static PoiStyle getRedFrontAndYellowBgCenterPoiStyle() {
        return getCenterPoiStyle().setFontColor(IndexedColors.RED).setBgColor(IndexedColors.YELLOW);
    }

    public static PoiStyle getStyle(Node node) {
        if (node == null) {
            return null;
        }
        if (node instanceof Styleable) {
            Styleable styleable = (Styleable) node;
            PoiStyle poiStyle = styleable.getStyle();
            if (poiStyle != null) {
                return poiStyle;
            }
        }
        Node parent = node.parent();
        if (parent != null) {
            return getStyle(parent);
        }
        return null;
    }

    public static PoiStyle getTitleStyle(Node node) {
        if (node == null) {
            return null;
        }
        if (node instanceof Titleable) {
            Titleable titleable = (Titleable) node;
            PoiStyle poiStyle = titleable.getTitleStyle();
            if (poiStyle != null) {
                return poiStyle;
            }
        }
        Node parent = node.parent();
        if (parent != null) {
            return getTitleStyle(parent);
        }
        return null;
    }

    public static <T> ItemFunction<T, String[]> emptyTitle() {
        return item -> EMPTY_TITLE;
    }

    public static void main(String[] args) {
        Number referValue = 20;
        String excelFlagSuffix = SymbolConstantUtil.PERCENT;
        boolean reverse = true;
        if (Objects.isNull(referValue)) {
            referValue = 0D;
        }
        for (int i = 0; i < 5; i++) {
            Integer decimalScale = i;
            String excelReferFlag = BigDecimal.valueOf(referValue.doubleValue()).setScale(decimalScale, BigDecimal.ROUND_HALF_UP).toString();
            if (StringUtils.isNotBlank(excelFlagSuffix)) {
                excelReferFlag += excelFlagSuffix;
            }
            String format = GREEN_STYLE_KEY + (reverse ? DOWN_ARROW : UP_ARROW)
                    + SymbolConstantUtil.SPACE + excelReferFlag + SymbolConstantUtil.ENGLISH_SEMICOLON
                    + RED_STYLE_KEY + (reverse ? UP_ARROW : DOWN_ARROW)
                    + SymbolConstantUtil.SPACE + excelReferFlag + SymbolConstantUtil.ENGLISH_SEMICOLON + excelReferFlag;
            System.out.println(format);
        }
    }
}
