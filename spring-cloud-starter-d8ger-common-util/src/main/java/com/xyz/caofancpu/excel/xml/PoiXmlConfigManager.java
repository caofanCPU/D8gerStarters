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
import com.xyz.caofancpu.excel.exception.ExcelException;
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
import com.xyz.caofancpu.excel.xml.util.XmlElementUtil;
import com.xyz.caofancpu.excel.xml.util.XmlExcelUtil;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel XML定义读取注册
 */
public class PoiXmlConfigManager {

    /**
     * 注册信息
     */
    private static final PoiStyleConfig DEFAULT_STYLE = XmlExcelUtil.getVCenterPoiStyle();

    /**
     * 注册信息
     */
    private final Map<String, WorkbookConfig> workbookConfigMap = new HashMap<>();

    public WorkbookConfig getWorkbookConfig(String id) {
        return workbookConfigMap.get(id);
    }

    @Deprecated
    public void loadFromPaths(String locations) {
        if (StringUtils.isBlank(locations)) {
            throw new IllegalArgumentException("locations 不能为空");
        }
        for (String location : StringUtils.split(locations, ",")) {
            //读取xml文件
            Element rootElement;
            try (InputStream is = XmlElementUtil.getResourceAsStream(location)) {
                rootElement = new SAXReader().read(is).getRootElement();
            } catch (Exception e) {
                throw new ExcelException("获取配置失败", e);
            }
            WorkbookConfig workbookConfig = loadWorkbookConfig(rootElement);
            workbookConfigMap.put(workbookConfig.getTemplateId(), workbookConfig);
        }
    }

    @Deprecated
    public WorkbookConfig loadFromPath(String location) {
        if (StringUtils.isBlank(location)) {
            throw new IllegalArgumentException("location 不能为空");
        }
        //读取xml文件
        Element rootElement;
        try (InputStream is = XmlElementUtil.getResourceAsStream(location)) {
            rootElement = new SAXReader().read(is).getRootElement();
        } catch (Exception e) {
            throw new ExcelException("获取配置失败", e);
        }
        WorkbookConfig workbookConfig = loadWorkbookConfig(rootElement);
        if (workbookConfig.getTemplateId() == null) {
            throw new ExcelException("模板Id为空");
        }
        workbookConfigMap.put(workbookConfig.getTemplateId(), workbookConfig);
        return workbookConfig;
    }

    public WorkbookConfig loadFromPath(String templateId, String location) {
        if (StringUtils.isBlank(location)) {
            throw new IllegalArgumentException("location 不能为空");
        }
        //读取xml文件
        Element rootElement;
        try (InputStream is = XmlElementUtil.getResourceAsStream(location)) {
            rootElement = new SAXReader().read(is).getRootElement();
        } catch (Exception e) {
            throw new ExcelException("获取配置失败", e);
        }
        WorkbookConfig workbookConfig = loadWorkbookConfig(rootElement);
        workbookConfig.setTemplateId(templateId);
        workbookConfigMap.put(templateId, workbookConfig);
        return workbookConfig;
    }

    @Deprecated
    public WorkbookConfig loadFromStr(String xml) {
        //读取xml文件
        Element rootElement;
        try {
            rootElement = new SAXReader().read(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))).getRootElement();
        } catch (DocumentException e) {
            throw new ExcelException("获取配置失败", e);
        }
        WorkbookConfig workbookConfig = loadWorkbookConfig(rootElement);
        if (workbookConfig.getTemplateId() == null) {
            throw new ExcelException("模板Id为空");
        }
        workbookConfigMap.put(workbookConfig.getTemplateId(), workbookConfig);
        return workbookConfig;
    }

    public WorkbookConfig loadFromStr(String templateId, String xml) {
        //读取xml文件
        Element rootElement;
        try {
            rootElement = new SAXReader().read(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))).getRootElement();
        } catch (DocumentException e) {
            throw new ExcelException("获取配置失败", e);
        }
        WorkbookConfig workbookConfig = loadWorkbookConfig(rootElement);
        workbookConfig.setTemplateId(templateId);
        workbookConfigMap.put(templateId, workbookConfig);
        return workbookConfig;
    }


    private WorkbookConfig loadWorkbookConfig(Element rootElement) {
        WorkbookConfig workbookConfig = new WorkbookConfig();
        workbookConfig.setTemplateId(XmlElementUtil.getAttributeRequired(rootElement, "id"));
        workbookConfig.setTemplateName(XmlElementUtil.getAttribute(rootElement, "templateName"));
        workbookConfig.setType(XmlElementUtil.getAttribute(rootElement, "type"));
        workbookConfig.setName(XmlElementUtil.getAttributeRequired(rootElement, "name"));

        //noinspection unchecked
        List<Element> setEles = rootElement.elements("set");
        for (Element sheetEle : setEles) {
            workbookConfig.getSets().add(getSetConfig(sheetEle));
        }

        //noinspection unchecked
        List<Element> sheetEles = rootElement.elements("sheet");
        for (Element sheetEle : sheetEles) {
            SheetConfig sheetConfig = new SheetConfig();
            sheetConfig.setName(XmlElementUtil.getAttributeRequired(sheetEle, "name"));
            sheetConfig.setData(XmlElementUtil.getAttribute(sheetEle, "data"));
            sheetConfig.setAlign(ListAlign.create(XmlElementUtil.getAttribute(sheetEle, "align", "down")));

            //noinspection unchecked
            sheetConfig.setAreaList(getAreaConfigs(sheetEle.elements(), DEFAULT_STYLE));

            workbookConfig.getSheets().add(sheetConfig);
        }
        return workbookConfig;
    }

    private List<IAreaConfig> getAreaConfigs(List<Element> elements, PoiStyleConfig style) {
        List<IAreaConfig> areas = new ArrayList<>();
        for (Element areaEle : elements) {
            areas.add(getAreaConfig(areaEle, style));
        }
        return areas;
    }

    private IAreaConfig getAreaConfig(Element element, PoiStyleConfig style) {
        IAreaConfig area;
        if ("align".equals(element.getName())) {
            area = getAlignConfig(element, style);
        } else if ("list".equals(element.getName())) {
            area = getListConfig(element, style);
        } else if ("row".equals(element.getName())) {
            area = getRowConfig(element, style);
        } else if ("split".equals(element.getName())) {
            area = getSplitConfig(element);
        } else if ("table".equals(element.getName())) {
            area = getTableConfig(element, style);
        } else if ("set".equals(element.getName())) {
            area = getSetConfig(element);
        } else {
            throw new ExcelException("获取配置失败");
        }
        return area;
    }

    private AlignConfig getAlignConfig(Element element, PoiStyleConfig style) {
        AlignConfig alignConfig = new AlignConfig();
        alignConfig.setAlign(ListAlign.create(XmlElementUtil.getAttribute(element, "align", "down")));
        alignConfig.setFilter(XmlElementUtil.getAttribute(element, "filter"));
        //noinspection unchecked
        alignConfig.setAreaList(getAreaConfigs(element.elements(), style));
        return alignConfig;
    }

    private ListConfig getListConfig(Element element, PoiStyleConfig style) {
        ListConfig listConfig = new ListConfig();
        listConfig.setData(XmlElementUtil.getAttributeRequired(element, "data"));
        listConfig.setItem(XmlElementUtil.getAttribute(element, "item", "item"));
        listConfig.setIndex(XmlElementUtil.getAttribute(element, "index", "index"));
        Integer split = XmlElementUtil.getIntAttribute(element, "split");
        if (split != null) {
            SplitConfig splitConfig = new SplitConfig();
            splitConfig.setSplit(split);
            splitConfig.setColumnWidth(XmlElementUtil.getColumnWidth(element, "splitWidth"));
            listConfig.setSplitConfig(splitConfig);
        }
        listConfig.setAlign(ListAlign.create(XmlElementUtil.getAttribute(element, "align", "down")));
        //noinspection unchecked
        listConfig.setAreaList(getAreaConfigs(element.elements(), style));
        return listConfig;
    }


    private RowConfig getRowConfig(Element element, PoiStyleConfig style) {
        RowConfig rowConfig = new RowConfig();
        rowConfig.setRow(XmlElementUtil.getIntAttribute(element, "row"));
        rowConfig.setFilter(XmlElementUtil.getAttribute(element, "filter"));
        rowConfig.setStyle(XmlElementUtil.getStyle(element, style));

        //noinspection unchecked
        List<Element> cellElements = element.elements("cel");
        if (cellElements != null && !cellElements.isEmpty()) {
            for (Element celEle : cellElements) {
                RowConfig.D8Cell d8Cell = rowConfig.new D8Cell();
                d8Cell.setRow(XmlElementUtil.getIntAttribute(celEle, "row"));
                d8Cell.setCell(XmlElementUtil.getIntAttribute(celEle, "cel"));
                d8Cell.setColumnWidth(XmlElementUtil.getColumnWidth(celEle, "columnWidth"));
                d8Cell.setDefaultValue(XmlElementUtil.getAttribute(celEle, "defaultValue"));
                d8Cell.setFilter(XmlElementUtil.getAttribute(celEle, "filter"));
                d8Cell.setValue(celEle.getText());
                d8Cell.setStyle(XmlElementUtil.getStyle(celEle, rowConfig.getStyle()));
                rowConfig.getCellList().add(d8Cell);
            }
        } else {
            RowConfig.D8Cell d8Cell = rowConfig.new D8Cell();
            d8Cell.setCell(XmlElementUtil.getIntAttribute(element, "cel"));
            d8Cell.setFilter(XmlElementUtil.getAttribute(element, "filter"));
            d8Cell.setValue(element.getText());
            d8Cell.setStyle(rowConfig.getStyle());
            rowConfig.getCellList().add(d8Cell);
        }
        return rowConfig;
    }


    private SplitConfig getSplitConfig(Element element) {
        SplitConfig splitConfig = new SplitConfig();
        splitConfig.setSplit(XmlElementUtil.getIntAttributeRequired(element, "split"));
        splitConfig.setFilter(XmlElementUtil.getAttribute(element, "filter"));
        splitConfig.setColumnWidth(XmlElementUtil.getColumnWidth(element, "columnWidth"));
        return splitConfig;
    }

    private TableConfig getTableConfig(Element element, PoiStyleConfig style) {
        TableConfig tableConfig = new TableConfig();

        tableConfig.setDataList(getDataConfigs(element));
        tableConfig.setFilter(XmlElementUtil.getAttribute(element, "filter"));
        tableConfig.setShowTitle(Boolean.valueOf(XmlElementUtil.getAttribute(element, "showTitle", "true")));
        tableConfig.setMergeTitle(Boolean.valueOf(XmlElementUtil.getAttribute(element, "mergeTitle", "false")));
        tableConfig.setStyle(XmlElementUtil.getStyle(element, style));
        tableConfig.setTitleStyle(XmlElementUtil.getTitleStyle(element, tableConfig.getStyle()));

        //noinspection unchecked
        List<Element> elements = element.elements();
        for (Element childElement : elements) {
            if ("set".equals(childElement.getName())) {
                tableConfig.getSets().add(getSetConfig(childElement));
            } else if ("field".equals(childElement.getName())) {
                tableConfig.getFields().add(getFieldConfig(childElement, tableConfig.getStyle(), tableConfig.getTitleStyle()));
            } else if ("group".equals(childElement.getName())) {
                tableConfig.getFields().add(getFieldGroupConfig(childElement, tableConfig.getStyle(), tableConfig.getTitleStyle()));
            } else if ("head".equals(childElement.getName())) {
                tableConfig.setTableHead(getTableHeadConfig(childElement, tableConfig.getStyle()));
            }
        }
        return tableConfig;
    }

    private List<DataConfig> getDataConfigs(Element element) {
        List<DataConfig> dataConfigs = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DataConfig dataConfig = getDataConfig(element, i);
            if (dataConfig == null) {
                break;
            }
            dataConfigs.add(dataConfig);
        }
        return dataConfigs;
    }

    private DataConfig getDataConfig(Element element, int i) {
        String suffix = "";
        if (i > 0) {
            suffix = "_" + i;
        }
        String dataAttribute = XmlElementUtil.getAttribute(element, "data" + suffix);
        if (dataAttribute == null) {
            return null;
        }

        DataConfig dataConfig = new DataConfig();
        dataConfig.setData(dataAttribute);
        dataConfig.setItem(XmlElementUtil.getAttribute(element, "item" + suffix, "item" + suffix));
        dataConfig.setIndex(XmlElementUtil.getAttribute(element, "index" + suffix, "index" + suffix));
        return dataConfig;
    }

    private SetConfig getSetConfig(Element areaEle) {
        SetConfig setConfig = new SetConfig();
        setConfig.setName(XmlElementUtil.getAttributeRequired(areaEle, "name"));
        setConfig.setData(XmlElementUtil.getAttribute(areaEle, "data"));
        setConfig.setEval(XmlElementUtil.getAttribute(areaEle, "eval"));
        return setConfig;
    }

    private TableHeadConfig getTableHeadConfig(Element element, PoiStyleConfig style) {
        TableHeadConfig headConfig = new TableHeadConfig();
        headConfig.setName(XmlElementUtil.getAttribute(element, "name"));
        headConfig.setRow(XmlElementUtil.getIntAttribute(element, "row", 1));
        headConfig.setStyle(XmlElementUtil.getStyle(element, style));

        return headConfig;
    }

    /**
     * 合并多列
     */
    private GroupConfig getFieldGroupConfig(Element element, PoiStyleConfig style, PoiStyleConfig titleStyle) {
        GroupConfig fieldConfig = new GroupConfig();
        fieldConfig.setData(XmlElementUtil.getAttributeRequired(element, "data"));
        fieldConfig.setItem(XmlElementUtil.getAttribute(element, "item", "item"));
        fieldConfig.setIndex(XmlElementUtil.getAttribute(element, "index", "index"));
        //noinspection unchecked
        List<Element> childElements = element.elements();
        for (Element childElement : childElements) {
            if ("field".equals(childElement.getName())) {
                fieldConfig.getFieldList().add(getFieldConfig(childElement, style, titleStyle));
            } else if ("group".equals(childElement.getName())) {
                fieldConfig.getFieldList().add(getFieldGroupConfig(childElement, style, titleStyle));
            }
        }
        return fieldConfig;
    }

    private FieldConfig getFieldConfig(Element element, PoiStyleConfig style, PoiStyleConfig titleStyle) {
        FieldConfig fieldConfig = new FieldConfig();
        fieldConfig.setName(XmlElementUtil.getAttributeRequired(element, "name"));
        fieldConfig.setTitles(XmlElementUtil.getAttributeRequired(element, "title").split("@@@@"));
        String format = XmlElementUtil.getAttribute(element, "format");
        if (format != null) {
            fieldConfig.setFormat(format);
        } else {
            format = XmlElementUtil.getAttribute(element, "enumFormat");
            if (format != null) {
                String[] split = format.split(",");
                Map<String, String> formatMap = new HashMap<>();
                for (String splitItem : split) {
                    String[] splitEntry = splitItem.split(":");
                    if (splitEntry.length > 1) {
                        formatMap.put(splitEntry[0], splitEntry[1]);
                    }
                }
                fieldConfig.setEnumFormat(formatMap);
            } else {
                String pattern = XmlElementUtil.getAttribute(element, "dateFormat");
                if (pattern != null) {
                    fieldConfig.setDateFormat(new SimpleDateFormat(pattern.trim()));
                }
            }
        }
        fieldConfig.setEval(XmlElementUtil.getAttribute(element, "eval"));
        fieldConfig.setFilter(XmlElementUtil.getAttribute(element, "filter"));

        fieldConfig.setColumnWidth(XmlElementUtil.getColumnWidth(element, "columnWidth"));
        fieldConfig.setStyle(XmlElementUtil.getStyle(element, style));
        fieldConfig.setTitleStyle(XmlElementUtil.getTitleStyle(element, titleStyle));

        fieldConfig.setDefaultValue(XmlElementUtil.getAttribute(element, "defaultValue", ""));
        fieldConfig.setMergeRow(XmlElementUtil.getIntAttribute(element, "mergeRow"));
        return fieldConfig;
    }

    public void setWorkbookConfigMap(Map<String, WorkbookConfig> workbookConfigMap) {
        if (workbookConfigMap != null) {
            this.workbookConfigMap.putAll(workbookConfigMap);
        }
    }
}
