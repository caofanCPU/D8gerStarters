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


import com.xyz.caofancpu.constant.SymbolConstantUtil;
import com.xyz.caofancpu.core.CollectionUtil;
import com.xyz.caofancpu.excel.core.face.Area;
import com.xyz.caofancpu.excel.core.face.Column;
import com.xyz.caofancpu.excel.enums.ListAlign;
import com.xyz.caofancpu.excel.tmp.AreaIndex;
import com.xyz.caofancpu.excel.tmp.CellRegion;
import com.xyz.caofancpu.excel.tmp.Tmp;
import com.xyz.caofancpu.excel.util.BitmapUtil;
import com.xyz.caofancpu.excel.util.PoiAssert;
import com.xyz.caofancpu.excel.util.PoiUtil;
import lombok.Getter;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Excel 生成器
 *
 * @author D8GER
 * @author guanxiaochen
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class PoiBuilder {
    private final PoiBook poiBook;
    private final Workbook wb;
    private final Map<String, CellStyle> cellStyleMap = new HashMap<>();
    private CellStyle defaultStyle;

    private PoiBuilder(PoiBook poiBook) {
        this.poiBook = poiBook;
        this.wb = poiBook.getWorkbook();
    }

    public static Workbook build(PoiBook poiBook) {
        return new PoiBuilder(poiBook).build();
    }

    private Workbook build() {
        for (PoiSheet poiSheet : poiBook.getPoiSheetList()) {
            buildSheet(poiSheet);
        }
        return wb;
    }

    private void buildSheet(PoiSheet poiSheet) {
        AreaIndex areaIndex = new AreaIndex(poiSheet.getAlign());
        Sheet sheet = poiBook.getSheet(poiSheet);
        buildSheetPrintSetup(sheet, poiSheet);
        buildSheet(sheet, poiSheet.getAreas(), areaIndex);
    }

    private void buildSheetPrintSetup(Sheet sheet, PoiSheet poiSheet) {
        for (Tmp<Short, Double> margin : poiSheet.getMargins()) {
            sheet.setMargin(margin.getKey(), margin.getValue());
        }
        for (Tmp<Integer, Integer> columnWidth : poiSheet.getColumnWidths()) {
            sheet.setColumnWidth(columnWidth.getKey(), getColumnWidth(columnWidth.getValue()));
        }
        if (poiSheet.getPrintSetupPaperSize() != null) {
            sheet.getPrintSetup().setPaperSize(poiSheet.getPrintSetupPaperSize());
        }
        if (poiSheet.getPrintSetupScale() != null) {
            sheet.getPrintSetup().setScale(poiSheet.getPrintSetupScale());
        }
    }

    private void buildSheet(Sheet sheet, List<Area> areas, AreaIndex areaIndex) {
        for (Area area : areas) {
            if (area instanceof Align) {
                buildAlign(sheet, (Align) area, areaIndex);
            } else if (area instanceof PoiTable) {
                buildTable(sheet, (PoiTable) area, areaIndex);
            } else if (area instanceof PoiRow) {
                buildRow(sheet, (PoiRow) area, areaIndex);
            } else if (area instanceof Split) {
                buildSplit(sheet, (Split) area, areaIndex);
            }
        }
    }

    private void buildAlign(Sheet sheet, Align align, AreaIndex parentRowCel) {
        AreaIndex rowCell = createRowCel(parentRowCel, align.getAlign());
        buildSheet(sheet, align.getAreas(), rowCell);
        updateParent(parentRowCel, rowCell);
    }

    private void buildTable(Sheet sheet, PoiTable table, AreaIndex rowCel) {
        DataItem leafItem = table.getLeafItem();
        if (leafItem == null || !leafItem.loadNext()) {
            return;
        }

        int startRowNum = rowCel.getRow();
        int startCelNum = rowCel.getCel();
        int rowNum = startRowNum;
        int celNum = startCelNum;

        // 表头
        if (table.getShowTitle()) {
            TitleHandler titleHandler = getTitleHandler(sheet, startRowNum, startCelNum, table);
            //标题合并单元格
            mergedRegion(sheet, titleHandler);
            rowNum = titleHandler.getMaxRow() + 1;
            celNum = titleHandler.getMaxCel() + 1;
        } else {
            celNum = getTitleCel(celNum, table);
        }

        rowCel.setCel(celNum);
        rowNum = table.setCellValue(rowNum, startCelNum, (firstRow, lastRow, firstCel, lastCel, value, style) -> PoiUtil.mergedRegion(sheet, firstRow, lastRow, firstCel, lastCel, value, getCellStyle(style)));
        rowCel.setRow(rowNum);
    }

    /**
     * 优先合并列,再合并行
     *
     * @param sheet        sheet
     * @param titleHandler 需要合并的单元格集合
     */
    private void mergedRegion(Sheet sheet, TitleHandler titleHandler) {
        for (Map.Entry<String, List<CellRegion>> entry : titleHandler.getTitleMerge().entrySet()) {
            mergedRegion(sheet, entry.getKey(), entry.getValue());
        }
    }

    /**
     * 优先合并列,再合并行
     *
     * @param sheet       sheet
     * @param value       标题
     * @param cellRegions 需要合并的单元格集合
     */
    private void mergedRegion(Sheet sheet, String value, List<CellRegion> cellRegions) {
        if (cellRegions.isEmpty()) {
            return;
        }

        CellRegion[][] arrays = getRegionArrays(cellRegions);
        BitmapUtil.mergedRegion(arrays, (index1, index2) -> {
            CellRegion startRegion = arrays[index1.getRow()][index1.getCell()];
            if (index1.getRow() == index2.getRow() && index1.getCell() == index2.getCell()) {
                PoiUtil.setCellValue(PoiUtil.getRow(sheet, startRegion.getRow()), startRegion.getCel(), value, getCellStyle(startRegion.getStyle()));
            } else {
                CellRegion endRegion = arrays[index2.getRow()][index2.getCell()];
                PoiUtil.mergedRegion(sheet, startRegion.getRow(), endRegion.getRow(), startRegion.getCel(), endRegion.getCel(), value, getMergeStyle(startRegion.getStyle(), endRegion.getStyle()));
            }
        });
    }

    private CellRegion[][] getRegionArrays(List<CellRegion> cellRegions) {
        int minRow = Integer.MAX_VALUE;
        int minCel = Integer.MAX_VALUE;
        int maxRow = 0;
        int maxCel = 0;
        for (CellRegion cellRegion : cellRegions) {
            if (cellRegion.getRow() > maxRow) {
                maxRow = cellRegion.getRow();
            }
            if (cellRegion.getRow() < minRow) {
                minRow = cellRegion.getRow();
            }
            if (cellRegion.getCel() > maxCel) {
                maxCel = cellRegion.getCel();
            }
            if (cellRegion.getCel() < minCel) {
                minCel = cellRegion.getCel();
            }
        }
        CellRegion[][] arrays = new CellRegion[maxRow - minRow + 1][maxCel - minCel + 1];
        for (CellRegion cellRegion : cellRegions) {
            arrays[cellRegion.getRow() - minRow][cellRegion.getCel() - minCel] = cellRegion;
        }
        return arrays;
    }

    /**
     * 合并标题
     *
     * @param sheet       sheet
     * @param startRowNum table起始行号
     * @param titleCelNum column起始列号
     * @param table       table 为了取分组列
     */
    private TitleHandler getTitleHandler(Sheet sheet, int startRowNum, int titleCelNum, PoiTable<Object> table) {
        TitleHandler titleHandler = new TitleHandler(startRowNum);
        DataItem leafItem = table.getLeafItem();
        if (leafItem != null && leafItem.loadNext()) {
            for (Column iColumn : table.getColumns()) {
                if (iColumn instanceof PoiColumn) {
                    PoiColumn column = (PoiColumn) iColumn;
                    int cel = titleCelNum++;
                    if (column.getColumnWidth() != null) {
                        sheet.setColumnWidth(cel, getColumnWidth(column.getColumnWidth()));
                    }
                    //获取标题
                    titleHandler.addTitle(cel, getTitles(column, leafItem));
                } else if (iColumn instanceof PoiColumnItems) {
                    PoiColumnItems poiColumns = (PoiColumnItems) iColumn;
                    DataItem<Object> child = table.getItem(leafItem, poiColumns.getDataIndex()).child(poiColumns.getItemFunction());
                    wrapTitleHandler(sheet, titleCelNum, poiColumns, child, null, titleHandler);
                    titleCelNum = titleHandler.getMaxCel() + 1;
                }
            }
            titleHandler.appendTitle();
        }
        return titleHandler;
    }

    private void wrapTitleHandler(Sheet sheet, int titleCelNum, PoiColumnItems<Object, Object> poiColumns, DataItem<Object> item, List<StyleTitle> parentGroupTitles, TitleHandler titleHandler) {
        while (item.loadNext()) {
            //获取标题
            List<StyleTitle> groupTitles = mergeList(parentGroupTitles, getTitles(poiColumns, item));
            for (Column iColumn : poiColumns.getColumns()) {
                if (iColumn instanceof PoiColumn) {
                    int cel = titleCelNum++;
                    PoiColumn column = (PoiColumn) iColumn;
                    if (column.getColumnWidth() != null) {
                        sheet.setColumnWidth(cel, getColumnWidth(column.getColumnWidth()));
                    }
                    //获取标题
                    titleHandler.addTitle(cel, groupTitles);
                    titleHandler.addTitle(cel, getTitles(column, item));
                } else if (iColumn instanceof PoiColumnItems) {
                    PoiColumnItems childPoiColumns = (PoiColumnItems) iColumn;
                    DataItem<Object> child = item.child(childPoiColumns.getItemFunction());

                    wrapTitleHandler(sheet, titleCelNum, childPoiColumns, child, groupTitles, titleHandler);
                    titleCelNum = titleHandler.getMaxCel() + 1;
                }
            }
        }
    }

    /**
     * 获取标题
     */
    private List<StyleTitle> getTitles(PoiColumnItems column, DataItem item) {
        if (Objects.isNull(column.getTitles())) {
            return Collections.emptyList();
        }
        String[] titles = (String[]) column.getTitles().apply(item);
        List<StyleTitle> styleTitles = new ArrayList<>(titles.length);
        for (String title : titles) {
            styleTitles.add(new StyleTitle(title, column));
        }
        return styleTitles;
    }

    /**
     * 获取标题
     */
    private List<StyleTitle> getTitles(PoiColumn column, DataItem item) {
        if (Objects.isNull(column.getTitles())) {
            return Collections.emptyList();
        }
        String[] titles = (String[]) column.getTitles().apply(item);
        List<StyleTitle> styleTitles = new ArrayList<>(titles.length);
        for (String title : titles) {
            styleTitles.add(new StyleTitle(title, column));
        }
        return styleTitles;
    }

    /**
     * 合并标题
     */
    private List<StyleTitle> mergeList(List<StyleTitle> list1, List<StyleTitle> list2) {
        if (CollectionUtil.isEmpty(list1)) {
            return list2;
        }
        if (CollectionUtil.isEmpty(list2)) {
            return list1;
        }

        List<StyleTitle> result = new ArrayList<>();
        result.addAll(list1);
        result.addAll(list2);
        return result;
    }

    private int getTitleCel(int startCel, PoiTable<Object> table) {
        DataItem leafItem = table.getLeafItem();
        if (leafItem != null && leafItem.loadNext()) {
            for (Column iColumn : table.getColumns()) {
                if (iColumn instanceof PoiColumn) {
                    startCel++;
                } else if (iColumn instanceof PoiColumnItems) {
                    PoiColumnItems poiColumns = (PoiColumnItems) iColumn;
                    DataItem<Object> child = table.getItem(leafItem, poiColumns.getDataIndex()).child(poiColumns.getItemFunction());
                    startCel = getTitleCel(startCel, poiColumns.getColumns(), child);
                }
            }
        }
        return startCel;
    }

    private int getTitleCel(int startCel, List<Column> columns, DataItem<Object> item) {
        while (item.loadNext()) {
            for (Column iColumn : columns) {
                if (iColumn instanceof PoiColumn) {
                    startCel++;
                } else if (iColumn instanceof PoiColumnItems) {
                    PoiColumnItems poiColumns = (PoiColumnItems) iColumn;
                    ItemFunction<Object, Collection<Object>> valueFunction = poiColumns.getItemFunction();
                    startCel = getTitleCel(startCel, poiColumns.getColumns(), item.child(valueFunction));
                }
            }
        }
        return startCel;
    }

    private Integer getColumnWidth(Float columnWidth) {
        return PoiUtil.getColumnWidth(columnWidth);
    }

    private Integer getColumnWidth(Integer columnWidth) {
        return PoiUtil.getColumnWidth(columnWidth);
    }

    private void setCellValue(Row row, int celNum, Object value, PoiStyle titleStyle) {
        PoiUtil.setCellValue(row, celNum, value, getCellStyle(titleStyle, value));
    }

    private void buildRow(Sheet sheet, PoiRow poiRow, AreaIndex rowCel) {
        int rowNum = rowCel.getRow();
        int celNum = rowCel.getCel();

        Row row = PoiUtil.getRow(sheet, rowNum++);
        for (PoiRow.D8Cell d8Cell : poiRow.getD8Cells()) {
            if (d8Cell.getColumnWidth() != null) {
                for (int i = 0; i < d8Cell.getCel(); i++) {
                    sheet.setColumnWidth(celNum + i, getColumnWidth(d8Cell.getColumnWidth()));
                }
            }
            if (d8Cell.getCel() > 1 || poiRow.getRow() > 1) {
                // 添加合并cel逻辑
                PoiUtil.mergedRegion(sheet, rowNum - 1, rowNum + poiRow.getRow() - 2, celNum, celNum + d8Cell.getCel() - 1, d8Cell.getValue(), getCellStyle(PoiUtil.getStyle(d8Cell)));
                celNum += d8Cell.getCel();
            } else {
                setCellValue(row, celNum++, d8Cell.getValue(), PoiUtil.getStyle(d8Cell));
            }
        }

        rowCel.setCel(celNum);
        rowCel.setRow(rowNum);
    }

    private void buildSplit(Sheet sheet, Split split, AreaIndex rowCel) {
        Integer splitNum = split.getSplit();
        if (splitNum != null) {
            if (rowCel.getAlign() == ListAlign.DOWN) {
                rowCel.setRow(rowCel.getRow() + splitNum);
            } else {
                if (split.getColumnWidth() != null) {
                    for (int i = 0; i < splitNum; i++) {
                        sheet.setColumnWidth(rowCel.getCel() + i, getColumnWidth(split.getColumnWidth()));
                    }
                }
                rowCel.setCel(rowCel.getCel() + splitNum);
            }
        }
    }

    private AreaIndex createRowCel(AreaIndex parentRowCel, ListAlign align) {
        return new AreaIndex(align, parentRowCel.getRow(), parentRowCel.getCel());
    }

    private void updateParent(AreaIndex parentRowCel, AreaIndex rowCel) {
        parentRowCel.setRow(rowCel.getMaxRow());
        parentRowCel.setCel(rowCel.getMaxCel());
    }

    private CellStyle getMergeStyle(PoiStyle beginStyle, PoiStyle endStyle) {
        if (endStyle != null) {
            if (endStyle.getBorderBottom() != null) {
                beginStyle.setBorderBottom(endStyle.getBorderBottom());
            }
            if (endStyle.getBorderRight() != null) {
                beginStyle.setBorderRight(endStyle.getBorderRight());
            }
        }
        return getCellStyle(endStyle);
    }

    private CellStyle getCellStyle(PoiStyle style) {
        if (style == null) {
            return getDefaultStyle();
        }
        return cellStyleMap.computeIfAbsent(style.getKey(), k -> style.createCellStyle(wb));
    }

    private CellStyle getCellStyle(PoiStyle style, Object data) {
        if (style == null) {
            return getDefaultStyle();
        }
        if (style instanceof DynamicStyle) {
            PoiStyle finalStyle = ((DynamicStyle) style).parserDynamic(data);
            return cellStyleMap.computeIfAbsent(finalStyle.getKey(), k -> finalStyle.createCellStyle(wb));
        }
        return getCellStyle(style);
    }

    /**
     * 默认样式:上下居中并且有边框的单元格
     */
    private CellStyle getDefaultStyle() {
        if (defaultStyle == null) {
            defaultStyle = wb.createCellStyle();
            defaultStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            defaultStyle.setBorderBottom(BorderStyle.THIN);
            defaultStyle.setBorderLeft(BorderStyle.THIN);
            defaultStyle.setBorderTop(BorderStyle.THIN);
            defaultStyle.setBorderRight(BorderStyle.THIN);
        }
        return defaultStyle;
    }

    public interface CelValueFunction {
        default void setCelValue(int row, int cel, Object value, PoiStyle style) {
            setCelValue(row, row, cel, cel, value, style);
        }

        void setCelValue(int firstRow, int lastRow, int firstCel, int lastCel, Object value, PoiStyle style);
    }

    private static class TitleHandler {
        /**
         * 起始行号
         */
        private final int startRow;
        @Getter
        private final Map<String, List<CellRegion>> titleMerge = new HashMap<>();
        private final Map<Integer, List<StyleTitle>> celTitlesMap = new TreeMap<>();
        private int maxCel = -1;

        /**
         * @param startRow 起始行号
         */
        public TitleHandler(int startRow) {
            this.startRow = startRow;
        }

        /**
         * 添加一行标题
         *
         * @param cel    列
         * @param title  标题
         * @param column 样式
         * @return currRow 当前标题所在行
         */
        public int addTitle(int cel, String title, Node column) {
            return addTitle(cel, title, PoiUtil.getTitleStyle(column));
        }

        /**
         * 添加一行标题
         *
         * @param cel        列
         * @param title      标题
         * @param titleStyle 样式
         * @return currRow 当前标题所在行
         */
        public int addTitle(int cel, String title, PoiStyle titleStyle) {
            return addTitle(cel, new StyleTitle(title, titleStyle));
        }

        /**
         * 添加一行标题
         *
         * @param cel         列
         * @param styleTitles 标题&样式
         * @return currRow 当前标题所在行
         */
        public int addTitle(int cel, List<StyleTitle> styleTitles) {
            if (styleTitles == null || styleTitles.isEmpty()) {
                return getEndRow(cel);
            }
            for (StyleTitle styleTitle : styleTitles) {
                addTitle(cel, styleTitle);
            }
            return getEndRow(cel);
        }

        /**
         * 添加一行标题
         *
         * @param cel        列
         * @param styleTitle 标题&样式
         * @return currRow 当前标题所在行
         */
        public int addTitle(int cel, StyleTitle styleTitle) {
            List<StyleTitle> rowTitles = getRowTitles(cel);
            int row = startRow + rowTitles.size();
            rowTitles.add(styleTitle);
            getTitleRegions(styleTitle.getTitle()).add(new CellRegion(row, cel, styleTitle.getPoiStyle()));

            if (maxCel < cel) {
                maxCel = cel;
            }
            return row;
        }

        private List<CellRegion> getTitleRegions(String title) {
            return titleMerge.computeIfAbsent(title, k -> new ArrayList<>());
        }

        public List<StyleTitle> getRowTitles(int cel) {
            return celTitlesMap.computeIfAbsent(cel, k -> new ArrayList<>());
        }

        public int getMaxRow() {
            return startRow + getMaxSize() - 1;
        }

        private int getMaxSize() {
            int maxRow = 0;
            for (List<StyleTitle> value : celTitlesMap.values()) {
                if (maxRow < value.size()) {
                    maxRow = value.size();
                }
            }
            return maxRow;
        }

        public int getEndRow(int cel) {
            return startRow + getRowTitles(cel).size() - 1;
        }

        public int getMaxCel() {
            PoiAssert.isTrue(maxCel > 0, "表格列不能为空");
            return maxCel;
        }

        /**
         * 填充,保证最大行都一致
         */
        public void appendTitle() {
            int maxSize = getMaxSize();
            for (Map.Entry<Integer, List<StyleTitle>> entry : celTitlesMap.entrySet()) {
                List<StyleTitle> styleTitles = entry.getValue();
                if (styleTitles.size() < maxSize) {
                    int cel = entry.getKey();
                    StyleTitle lastTitle = styleTitles.isEmpty() ? getEmptyTitle(cel) : styleTitles.get(styleTitles.size() - 1);
                    do {
                        getTitleRegions(lastTitle.getTitle()).add(new CellRegion(startRow + styleTitles.size(), cel, lastTitle.getPoiStyle()));
                        styleTitles.add(lastTitle);
                    } while (styleTitles.size() < maxSize);
                }
            }
        }

        private StyleTitle getEmptyTitle(int cel) {
            List<StyleTitle> styleTitles = celTitlesMap.get(cel - 1);
            if (CollectionUtil.isEmpty(styleTitles)) {
                for (List<StyleTitle> value : celTitlesMap.values()) {
                    if (CollectionUtil.isNotEmpty(value)) {
                        return new StyleTitle(SymbolConstantUtil.EMPTY, value.get(0).getPoiStyle());
                    }
                }
            } else {
                return new StyleTitle(SymbolConstantUtil.EMPTY, styleTitles.get(0).getPoiStyle());
            }
            return new StyleTitle(SymbolConstantUtil.EMPTY, (PoiStyle) null);
        }
    }

    @Getter
    private static class StyleTitle {
        private final String title;
        private final PoiStyle poiStyle;

        public StyleTitle(String title, PoiStyle poiStyle) {
            this.title = title;
            this.poiStyle = poiStyle;
        }

        public StyleTitle(String title, Node column) {
            this.title = title;
            this.poiStyle = PoiUtil.getTitleStyle(column);
        }
    }
}
