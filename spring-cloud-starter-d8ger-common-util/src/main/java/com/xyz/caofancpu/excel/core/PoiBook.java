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
import lombok.Getter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;


/**
 * Apache的workbook的封装
 *
 * @author D8GER
 * @author guanxiaochen
 */
public class PoiBook extends Node {
    /**
     * excel文件名, 不带后缀
     */
    private final String name;
    /**
     * 支持多线程添加的PoiSheet,每个sheet还是要走单线程
     */
    @Getter
    private final List<PoiSheet> poiSheetList = new CopyOnWriteArrayList<>();
    /**
     * PoiSheet与Sheet的对应关系缓存表
     */
    private final Map<String, Sheet> cacheMap = new ConcurrentHashMap<>();
    @Getter
    private final Workbook workbook;

    /**
     * @param name           excel名称,自动添加后缀
     * @param createFunction 新建 SXSSFWorkbook | XSSFWorkbook | HSSFWorkbook
     */
    public PoiBook(String name, Supplier<Workbook> createFunction) {
        this.name = name;
        // 初始化时就创建
        workbook = createFunction.get();
    }

    /**
     * 默认使用 SXSSFWorkbook
     *
     * @param name excel名称,自动添加后缀.xlsx
     */
    public PoiBook(String name) {
        this(name, SXSSFWorkbook::new);
    }

    /**
     * SXSSF Excel2007--POI3.15版本
     *
     * @param name                excel名称,自动添加后缀.xlsx
     * @param rowAccessWindowSize 内存中保存的行数
     */
    public static PoiBook newSXSSFBook(String name, int rowAccessWindowSize) {
        return new PoiBook(name, () -> new SXSSFWorkbook(rowAccessWindowSize));
    }

    /**
     * SXSSF Excel2007--POI3.15版本
     *
     * @param name excel名称,自动添加后缀.xlsx
     */
    public static PoiBook newSXSSFBook(String name) {
        return new PoiBook(name, SXSSFWorkbook::new);
    }

    /**
     * XSSF Excel2007版本
     *
     * @param name excel名称,自动添加后缀.xlsx
     */
    public static PoiBook newXSSFBook(String name) {
        return new PoiBook(name, XSSFWorkbook::new);
    }

    /**
     * HSSF: Excel97-2003版本，扩展名为.xls
     *
     * @param name excel名称,自动添加后缀.xls
     */
    public static PoiBook newHSSFBook(String name) {
        return new PoiBook(name, HSSFWorkbook::new);
    }

    public synchronized PoiSheet addSheet(PoiSheet poiSheet) {
        poiSheet.parent(this);
        if (Objects.isNull(cacheMap.get(poiSheet.getNameKey()))) {
            poiSheetList.add(poiSheet);
            cacheMap.putIfAbsent(poiSheet.getNameKey(), getWorkbook().createSheet(poiSheet.getNameKey()));
        }
        return poiSheet;
    }

    /**
     * 如果未创建过Sheet则返回null
     *
     * @param poiSheet
     * @return
     */
    public Sheet getSheet(PoiSheet poiSheet) {
        return cacheMap.get(poiSheet.getNameKey());
    }

    /**
     * 构建填充Excel各个Sheet页数据
     *
     * @return
     */
    public Workbook buildWorkbook() {
        return PoiBuilder.build(this);
    }

    /**
     * Excel的文件全名
     *
     * @return
     */
    public String getFileName() {
        if (name.endsWith(SymbolConstantUtil.OLD_XLS_SUFFIX) || name.endsWith(SymbolConstantUtil.NEW_XLSX_SUFFIX)) {
            return name;
        }
        Class<? extends Workbook> clazz = workbook.getClass();
        if (XSSFWorkbook.class.isAssignableFrom(clazz) || SXSSFWorkbook.class.isAssignableFrom(clazz)) {
            return name + SymbolConstantUtil.NEW_XLSX_SUFFIX;
        }
        return name + SymbolConstantUtil.OLD_XLS_SUFFIX;
    }
}
