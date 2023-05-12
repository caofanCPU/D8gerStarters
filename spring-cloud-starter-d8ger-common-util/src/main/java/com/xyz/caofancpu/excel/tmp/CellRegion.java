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

package com.xyz.caofancpu.excel.tmp;

import com.xyz.caofancpu.excel.core.PoiStyle;
import lombok.Getter;

/**
 * 一个单元格
 *
 * @author D8GER
 * @author guanxiaochen
 */
@Getter
public class CellRegion implements Comparable<CellRegion> {
    private final int row;
    private final int cel;
    private PoiStyle style;

    public CellRegion(int row, int cel) {
        this.row = row;
        this.cel = cel;
    }

    public CellRegion(int row, int cel, PoiStyle style) {
        this.row = row;
        this.cel = cel;
        this.style = style;
    }

    @Override
    public int compareTo(CellRegion o) {
        int i = this.row - o.row;
        if (i == 0) {
            return this.cel - o.cel;
        }
        return i;
    }
}
