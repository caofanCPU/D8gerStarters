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

import lombok.Getter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;


public class PoiBook extends Node {
    private static final String XLS_SUFFIX = ".xls";
    private static final String XLSX_SUFFIX = ".xlsx";

    final Supplier<Workbook> createFunction;
    private final String name;
    /**
     * 支持多线程添加的PoiSheet,每个sheet还是要去单线程
     */
    @Getter
    private final List<PoiSheet> sheets = new CopyOnWriteArrayList<>();
    private Workbook workbook;

    /**
     * @param name           excel名称,自动添加后缀
     * @param createFunction 新建 SXSSFWorkbook | XSSFWorkbook | HSSFWorkbook
     */
    public PoiBook(String name, Supplier<Workbook> createFunction) {
        this.name = name;
        this.createFunction = createFunction;
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

    public synchronized PoiSheet addSheet(PoiSheet sheet) {
        sheet.parent(this);
        sheets.add(sheet);
        return sheet;
    }

    public synchronized Workbook getWorkbook() {
        if (workbook == null) {
            workbook = PoiBuilder.build(this, createFunction.get());
        }
        return workbook;
    }

    public String getFileName() {
        if (name.endsWith(XLS_SUFFIX) || name.endsWith(XLSX_SUFFIX)) {
            return name;
        }
        Class<? extends Workbook> clazz = workbook != null ? workbook.getClass() : createFunction.get().getClass();
        if (XSSFWorkbook.class.isAssignableFrom(clazz) || SXSSFWorkbook.class.isAssignableFrom(clazz)) {
            return name + XLSX_SUFFIX;
        }
        return name + XLS_SUFFIX;
    }

    public Boolean isExitSheet(String sheetName) {
        for (PoiSheet sheet : sheets) {
            if (sheet.getName().equals(sheetName)) {
                return true;
            }
        }
        return false;
    }
}
