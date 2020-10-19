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

package com.xyz.caofancpu.excel.xml.util;

import com.xyz.caofancpu.excel.exception.ExcelException;
import com.xyz.caofancpu.excel.xml.config.PoiStyleConfig;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * excel工具类
 *
 * @author D8GER
 * @author guanxiaochen
 */
public class XmlExcelUtil {
    public static Object ifNull(Object ifNull, Object object1, Object object2) {
        if (ifNull == null || Boolean.FALSE.equals(ifNull)) {
            return object1;
        }
        return object2;
    }

    public static Object ifNull(Object object1, Object object2) {
        if (object1 == null) {
            return object2;
        }
        return object1;
    }

    public static boolean isEmpty(Object object) {
        if (null == object) {
            return false;
        }
        if (object instanceof String) {
            return ((String) object).isEmpty();
        } else if (object instanceof Collection) {
            return ((Collection) object).isEmpty();
        } else if (object instanceof Map) {
            return ((Map) object).isEmpty();
        }
        return true;
    }

    /**
     * 排序
     */
    public static List<Object> reSort(List<Object> list, String eval) {
        list.sort((a, b) -> compare(eval, a, b));
        return list;
    }

    /**
     * 排序
     */
    public static List<Object> sort(List<Object> list, String eval) {
        list.sort((a, b) -> compare(eval, b, a));
        return list;
    }

    /**
     * 过滤
     */
    public static <T> List<T> not(Collection<T> list, String eval, Object value) {
        List<T> result = new ArrayList<>();
        for (T o : list) {
            Object property = getProperty(o, eval);
            if (property == null) {
                if (value != null) {
                    result.add(o);
                }
            } else if (value == null || !property.toString().equals(value.toString())) {
                result.add(o);
            }
        }
        return result;
    }

    private static int compare(String eval, Object a, Object b) {
        Comparable u1 = getProperty(a, eval);
        if (u1 == null) {
            return 1;
        }
        Comparable u2 = getProperty(b, eval);
        if (u2 == null) {
            return -1;
        }
        //noinspection unchecked
        return u2.compareTo(u1);
    }

    /**
     * 转换为Set
     *
     * @return
     */
    public static <F, T> Set<F> transSet(Collection<T> coll, String eval) {
        if (isEmpty(coll)) {
            return Collections.emptySet();
        }
        Function<T, F> function = i -> getProperty(i, eval);
        return coll.stream().map(function).collect(Collectors.toSet());
    }

    /**
     * 转换为List
     */
    public static <F, T> List<F> transList(Collection<T> coll, String eval) {
        if (isEmpty(coll)) {
            return Collections.emptyList();
        }
        Function<T, F> function = i -> getProperty(i, eval);
        return coll.stream().map(function).collect(Collectors.toList());
    }

    /**
     * 转换为去重的List
     */
    public static <F, T> List<F> distinctList(Collection<T> coll, String eval) {
        if (isEmpty(coll)) {
            return Collections.emptyList();
        }
        Function<T, F> function = i -> getProperty(i, eval);
        return coll.stream().map(function).distinct().collect(Collectors.toList());
    }

    /**
     * 去重
     */
    public static <T> List<T> distinctList(Collection<T> coll) {
        if (isEmpty(coll)) {
            return Collections.emptyList();
        }
        return coll.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 转换为Map-List
     */
    public static <T, F> Map<F, List<T>> groupIndex(Collection<T> coll, String eval) {
        if (isEmpty(coll)) {
            return Collections.emptyMap();
        }
        Function<T, F> function = i -> getProperty(i, eval);
        return coll.stream().collect(Collectors.groupingBy(function));
    }

    /**
     * 转换为Map-List
     */
    public static <T, F, R> Map<F, List<R>> groupIndex(Collection<T> coll, String group, String mapper) {
        if (isEmpty(coll)) {
            return Collections.emptyMap();
        }
        Function<T, F> groupFunction = i -> getProperty(i, group);
        Function<T, R> mapperFunction = i -> getProperty(i, mapper);
        return coll.stream().collect(Collectors.groupingBy(groupFunction, Collectors.mapping(mapperFunction, Collectors.toList())));
    }

    /**
     * 转换为Map-Value
     */
    public static <K, V> Map<K, V> index(Iterable<V> values, String eval) {
        Map<K, V> map = new HashMap<>();
        if (values == null) {
            return map;
        }
        Function<V, K> function = i -> getProperty(i, eval);
        for (V item : values) {
            map.put(function.apply(item), item);
        }
        return map;
    }

    /**
     * 转换为Map-Value
     */
    public static <T, K, V> Map<K, V> index(Iterable<T> values, String keyEval, String valueEval) {
        Map<K, V> map = new HashMap<>();
        if (values == null) {
            return map;
        }
        Function<T, K> keyFunction = i -> getProperty(i, keyEval);
        Function<T, V> valueFunction = i -> getProperty(i, valueEval);
        for (T item : values) {
            map.put(keyFunction.apply(item), valueFunction.apply(item));
        }
        return map;
    }

    public static String join(Collection<String> list) {
        return join(list, ",");
    }

    public static String join(Collection<String> list, String separator) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        if (list.size() == 1) {
            return list.iterator().next();
        }
        StringBuilder sb = new StringBuilder();
        for (String ele : list) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            sb.append(ele);
        }
        return sb.toString();
    }

    public static String join(String[] array, String separator) {
        if (array == null || array.length == 0) {
            return "";
        }
        if (array.length == 1) {
            return array[0];
        }
        StringBuilder sb = new StringBuilder();
        for (String ele : array) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            sb.append(ele);
        }
        return sb.toString();
    }

    /**
     * 保持原来顺序返回list分组
     *
     * @param list key不会跳着分布
     * @param eval
     * @return
     */
    private static <K, V> List<List<V>> groupList(Collection<V> list, String eval) {
        Function<? super V, K> function = i -> getProperty(i, eval);
        List<List<V>> result = new ArrayList<>();
        K cutKey = null;
        List<V> resultItem = new ArrayList<>();
        for (V item : list) {
            K key = function.apply(item);
            if (cutKey == null || !cutKey.equals(key)) {
                cutKey = key;
                resultItem = new ArrayList<>();
                result.add(resultItem);
            }
            resultItem.add(item);
        }
        return result;
    }

    /**
     * 从list里根据唯一字段值 查找
     */
    public static <T> List<T> find(Collection<T> list, String eval, String value) {
        List<T> result = new ArrayList<>();
        for (T o : list) {
            Object property = getProperty(o, eval);
            if (property == null) {
                if (value == null) {
                    result.add(o);
                }
            } else if (value != null && property.toString().equals(value)) {
                result.add(o);
            }
        }
        return result;
    }


    public static <T> T getProperty(Object a, String eval) {
        try {
            //noinspection unchecked
            return (T) PropertyUtils.getProperty(a, eval);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new ExcelException("获取属性异常", e);
        }
    }

    public static Row getRow(Sheet sheet, int rowIndex) {
        Row wrongRow = sheet.getRow(rowIndex);
        if (wrongRow == null) {
            wrongRow = sheet.createRow(rowIndex);
        }
        return wrongRow;
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
            XmlExcelUtil.setCellValue(getRow(sheet, firstRow), firstCol, value, centerStyle);
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
            XmlExcelUtil.setCellValue(getRow(sheet, firstRow), firstCol, value, centerStyle);
        }
    }

    /**
     * 设置带样式的单元格值
     *
     * @param row    行
     * @param colNum 列号
     */
    private static void setCellBorder(Row row, int colNum) {
        setCellBorder(row, colNum, true, true);
    }

    /**
     * 设置带样式的单元格值
     *
     * @param row    行
     * @param colNum 列号
     * @param top    顶部
     * @param bottom 底部
     */
    private static void setCellBorder(Row row, int colNum, boolean top, boolean bottom) {
        Cell cell = row.getCell(colNum);
        if (cell == null) {
            cell = row.createCell(colNum);
        }
        CellStyle cellStyle = cell.getCellStyle();
        cellStyle.setBorderLeft(BorderStyle.THIN);//左边框
        cellStyle.setBorderTop(BorderStyle.THIN);//上边框
        if (top) {
            cellStyle.setBorderBottom(BorderStyle.THIN); //下边框
        }
        if (bottom) {
            cellStyle.setBorderRight(BorderStyle.THIN);//右边框
        }
    }

    /**
     * 设置单元格值
     *
     * @param row    行
     * @param colNum 列号
     * @param value  内容
     */
    public static void setCellValue(Row row, int colNum, String value) {
        Cell cell1 = row.createCell(colNum);
        cell1.setCellValue(new HSSFRichTextString(value));
    }


    public static byte[] getWorkBookBytes(Workbook wb) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            wb.write(os);
            return os.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Excel保存失败");
        } finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (IOException ignored) {
                }
            }
        }
    }


    /**
     * 居中并且没有下边框的单元格
     */
    public static PoiStyleConfig getCenterNonBottomPoiStyle() {
        PoiStyleConfig centerStyle = new PoiStyleConfig();
        centerStyle.setAlign(HorizontalAlignment.CENTER); // 居中
        centerStyle.setVAlign(VerticalAlignment.CENTER); //上下居中
        centerStyle.setBorderLeft(BorderStyle.THIN); //左边框
        centerStyle.setBorderTop(BorderStyle.THIN); //上边框
        centerStyle.setBorderRight(BorderStyle.THIN); //右边框
        return centerStyle;
    }

    /**
     * 居中并且没有上边框的单元格
     */
    public static PoiStyleConfig getCenterNonTopPoiStyle() {
        PoiStyleConfig centerStyle = new PoiStyleConfig();
        centerStyle.setAlign(HorizontalAlignment.CENTER); // 居中
        centerStyle.setVAlign(VerticalAlignment.CENTER); //上下居中
        centerStyle.setBorderBottom(BorderStyle.THIN); //下边框
        centerStyle.setBorderLeft(BorderStyle.THIN); //左边框
        centerStyle.setBorderRight(BorderStyle.THIN); //右边框
        return centerStyle;
    }

    /**
     * 居中并且有边框的单元格
     */
    public static PoiStyleConfig getCenterPoiStyle() {
        PoiStyleConfig centerStyle = new PoiStyleConfig();
        centerStyle.setAlign(HorizontalAlignment.CENTER); // 居中
        centerStyle.setVAlign(VerticalAlignment.CENTER); //上下居中
        centerStyle.setBorderBottom(BorderStyle.THIN); //下边框
        centerStyle.setBorderLeft(BorderStyle.THIN); //左边框
        centerStyle.setBorderTop(BorderStyle.THIN); //上边框
        centerStyle.setBorderRight(BorderStyle.THIN); //右边框
        return centerStyle;
    }

    /**
     * 上下居中并且有边框的单元格
     */
    public static PoiStyleConfig getVCenterPoiStyle() {
        PoiStyleConfig centerStyle = new PoiStyleConfig();
        centerStyle.setVAlign(VerticalAlignment.CENTER); //上下居中
        centerStyle.setBorderBottom(BorderStyle.THIN); //下边框
        centerStyle.setBorderLeft(BorderStyle.THIN); //左边框
        centerStyle.setBorderTop(BorderStyle.THIN); //上边框
        centerStyle.setBorderRight(BorderStyle.THIN); //右边框
        return centerStyle;
    }
}
