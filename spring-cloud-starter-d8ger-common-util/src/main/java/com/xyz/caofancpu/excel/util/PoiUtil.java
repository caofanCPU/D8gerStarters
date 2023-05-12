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


import com.xyz.caofancpu.annotation.AttentionDoc;
import com.xyz.caofancpu.annotation.ImportDoc;
import com.xyz.caofancpu.constant.SymbolConstantUtil;
import com.xyz.caofancpu.core.CollectionFunUtil;
import com.xyz.caofancpu.excel.core.ItemFunction;
import com.xyz.caofancpu.excel.core.Node;
import com.xyz.caofancpu.excel.core.PoiBook;
import com.xyz.caofancpu.excel.core.PoiSheet;
import com.xyz.caofancpu.excel.core.PoiStyle;
import com.xyz.caofancpu.excel.core.face.Styleable;
import com.xyz.caofancpu.excel.core.face.Titleable;
import lombok.NonNull;
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
 *
 * @author D8GER
 * @author guanxiaochen
 */
public class PoiUtil {
    /**
     * 空标题
     */
    private static final String[] EMPTY_TITLE = new String[0];

    /**
     * 基础列宽阈值
     */
    private static final Integer BASE_COLUMN_WIDTH_THRESHOLD = 256;

    /**
     * 通过一些测试设置的列宽补偿值
     */
    private static final Integer COMPENSATORY_COLUMN_WIDTH = 182;

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

    /**
     * 获取某行
     *
     * @param sheet
     * @param rowIndex
     * @return
     */
    public static Row getRow(Sheet sheet, int rowIndex) {
        Row targetRow = sheet.getRow(rowIndex);
        return Objects.nonNull(targetRow) ? targetRow : sheet.createRow(rowIndex);
    }

    /**
     * 获取列宽
     *
     * @param columnWidth
     * @return
     */
    public static Integer getColumnWidth(Number columnWidth) {
        int intValue = columnWidth.intValue();
        return intValue > BASE_COLUMN_WIDTH_THRESHOLD ? intValue : intValue * BASE_COLUMN_WIDTH_THRESHOLD + COMPENSATORY_COLUMN_WIDTH;
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

    /**
     * 默认数字格式的单元格, 数值精度为1位小数
     *
     * @param row
     * @param colNum
     * @param value
     * @param style
     */
    public static void setCellValue(Row row, int colNum, Number value, CellStyle style) {
        setCellValue(row, colNum, value, 1, SymbolConstantUtil.EMPTY, style);
    }

    /**
     * 设置数字格式的单元格, 支持数值精度, 默认值
     *
     * @param row
     * @param colNum
     * @param value
     * @param decimalScale
     * @param defaultValue
     * @param style
     */
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

    /**
     * Sheet里指定区域合并单元格, 注意区域数值是从0开始的
     * 举例: sheet左上角单元格, 第1行第1列: 数值坐标为(0, 0)
     *
     * @param sheet
     * @param firstRow
     * @param lastRow
     * @param firstCol
     * @param lastCol
     * @param value
     * @param mergedStyle
     */
    @AttentionDoc("慎用该方法, 因为区域数值容易弄混, 推荐另外一个方法mergedRegion")
    public static void mergedRegion(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol, Object value, CellStyle mergedStyle) {
        if (lastRow > firstRow || lastCol > firstCol) {
            sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
            PoiUtil.setCellValue(getRow(sheet, firstRow), firstCol, value, mergedStyle);
            if (mergedStyle != null) {
                for (int i = firstRow; i <= lastRow; i++) {
                    Row row = getRow(sheet, i);
                    for (int j = firstCol; j <= lastCol; j++) {
                        Cell cell = row.getCell(j);
                        if (cell == null) {
                            cell = row.createCell(j);
                        }
                        cell.setCellStyle(mergedStyle);
                    }
                }
            }
        } else {
            PoiUtil.setCellValue(getRow(sheet, firstRow), firstCol, value, mergedStyle);
        }
    }

    /**
     * 按照指定区域合并单元格
     *
     * @param poiBook
     * @param poiSheet
     * @param firstRow    从excel的第几行开始, 最小值为1
     * @param firstCol    从excel的第几列开始, 最小值为1
     * @param lastRow     到excel的第几行结束, 最小值为1
     * @param lastCol     到excel的第几列结束, 最小值为1
     * @param value
     * @param mergedStyle
     */
    @ImportDoc("推荐使用该方法")
    @AttentionDoc("使用该方法时, 请确保指定区域没有已存在的单元格, 否则会报错")
    public static void mergedRegion(@NonNull PoiBook poiBook, @NonNull PoiSheet poiSheet, int firstRow, int firstCol, int lastRow, int lastCol, Object value, PoiStyle mergedStyle) {
        firstRow = handleExcelCoordinateValue(firstRow);
        firstCol = handleExcelCoordinateValue(firstCol);
        lastRow = handleExcelCoordinateValue(lastRow);
        lastCol = handleExcelCoordinateValue(lastCol);
        Sheet sheet = poiBook.getSheet(poiSheet);
        CellStyle cellStyle = Objects.isNull(mergedStyle) ? null : mergedStyle.createCellStyle(poiBook.getWorkbook());
        // 注意, 下面方法区域参数的位置与本方法入参是不一样的
        mergedRegion(sheet, firstRow, lastRow, firstCol, lastCol, value, cellStyle);
    }

    private static int handleExcelCoordinateValue(int value) {
        return value < 1 ? 0 : value - 1;
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
     * 列数据以0为分界线, 在excel中显示时会带箭头, 但其代表的数值仍然是本身
     * 箭头(绿涨红跌) + 居中特殊样式
     * [Green]↑ 0.0;[Red]↓ 0.0;0.0
     * [Red]↑ 0.0;[Green]↓ 0.0;0.0
     *
     * @param opposite 可选参数, 传true则为红涨绿跌
     */
    public static PoiStyle getNumberArrowCenterPoiStyle(boolean... opposite) {
        boolean reverse = CollectionFunUtil.isNotEmpty(opposite) && BooleanUtils.isTrue(opposite[0]);
        return getNumberDynamicArrowCenterPoiStyle(1, null, reverse);
    }

    /**
     * 列数据以0为分界线, 在excel中显示时会带箭头, 但其代表的数值仍然是本身
     * 箭头(绿涨红跌) + 百分号 + 居中特殊样式
     * [Green]↑ 0.0%;[Red]↓ 0.0%;0.0%
     * [Red]↑ 0.0%;[Green]↓ 0.0%;0.0%
     *
     * @param decimalScale 精度, 小数位数
     * @param opposite     可选参数, 传true则为红涨绿跌
     */
    public static PoiStyle getNumberPercentArrowCenterPoiStyle(Integer decimalScale, boolean... opposite) {
        boolean reverse = CollectionFunUtil.isNotEmpty(opposite) && BooleanUtils.isTrue(opposite[0]);
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

    /**
     * 获取数据样式
     *
     * @param node
     * @return
     */
    public static PoiStyle getStyle(Node node) {
        if (Objects.isNull(node)) {
            return null;
        }
        if (node instanceof Styleable) {
            Styleable styleable = (Styleable) node;
            PoiStyle poiStyle = styleable.getStyle();
            if (Objects.nonNull(poiStyle)) {
                return poiStyle;
            }
        }
        Node parent = node.parent();
        if (Objects.nonNull(parent)) {
            return getStyle(parent);
        }
        return null;
    }

    /**
     * 获取标题样式
     *
     * @param node
     * @return
     */
    public static PoiStyle getTitleStyle(Node node) {
        if (Objects.isNull(node)) {
            return null;
        }
        if (node instanceof Titleable) {
            Titleable titleable = (Titleable) node;
            PoiStyle poiStyle = titleable.getTitleStyle();
            if (Objects.nonNull(poiStyle)) {
                return poiStyle;
            }
        }
        Node parent = node.parent();
        if (Objects.nonNull(parent)) {
            return getTitleStyle(parent);
        }
        return null;
    }

    public static <T> ItemFunction<T, String[]> emptyTitle() {
        return item -> EMPTY_TITLE;
    }
}
