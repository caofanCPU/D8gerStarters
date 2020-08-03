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

import lombok.Getter;

/**
 * 位图合并工具类
 */
public class BitmapUtil {

    public static <T> void mergedRegion(T[][] arrays, IBitmapMerge mergeFunction) {
        if (arrays != null && arrays.length != 0) {
            mergedRegion(arrays, 0, 0, arrays[0].length - 1, mergeFunction);
        }
    }

    private static <T> void mergedRegion(T[][] arrays, int minRow, int minCel, int maxCel, IBitmapMerge mergeFunction) {
        int maxRow = arrays.length - 1;

        BitIndex startIndex = getNextIndex(arrays, minRow, minCel, maxCel);
        if (startIndex != null) {
            if (startIndex.getCell() > minCel) {
                //非首列 左右分开处理
                mergedRegion(arrays, startIndex.getRow() + 1, minCel, startIndex.getCell() - 1, mergeFunction);
                mergedRegion(arrays, startIndex.getRow(), startIndex.getCell(), maxCel, mergeFunction);
            } else {
                BitIndex endIndex = getEndIndex(arrays, startIndex, minCel, maxCel);

                // 同行
                if (startIndex.getRow() == endIndex.getRow()) {
                    //最后一行
                    if (endIndex.getRow() == maxRow) {
                        mergeFunction.apply(startIndex, endIndex);
                        if (endIndex.getCell() < maxCel) {
                            mergedRegion(arrays, startIndex.getRow(), endIndex.getCell() + 2, maxCel, mergeFunction);
                        }
                    } else if (endIndex.getCell() == maxCel) {
                        //最后一列 上下分开处理
                        mergeFunction.apply(startIndex, endIndex);
                        mergedRegion(arrays, endIndex.getRow() + 1, minCel, maxCel, mergeFunction);
                    } else {
                        //同一行 左右分开处理
                        mergedRegion(arrays, startIndex.getRow(), minCel, endIndex.getCell(), mergeFunction);
                        mergedRegion(arrays, startIndex.getRow(), endIndex.getCell() + 1, maxCel, mergeFunction);
                    }
                } else {
                    //最后一列
                    if (endIndex.getCell() == maxCel) {
                        //跨行 上下分开处理
                        mergeFunction.apply(startIndex, new BitIndex(endIndex.getRow(), maxCel));
                        if (endIndex.getRow() < maxRow) {
                            // 非最后一行
                            mergedRegion(arrays, endIndex.getRow() + 1, minCel, maxCel, mergeFunction);
                        }
                    } else {
                        mergeFunction.apply(startIndex, new BitIndex(endIndex.getRow() - 1, maxCel));
                        mergedRegion(arrays, endIndex.getRow(), minCel, maxCel, mergeFunction);
                    }
                }
            }
        }
    }

    private static <T> BitIndex getNextIndex(T[][] arrays, int minRow, int minCel, int maxCel) {
        for (int row = minRow; row < arrays.length; row++) {
            T[] array = arrays[row];
            for (int cel = minCel; cel <= maxCel; cel++) {
                if (array[cel] != null) {
                    return new BitIndex(row, cel);
                }
            }
        }
        return null;
    }


    private static <T> BitIndex getEndIndex(T[][] arrays, BitIndex excludeIndex, int minCel, int maxCel) {
        BitIndex emptyIndex = getEmptyIndex(arrays, excludeIndex, minCel, maxCel);
        if (emptyIndex == null) {
            return new BitIndex(arrays.length - 1, maxCel);
        }
        int row = emptyIndex.getRow();
        return emptyIndex.getCell() == minCel ? new BitIndex(row - 1, maxCel) : new BitIndex(row, emptyIndex.getCell() - 1);
    }


    private static <T> BitIndex getEmptyIndex(T[][] arrays, BitIndex excludeIndex, int minCel, int maxCel) {
        int minRow = excludeIndex.getRow();
        T[] firstRow = arrays[minRow];
        for (int cell = excludeIndex.getCell() + 1; cell <= maxCel; cell++) {
            if (firstRow[cell] == null) {
                return new BitIndex(minRow, cell);
            }
        }
        if (minRow + 1 < arrays.length) {
            for (int row = minRow + 1; row < arrays.length; row++) {
                T[] array = arrays[row];
                for (int cel = minCel; cel <= maxCel; cel++) {
                    if (array[cel] == null) {
                        return new BitIndex(row, cel);
                    }
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        Integer[][] integers = new Integer[1][9];
        integers[0] = new Integer[]{0, null, 0, null, 0, null, 0, null, 0};

        mergedRegion(integers, (a, b) -> {
            System.out.println(a.getCell() + ":" + a.getRow() + "-" + b.getCell() + ":" + b.getRow());
        });
    }

    public interface IBitmapMerge {
        void apply(BitIndex t1, BitIndex t2);
    }

    @Getter
    public static class BitIndex {
        private final int row;
        private final int cell;

        BitIndex(int row, int cell) {
            this.row = row;
            this.cell = cell;
        }
    }
}
