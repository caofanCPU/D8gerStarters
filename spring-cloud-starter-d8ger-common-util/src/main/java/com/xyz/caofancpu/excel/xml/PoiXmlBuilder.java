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

package com.xyz.caofancpu.excel.xml;


import com.xyz.caofancpu.excel.enums.ListAlign;
import com.xyz.caofancpu.excel.tmp.AreaIndex;
import com.xyz.caofancpu.excel.tmp.Region;
import com.xyz.caofancpu.excel.util.PoiAssert;
import com.xyz.caofancpu.excel.xml.config.AlignConfig;
import com.xyz.caofancpu.excel.xml.config.DataConfig;
import com.xyz.caofancpu.excel.xml.config.IAreaConfig;
import com.xyz.caofancpu.excel.xml.config.ListConfig;
import com.xyz.caofancpu.excel.xml.config.PoiStyleConfig;
import com.xyz.caofancpu.excel.xml.config.RowConfig;
import com.xyz.caofancpu.excel.xml.config.SetConfig;
import com.xyz.caofancpu.excel.xml.config.SheetConfig;
import com.xyz.caofancpu.excel.xml.config.SplitConfig;
import com.xyz.caofancpu.excel.xml.config.TableConfig;
import com.xyz.caofancpu.excel.xml.config.TableHeadConfig;
import com.xyz.caofancpu.excel.xml.config.WorkbookConfig;
import com.xyz.caofancpu.excel.xml.config.field.FieldConfig;
import com.xyz.caofancpu.excel.xml.config.field.GroupConfig;
import com.xyz.caofancpu.excel.xml.config.field.IFieldConfig;
import com.xyz.caofancpu.excel.xml.util.XmlExcelUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import static com.xyz.caofancpu.excel.util.PoiUtil.getColumnWidth;

/**
 * Excel 生成器
 */
public class PoiXmlBuilder {
    private final WorkbookConfig config;
    private final PoiXmlContext context = new PoiXmlContext();
    private final Map<String, CellStyle> cellStyleMap = new HashMap<>();
    private Workbook wb;
    private String fileName;

    public PoiXmlBuilder(WorkbookConfig config, Map<String, Object> params) {
        this.config = config;
        context.beginStack(params);
    }

    public String getTemplateName() {
        return config.getTemplateName();
    }

    public Workbook getWorkbook() {
        return wb;
    }

    public String getFileName() {
        return fileName;
    }

    public PoiXmlBuilder build() {
        wb = new HSSFWorkbook();
        for (SetConfig setConfig : config.getSets()) {
            buildSet(setConfig);
        }
        for (SheetConfig sheetConfig : config.getSheets()) {
            buildSheet(sheetConfig);
        }
        fileName = context.formatStr(config.getName());
        return this;
    }

    private void buildSheet(SheetConfig sheetConfig) {
        String sheetDataStr = sheetConfig.getData();
        if (sheetDataStr != null) {
            Object sheetDatas = context.get(sheetDataStr);
            if (sheetDatas != null) {
                if (sheetDatas instanceof Collection) {
                    for (Object sheetData : (Collection) sheetDatas) {
                        buildSheetAreas(sheetConfig, sheetData);
                    }
                } else {
                    buildSheetAreas(sheetConfig, sheetDatas);
                }
            }
        } else {
            buildSheetAreas(sheetConfig, null);
        }
    }

    private void buildSheetAreas(SheetConfig sheetConfig, Object sheetData) {
        try {
            context.beginStack();
            if (sheetData != null) {
                context.put("sheet", sheetData);
            }
            AreaIndex areaIndex = new AreaIndex(sheetConfig.getAlign());
            Sheet sheet = wb.createSheet(context.formatStr(sheetConfig.getName()));
            buildSheetAreas(sheet, sheetConfig.getAreaList(), areaIndex);
        } finally {
            context.endStack();
        }
    }

    private void buildSheetAreas(Sheet sheet, List<IAreaConfig> areas, AreaIndex areaIndex) {
        for (IAreaConfig area : areas) {
            if (area instanceof AlignConfig) {
                buildAlign(sheet, (AlignConfig) area, areaIndex);
            } else if (area instanceof ListConfig) {
                buildList(sheet, (ListConfig) area, areaIndex);
            } else if (area instanceof TableConfig) {
                buildTable(sheet, (TableConfig) area, areaIndex);
            } else if (area instanceof RowConfig) {
                buildRow(sheet, (RowConfig) area, areaIndex);
            } else if (area instanceof SplitConfig) {
                buildSplit(sheet, (SplitConfig) area, areaIndex);
            } else if (area instanceof SetConfig) {
                buildSet((SetConfig) area);
            }
        }
    }


    private void buildAlign(Sheet sheet, AlignConfig alignConfig, AreaIndex parentRowcel) {
        if (alignConfig.getFilter() != null) {
            Object filter = context.get(alignConfig.getFilter());
            if (filter == null || Boolean.FALSE.equals(filter)) {
                return;
            }
        }

        AreaIndex rowcel = createRowCel(parentRowcel, alignConfig.getAlign());
        buildSheetAreas(sheet, alignConfig.getAreaList(), rowcel);
        updateParent(parentRowcel, rowcel);
    }

    private void buildList(Sheet sheet, ListConfig listConfig, AreaIndex parentRowcel) {
        try {
            context.beginStack();

            AreaIndex rowcel = createRowCel(parentRowcel, listConfig.getAlign());
            String data = listConfig.getData();
            Object listDatas = context.get(data);
            if (listDatas != null) {
                PoiAssert.isTrue(listDatas instanceof Collection, "list data must collection");
                int index = 0;
                for (Object listData : (Collection) listDatas) {
                    context.put(listConfig.getItem(), listData);
                    context.put(listConfig.getIndex(), index);
                    if (index > 0) {
                        if (listConfig.getSplitConfig() != null) {
                            buildSplit(sheet, listConfig.getSplitConfig(), rowcel);
                        }
                    }
                    buildSheetAreas(sheet, listConfig.getAreaList(), rowcel);
                    index++;
                }
            }
            updateParent(parentRowcel, rowcel);
        } finally {
            context.endStack();
        }
    }

    private void buildTable(Sheet sheet, TableConfig tableConfig, AreaIndex rowcel) {
        try {
            if (tableConfig.getFilter() != null) {
                Object filter = context.get(tableConfig.getFilter());
                if (filter == null || Boolean.FALSE.equals(filter)) {
                    return;
                }
            }

            context.beginStack();

            Object data = getFirstData(tableConfig.getDataList().iterator());
            if (data == null) {
                return;
            }

            TableHeadConfig tableHead = tableConfig.getTableHead();
            int startRowNum = tableHead != null ? rowcel.getRow() + tableHead.getRow() : rowcel.getRow();
            int startCelNum = rowcel.getCel();
            int rowNum = startRowNum;
            int celNum = startCelNum;

            List<IFieldConfig> fields = tableConfig.getFields();
            if (tableConfig.getShowTitle()) {
                if (tableConfig.getMergeTitle()) {
                    //标题合并单元格
                    Map<Integer, Map<String, Region>> titleMerge = new HashMap<>();
                    celNum = setRegionMap(sheet, startRowNum, startCelNum, fields, titleMerge);
                    for (Map<String, Region> regionMap : titleMerge.values()) {
                        for (Map.Entry<String, Region> regionEntry : regionMap.entrySet()) {
                            Region region = regionEntry.getValue();
                            XmlExcelUtil.mergedRegion(sheet, region.getFirstRow(), region.getLastRow(), region.getFirstCol(), region.getLastCol(), regionEntry.getKey(), getCellStyle(region.getStyle(), context));
                        }
                    }
                    rowNum += getTitleLength(fields);
                } else {
                    Row row = XmlExcelUtil.getRow(sheet, rowNum++);
                    celNum = setTitleValue(sheet, row, celNum, fields);
                }
            } else {
                // 修复showTitle="false" bug
                celNum++;
            }
            if (tableHead != null) {
                XmlExcelUtil.mergedRegion(sheet, rowcel.getRow(), rowcel.getRow() + tableHead.getRow() - 1, startCelNum, celNum - 1, context.formatStr(tableHead.getName()), getCellStyle(tableHead.getStyle(), context));
            }

            rowcel.setCel(celNum);

            ListIterator<DataConfig> iterator = tableConfig.getDataList().listIterator();
            if (iterator.hasNext()) {
                Map<Integer, Map<Integer, Region>> mergeRagionMap = new HashMap<>();
                rowNum = setCellValue(sheet, startCelNum, rowNum, iterator, fields, mergeRagionMap);

                for (Map<Integer, Region> regionMap : mergeRagionMap.values()) {
                    if (!regionMap.isEmpty()) {
                        for (Region region : regionMap.values()) {
                            XmlExcelUtil.mergedRegion(sheet, region.getFirstRow(), region.getLastRow(), region.getFirstCol(), region.getLastCol(), region.getValue(), getCellStyle(region.getStyle(), context));
                        }
                    }
                }
            }

            rowcel.setRow(rowNum);
        } finally {
            context.endStack();
        }
    }

    private Object getFirstData(Iterator<DataConfig> iterator) {
        if (!iterator.hasNext()) {
            return null;
        }

        Object next = null;
        DataConfig dataConfig = iterator.next();
        Object datas = context.get(dataConfig.getData());
        if (datas instanceof Collection) {
            Collection datas1 = (Collection) datas;
            if (!datas1.isEmpty()) {
                next = datas1.iterator().next();
                context.put(dataConfig.getItem(), next);
            }
        }
        if (!iterator.hasNext()) {
            return next;
        }
        return getFirstData(iterator);
    }

    private int setCellValue(Sheet sheet, int startCelNum, int rowNum, ListIterator<DataConfig> iterator, List<IFieldConfig> fields, Map<Integer, Map<Integer, Region>> mergeRagionMap) {
        DataConfig next = iterator.next();
        Object datas = context.get(next.getData());
        PoiAssert.isTrue(datas instanceof Collection, "table data must collection");

        int previousIndex = iterator.previousIndex();
        Map<Integer, Region> regionMap = mergeRagionMap.get(previousIndex);
        if (previousIndex > 0 && regionMap != null && !regionMap.isEmpty()) {
            mergeRagionMap.get(0).putAll(regionMap);
        }
        mergeRagionMap.put(previousIndex, new HashMap<>());

        int i = 0;
        for (Object data : (Collection) datas) {
            context.put(next.getItem(), data);
            context.put(next.getIndex(), i++);
            if (iterator.hasNext()) {
                rowNum = setCellValue(sheet, startCelNum, rowNum, iterator, fields, mergeRagionMap);
            } else {
                Row row = XmlExcelUtil.getRow(sheet, rowNum++);
                setCellValue(row, startCelNum, fields, mergeRagionMap);
            }
        }
        iterator.previous();
        return rowNum;
    }

    private int getTitleLength(List<IFieldConfig> fields) {
        int maxTitles = 0;
        for (IFieldConfig field : fields) {
            if (field instanceof FieldConfig) {
                FieldConfig fieldConfig = (FieldConfig) field;
                if (fieldConfig.isShow()) {
                    String[] titles = fieldConfig.getTitles();
                    if (maxTitles < titles.length) {
                        maxTitles = titles.length;
                    }
                }
            } else if (field instanceof GroupConfig) {
                GroupConfig groupConfig = (GroupConfig) field;
                int groupTitleLength = getTitleLength(groupConfig.getFieldList());
                if (maxTitles < groupTitleLength) {
                    maxTitles = groupTitleLength;
                }
            }
        }
        return maxTitles;
    }

    private int setRegionMap(Sheet sheet, int startRowNum, int titleCelNum, List<IFieldConfig> fields, Map<Integer, Map<String, Region>> titleMerge) {
        HashMap<String, Region> regionMap = new HashMap<>();
        titleMerge.put(titleCelNum, regionMap);

        for (IFieldConfig field : fields) {
            if (field instanceof FieldConfig) {
                FieldConfig fieldConfig = (FieldConfig) field;
                if (fieldConfig.getFilter() != null) {
                    Object filter = context.get(fieldConfig.getFilter());
                    if (filter == null || Boolean.FALSE.equals(filter)) {
                        fieldConfig.setShow(false);
                        continue;
                    }
                }
                String[] titles = fieldConfig.getTitles();
                int cel = titleCelNum++;
                if (fieldConfig.getColumnWidth() != null) {
                    sheet.setColumnWidth(cel, getColumnWidth(fieldConfig.getColumnWidth()));
                }
                for (int i = 0; i < titles.length; i++) {
                    int row = startRowNum + i;

                    String titleValue = context.formatStr(titles[i]);
                    Region titleRagin = regionMap.get(titleValue);
                    if (titleRagin == null) {
                        titleRagin = new Region(row, row, cel, cel, fieldConfig.getTitleStyle());
                        regionMap.put(titleValue, titleRagin);
                    } else {
                        titleRagin.merge(row, row, cel, cel, fieldConfig.getTitleStyle());
                    }
                }
            } else if (field instanceof GroupConfig) {
                GroupConfig groupConfig = (GroupConfig) field;
                Object listDatas = context.get(groupConfig.getData());
                if (listDatas != null) {
                    PoiAssert.isTrue(listDatas instanceof Collection, "group data must collection");
                    int index = 0;
                    for (Object listData : (Collection) listDatas) {
                        context.put(groupConfig.getItem(), listData);
                        context.put(groupConfig.getIndex(), index++);
                        titleCelNum = setRegionMap(sheet, startRowNum, titleCelNum, groupConfig.getFieldList(), titleMerge);
                    }
                }
            }
        }
        return titleCelNum;
    }

    private int setTitleValue(Sheet sheet, Row row, int startCel, List<IFieldConfig> fields) {
        for (IFieldConfig field : fields) {
            if (field instanceof FieldConfig) {
                FieldConfig fieldConfig = (FieldConfig) field;
                if (fieldConfig.getFilter() != null) {
                    Object filter = context.get(fieldConfig.getFilter());
                    if (filter == null || Boolean.FALSE.equals(filter)) {
                        fieldConfig.setShow(false);
                        continue;
                    }
                }
                int celNum = startCel++;
                if (fieldConfig.getColumnWidth() != null) {
                    sheet.setColumnWidth(celNum, getColumnWidth(fieldConfig.getColumnWidth()));
                }
                setCellValue(row, celNum, context.formatStr(XmlExcelUtil.join(fieldConfig.getTitles(), "@@@@")), fieldConfig.getTitleStyle());
            } else if (field instanceof GroupConfig) {
                GroupConfig groupConfig = (GroupConfig) field;
                Object listDatas = context.get(groupConfig.getData());
                if (listDatas != null) {
                    PoiAssert.isTrue(listDatas instanceof Collection, "group data must collection");
                    int index = 0;
                    for (Object listData : (Collection) listDatas) {
                        context.put(groupConfig.getItem(), listData);
                        context.put(groupConfig.getIndex(), index++);
                        startCel = setTitleValue(sheet, row, startCel, groupConfig.getFieldList());
                    }
                }
            }
        }
        return startCel;
    }

    private void setCellValue(Row row, int celNum, Object value, PoiStyleConfig titleStyle) {
        XmlExcelUtil.setCellValue(row, celNum, value, getCellStyle(titleStyle, context));
    }

    private int setCellValue(Row row, int startCel, List<IFieldConfig> fields, Map<Integer, Map<Integer, Region>> mergeRagionMap) {
        for (IFieldConfig field : fields) {
            if (field instanceof FieldConfig) {
                FieldConfig fieldConfig = (FieldConfig) field;
                if (fieldConfig.isShow()) {
                    startEhanceStyleFormat(fieldConfig);

                    Integer mergeRow = fieldConfig.getMergeRow();
                    if (mergeRow != null) {
                        Map<Integer, Region> regionMap = mergeRagionMap.get(mergeRow);
                        if (regionMap != null) {
                            int cel = startCel++;
                            Region region = regionMap.get(cel);
                            if (region != null) {
                                region.setLastRow(row.getRowNum(), fieldConfig.getStyle());
                            } else {
                                region = new Region(row.getRowNum(), row.getRowNum(), cel, cel, fieldConfig.getStyle());
                                region.setValue(getFieldValue(fieldConfig).toString());
                                regionMap.put(cel, region);
                            }
                        }
                    } else {
                        setCellValue(row, startCel++, getFieldValue(fieldConfig), fieldConfig.getStyle());
                    }

                    endEhanceStyleFormat(fieldConfig);
                }
            } else if (field instanceof GroupConfig) {
                GroupConfig groupConfig = (GroupConfig) field;
                Object listDatas = context.get(groupConfig.getData());
                if (listDatas != null) {
                    PoiAssert.isTrue(listDatas instanceof Collection, "group data must collection");
                    int index = 0;
                    for (Object listData : (Collection) listDatas) {
                        context.put(groupConfig.getItem(), listData);
                        context.put(groupConfig.getIndex(), index++);
                        startCel = setCellValue(row, startCel, groupConfig.getFieldList(), mergeRagionMap);
                    }
                }
            }
        }
        return startCel;
    }

    private void startEhanceStyleFormat(FieldConfig fieldConfig) {
        // 此处添加处理styleFormat逻辑
        String styleFormat = fieldConfig.getStyle().getStyleFormat();
        if (styleFormat != null && styleFormat.contains("{{") && styleFormat.contains("}}")) {
            Object object = context.get(fieldConfig.getName());
            if (fieldConfig.getDefaultValue() != null && object == null) {
                context.put("styleFormat", styleFormat);
                styleFormat = "";
                fieldConfig.getStyle().setStyleFormat(styleFormat);
                context.put("newStyleFormat", styleFormat);
                return;
            }

            context.put("styleFormat", styleFormat);
            String eval = styleFormat.substring(styleFormat.indexOf("{{") + 2, styleFormat.lastIndexOf("}}"));
            Object evalValue = context.get(eval);
            styleFormat = styleFormat.substring(0, styleFormat.indexOf("{{")) + evalValue + styleFormat.substring(styleFormat.indexOf("}}") + 2);
            fieldConfig.getStyle().setStyleFormat(styleFormat);
            context.put("newStyleFormat", styleFormat);
        }
    }

    private void endEhanceStyleFormat(FieldConfig fieldConfig) {
        String styleFormatInContext = (String) context.getObjectFromPeekMap("styleFormat");
        String newStyleFormatInContext = (String) context.getObjectFromPeekMap("newStyleFormat");
        if (styleFormatInContext != null && newStyleFormatInContext != null && newStyleFormatInContext.equals(fieldConfig.getStyle().getStyleFormat())) {
            fieldConfig.getStyle().setStyleFormat(styleFormatInContext);
            context.remove("styleFormat");
            context.remove("newStyleFormat");
        }
    }

    private Object getFieldValue(FieldConfig fieldConfig) {
        Object object = context.get(fieldConfig.getName());
        if (object == null) {
            return fieldConfig.getDefaultValue();
        }

        if (fieldConfig.getEval() != null) {
            object = context.get(fieldConfig.getEval(), object);
            if (object == null) {
                return fieldConfig.getDefaultValue();
            }
        }

        if (fieldConfig.getFormat() != null) {
            return String.format(fieldConfig.getFormat(), object);
        }

        if (fieldConfig.getEnumFormat() != null) {
            return fieldConfig.getEnumFormat().get(object.toString());
        }

        if (fieldConfig.getDateFormat() != null) {
            return fieldConfig.getDateFormat().format(object);
        }

        if (fieldConfig.getStyle().getStyleFormat() == null) {
            return object.toString();
        }
        return object;
    }

    private void buildRow(Sheet sheet, RowConfig rowConfig, AreaIndex rowcel) {
        if (rowConfig.getFilter() != null) {
            Object filter = context.get(rowConfig.getFilter());
            if (filter == null || Boolean.FALSE.equals(filter)) {
                return;
            }
        }

        int rowNum = rowcel.getRow();
        int celNum = rowcel.getCel();

        Row row = XmlExcelUtil.getRow(sheet, rowNum++);
        for (RowConfig.D8Cell d8CellConfig : rowConfig.getCellList()) {
            if (d8CellConfig.getFilter() != null) {
                Object filter = context.get(d8CellConfig.getFilter());
                if (filter == null || Boolean.FALSE.equals(filter)) {
                    continue;
                }
            }
            // 添加横向合并cel逻辑
            if (d8CellConfig.getCell() > 1) {
                XmlExcelUtil.mergedRegion(sheet, rowNum - 1, rowNum - 1, celNum, celNum + d8CellConfig.getCell() - 1, context.formatStr(d8CellConfig.getValue()), getCellStyle(d8CellConfig.getStyle(), context));
                celNum += d8CellConfig.getCell();
                continue;
            }
            if (d8CellConfig.getColumnWidth() != null) {
                sheet.setColumnWidth(celNum, getColumnWidth(d8CellConfig.getColumnWidth()));
            }
            setCellValue(row, celNum++, getCellValue(d8CellConfig), d8CellConfig.getStyle());
        }

        rowcel.setCel(celNum);
        rowcel.setRow(rowNum);
    }

    private String getCellValue(RowConfig.D8Cell d8CellConfig) {
        String valueString = context.formatStr(d8CellConfig.getValue());
        if (valueString == null || "".equals(valueString)) {
            if (d8CellConfig.getDefaultValue() != null) {
                valueString = d8CellConfig.getDefaultValue();
            }
        }
        return valueString;
    }

    private void buildSplit(Sheet sheet, SplitConfig splitConfig, AreaIndex rowcel) {
        if (splitConfig.getFilter() != null) {
            Object filter = context.get(splitConfig.getFilter());
            if (filter == null || Boolean.FALSE.equals(filter)) {
                return;
            }
        }
        Integer split = splitConfig.getSplit();
        if (split != null) {
            if (rowcel.getAlign() == ListAlign.DOWN) {
                rowcel.setRow(rowcel.getRow() + split);
            } else {
                if (splitConfig.getColumnWidth() != null) {
                    for (int i = 0; i < split; i++) {
                        sheet.setColumnWidth(rowcel.getCel() + i, getColumnWidth(splitConfig.getColumnWidth()));
                    }
                }
                rowcel.setCel(rowcel.getCel() + split);
            }
        }
    }

    private void buildSet(SetConfig setConfig) {
        if (setConfig.getEval() == null) {
            context.put(setConfig.getName(), context.get(setConfig.getData()));
        } else if (setConfig.getData() == null) {
            context.put(setConfig.getName(), context.get(setConfig.getEval()));
        } else {
            context.put(setConfig.getName(), context.get(setConfig.getEval(), context.get(setConfig.getData())));
        }
    }

    private AreaIndex createRowCel(AreaIndex parentRowcel, ListAlign align) {
        return new AreaIndex(align, parentRowcel.getRow(), parentRowcel.getCel());
    }

    private void updateParent(AreaIndex parentRowcel, AreaIndex rowcel) {
        parentRowcel.setRow(rowcel.getMaxRow());
        parentRowcel.setCel(rowcel.getMaxCel());
    }

    private CellStyle getCellStyle(PoiStyleConfig style, ExpressionParser parser) {
        PoiStyleConfig finalStyle = style.parserDynamic(parser);
        return cellStyleMap.computeIfAbsent(finalStyle.getKey(), k -> finalStyle.createCellStyle(wb));
    }
}
