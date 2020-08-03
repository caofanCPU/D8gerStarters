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


import com.xyz.caofancpu.excel.core.ItemFunction;
import com.xyz.caofancpu.excel.core.Node;
import com.xyz.caofancpu.excel.core.PoiStyle;
import com.xyz.caofancpu.excel.core.face.Styleable;
import com.xyz.caofancpu.excel.core.face.Titleable;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * poi自定义导出工具类
 */
public class PoiUtil {
    /**
     * 空标题
     */
    private static final String[] EMPTY_TITLE = new String[0];

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
            cell.setCellValue(value != null ? value.toString() : "");
        }
    }

    public static void setCellValue(Row row, int colNum, Number value, CellStyle style) {
        setCellValue(row, colNum, value, "", style);
    }

    public static void setCellValue(Row row, int colNum, Number value, String defaultVaue, CellStyle style) {
        Cell cell = row.createCell(colNum);
        cell.setCellStyle(style);
        if (value != null) {
            cell.setCellValue(value.doubleValue());
        } else {
            cell.setCellValue(defaultVaue);
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
     * 居中并且没有下边框的单元格
     */
    public static PoiStyle getCenterNonBottomPoiStyle() {
        PoiStyle centerStyle = new PoiStyle();
        centerStyle.setAlign(HorizontalAlignment.CENTER); // 居中
        centerStyle.setVAlign(VerticalAlignment.CENTER); //上下居中
        centerStyle.setBorderLeft(BorderStyle.THIN); //左边框
        centerStyle.setBorderTop(BorderStyle.THIN); //上边框
        centerStyle.setBorderRight(BorderStyle.THIN); //右边框
        centerStyle.setWrapText(true);
        return centerStyle;
    }

    /**
     * 居中并且没有上边框的单元格
     */
    public static PoiStyle getCenterNonTopPoiStyle() {
        PoiStyle centerStyle = new PoiStyle();
        centerStyle.setAlign(HorizontalAlignment.CENTER); // 居中
        centerStyle.setVAlign(VerticalAlignment.CENTER); //上下居中
        centerStyle.setBorderBottom(BorderStyle.THIN); //下边框
        centerStyle.setBorderLeft(BorderStyle.THIN); //左边框
        centerStyle.setBorderRight(BorderStyle.THIN); //右边框
        centerStyle.setWrapText(true);
        return centerStyle;
    }

    /**
     * 居中并且有边框的单元格
     */
    public static PoiStyle getCenterPoiStyle() {
        PoiStyle centerStyle = new PoiStyle();
        centerStyle.setAlign(HorizontalAlignment.CENTER); // 居中
        centerStyle.setVAlign(VerticalAlignment.CENTER); //上下居中
        centerStyle.setBorderBottom(BorderStyle.THIN); //下边框
        centerStyle.setBorderLeft(BorderStyle.THIN); //左边框
        centerStyle.setBorderTop(BorderStyle.THIN); //上边框
        centerStyle.setBorderRight(BorderStyle.THIN); //右边框
        centerStyle.setWrapText(true);
        return centerStyle;
    }

    /**
     * 上下居中并且有边框的单元格
     */
    public static PoiStyle getVCenterPoiStyle() {
        PoiStyle centerStyle = new PoiStyle();
        centerStyle.setVAlign(VerticalAlignment.CENTER); //上下居中
        centerStyle.setBorderBottom(BorderStyle.THIN); //下边框
        centerStyle.setBorderLeft(BorderStyle.THIN); //左边框
        centerStyle.setBorderTop(BorderStyle.THIN); //上边框
        centerStyle.setBorderRight(BorderStyle.THIN); //右边框
        centerStyle.setWrapText(true);
        return centerStyle;
    }

    /**
     * 居中并且有边框的单元格
     *
     * @return
     */
    public static PoiStyle getCenterCellPoiStyle() {
        PoiStyle centerStyle = new PoiStyle();
        centerStyle.setAlign(HorizontalAlignment.CENTER); // 左右居中
        centerStyle.setVAlign(VerticalAlignment.CENTER); //上下居中
        centerStyle.setBorderBottom(BorderStyle.THIN); //下边框
        centerStyle.setBorderLeft(BorderStyle.THIN); //左边框
        centerStyle.setBorderTop(BorderStyle.THIN); //上边框
        centerStyle.setBorderRight(BorderStyle.THIN); //右边框
        centerStyle.setWrapText(true);//自动换行
        return centerStyle;
    }

    /**
     * 箭头(绿涨红跌) + 百分号 + 居中特殊样式
     *
     * @return
     */
    public static PoiStyle getArrowPercentCenterPoiStyle() {
        PoiStyle centerStyle = new PoiStyle();
        centerStyle.setStyleFormat("[Green]↑ 0.0%;[Red]↓ 0.0%;0.0%");
        centerStyle.setBorderBottom(BorderStyle.THIN); //下边框
        centerStyle.setBorderLeft(BorderStyle.THIN); //左边框
        centerStyle.setBorderTop(BorderStyle.THIN); //上边框
        centerStyle.setBorderRight(BorderStyle.THIN); //右边框
        centerStyle.setWrapText(true);
        return centerStyle;
    }

    /**
     * 箭头(绿涨红跌) + 居中特殊样式
     *
     * @return
     */
    public static PoiStyle getArrowCenterPoiStyle() {
        PoiStyle centerStyle = new PoiStyle();
        centerStyle.setStyleFormat("[Green]↑ 0.0;[Red]↓ 0.0;0.0");
        centerStyle.setBorderBottom(BorderStyle.THIN); //下边框
        centerStyle.setBorderLeft(BorderStyle.THIN); //左边框
        centerStyle.setBorderTop(BorderStyle.THIN); //上边框
        centerStyle.setBorderRight(BorderStyle.THIN); //右边框
        centerStyle.setWrapText(true);
        return centerStyle;
    }

    /**
     * 红字
     *
     * @return
     */
    public static PoiStyle getRedCenterPoiStyle() {
        PoiStyle centerStyle = new PoiStyle();
        centerStyle.setFontColor(IndexedColors.RED);
        centerStyle.setAlign(HorizontalAlignment.CENTER); // 居中
        centerStyle.setVAlign(VerticalAlignment.CENTER); //上下居中
        centerStyle.setBorderBottom(BorderStyle.THIN); //下边框
        centerStyle.setBorderLeft(BorderStyle.THIN); //左边框
        centerStyle.setBorderTop(BorderStyle.THIN); //上边框
        centerStyle.setBorderRight(BorderStyle.THIN); //右边框
        centerStyle.setWrapText(true);
        return centerStyle;
    }

    /**
     * 绿字
     *
     * @return
     */
    public static PoiStyle getGreenCenterPoiStyle() {
        PoiStyle centerStyle = new PoiStyle();
        centerStyle.setFontColor(IndexedColors.GREEN);
        centerStyle.setAlign(HorizontalAlignment.CENTER); // 居中
        centerStyle.setVAlign(VerticalAlignment.CENTER); //上下居中
        centerStyle.setBorderBottom(BorderStyle.THIN); //下边框
        centerStyle.setBorderLeft(BorderStyle.THIN); //左边框
        centerStyle.setBorderTop(BorderStyle.THIN); //上边框
        centerStyle.setBorderRight(BorderStyle.THIN); //右边框
        centerStyle.setWrapText(true);
        return centerStyle;
    }

    /**
     * 填充黄底红字
     *
     * @return
     */
    public static PoiStyle getRedFrontAndYellowBgCenterPoiStyle() {
        PoiStyle style = new PoiStyle();
        style.setFontColor(IndexedColors.GREEN);
        style.setBgColor(IndexedColors.YELLOW);
        style.setAlign(HorizontalAlignment.CENTER); // 居中
        style.setVAlign(VerticalAlignment.CENTER); //上下居中
        style.setBorderBottom(BorderStyle.THIN); //下边框
        style.setBorderLeft(BorderStyle.THIN); //左边框
        style.setBorderTop(BorderStyle.THIN); //上边框
        style.setBorderRight(BorderStyle.THIN); //右边框
        style.setWrapText(true);
        return style;
    }

    /**
     * 居右并且有边框的单元格
     *
     * @return
     */
    public static PoiStyle getRightCellPoiStyle() {
        PoiStyle rightStyle = new PoiStyle();
        rightStyle.setAlign(HorizontalAlignment.RIGHT); // 居右
        rightStyle.setVAlign(VerticalAlignment.CENTER); //上下居中
        rightStyle.setBorderBottom(BorderStyle.THIN); //下边框
        rightStyle.setBorderLeft(BorderStyle.THIN); //左边框
        rightStyle.setBorderTop(BorderStyle.THIN); //上边框
        rightStyle.setBorderRight(BorderStyle.THIN); //右边框
        rightStyle.setWrapText(true);//自动换行
        return rightStyle;
    }

    /**
     * 居左并且有边框的单元格
     *
     * @return
     */
    public static PoiStyle getLeftCellPoiStyle() {
        PoiStyle leftStyle = new PoiStyle();
        leftStyle.setAlign(HorizontalAlignment.LEFT); // 居右
        leftStyle.setVAlign(VerticalAlignment.CENTER); //上下居中
        leftStyle.setBorderBottom(BorderStyle.THIN); //下边框
        leftStyle.setBorderLeft(BorderStyle.THIN); //左边框
        leftStyle.setBorderTop(BorderStyle.THIN); //上边框
        leftStyle.setBorderRight(BorderStyle.THIN); //右边框
        leftStyle.setWrapText(true);//自动换行
        return leftStyle;
    }

    /**
     * 填充蓝底
     *
     * @return
     */
    public static PoiStyle getBlueBgPoiStyle() {
        PoiStyle style = new PoiStyle();
        style.setBgColor(IndexedColors.PALE_BLUE);
        style.setAlign(HorizontalAlignment.CENTER); // 居中
        style.setVAlign(VerticalAlignment.CENTER); //上下居中
        style.setBorderBottom(BorderStyle.THIN); //下边框
        style.setBorderLeft(BorderStyle.THIN); //左边框
        style.setBorderTop(BorderStyle.THIN); //上边框
        style.setBorderRight(BorderStyle.THIN); //右边框
        style.setWrapText(true);
        return style;
    }

    /**
     * 填充蓝底 字体靠左
     *
     * @return
     */
    public static PoiStyle getBlueBgLeftCellPoiStyle() {
        PoiStyle style = new PoiStyle();
        style.setBgColor(IndexedColors.PALE_BLUE);
        style.setAlign(HorizontalAlignment.LEFT); // 居中
        style.setVAlign(VerticalAlignment.CENTER); //上下居中
        style.setBorderBottom(BorderStyle.THIN); //下边框
        style.setBorderLeft(BorderStyle.THIN); //左边框
        style.setBorderTop(BorderStyle.THIN); //上边框
        style.setBorderRight(BorderStyle.THIN); //右边框
        style.setWrapText(true);
        return style;
    }

    /**
     * 填充黄底
     *
     * @return
     */
    public static PoiStyle getYellowBgPoiStyle() {
        PoiStyle style = new PoiStyle();
        style.setBgColor(IndexedColors.YELLOW);
        style.setAlign(HorizontalAlignment.CENTER); // 居中
        style.setVAlign(VerticalAlignment.CENTER); //上下居中
        style.setBorderBottom(BorderStyle.THIN); //下边框
        style.setBorderLeft(BorderStyle.THIN); //左边框
        style.setBorderTop(BorderStyle.THIN); //上边框
        style.setBorderRight(BorderStyle.THIN); //右边框
        style.setWrapText(true);
        return style;
    }

    /**
     * 红跌绿涨箭头：小数处理
     *
     * @param decimals 小数位
     * @return
     */
    public static PoiStyle getUpDownDisplayStyle(Integer decimals) {
        PoiStyle centerStyle = new PoiStyle();
        String digital = "0";
        if (decimals == 0) {
            digital = "0";
        } else if (decimals == 1) {
            digital = "0.0";
        } else if (decimals == 2) {
            digital = "0.00";
        }
        centerStyle.setStyleFormat("[Green]↑ " + digital + ";[Red]↓ " + digital + ";" + digital);
        centerStyle.setAlign(HorizontalAlignment.CENTER); // 水平居中
        centerStyle.setVAlign(VerticalAlignment.CENTER); //垂直居中
        centerStyle.setBorderBottom(BorderStyle.THIN); //下边框
        centerStyle.setBorderLeft(BorderStyle.THIN); //左边框
        centerStyle.setBorderTop(BorderStyle.THIN); //上边框
        centerStyle.setBorderRight(BorderStyle.THIN); //右边框
        centerStyle.setWrapText(true);
        return centerStyle;
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
}
