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


import com.xyz.caofancpu.excel.exception.ExcelException;
import com.xyz.caofancpu.excel.xml.config.WorkbookConfig;
import com.xyz.caofancpu.excel.xml.util.XmlExcelUtil;

import java.util.Map;

/**
 * Excel 生成器
 *
 * @author D8GER
 * @author guanxiaochen
 */
public class PoiXmlManager {
    protected PoiXmlConfigManager configManager;

    public PoiXmlManager() {
        this.configManager = new PoiXmlConfigManager();
        FunctionManager.initClass("PoiUtil", XmlExcelUtil.class);
        FunctionManager.initClass("Long", Long.class);
        FunctionManager.initClass("Integer", Integer.class);
        FunctionManager.initClass("Float", Float.class);
        FunctionManager.initClass("String", String.class);
    }

    @Deprecated
    public WorkbookConfig loadFromPath(String locations) {
        return configManager.loadFromPath(locations);
    }

    public WorkbookConfig loadFromStr(String templateId, String xml) {
        return configManager.loadFromStr(templateId, xml);
    }

    @Deprecated
    public WorkbookConfig loadFromStr(String xml) {
        return configManager.loadFromStr(xml);
    }

    public PoiXmlBuilder createBuilder(String id, Map<String, Object> params) {
        WorkbookConfig config = configManager.getWorkbookConfig(id);
        if (config == null) {
            config = initWorkbookConfig(id);
        }
        initParams(config, params);
        return new PoiXmlBuilder(config, params);
    }

    public WorkbookConfig initWorkbookConfig(String id) {
        throw new ExcelException("模板" + id + "不存在");
    }

    public void initParams(WorkbookConfig config, Map<String, Object> params) {

    }
}
