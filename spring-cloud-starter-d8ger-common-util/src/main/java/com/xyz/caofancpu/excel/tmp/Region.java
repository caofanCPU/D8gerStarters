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

import com.xyz.caofancpu.excel.xml.config.PoiStyleConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * 临时对象
 */
@Getter
@Setter
public class Region {
    private int firstRow;
    private int lastRow;
    private int firstCol;
    private int lastCol;
    private String value;
    private PoiStyleConfig style;

    public Region(int firstRow, int lastRow, int firstCol, int lastCol, PoiStyleConfig style) {
        this.firstRow = firstRow;
        this.lastRow = lastRow;
        this.firstCol = firstCol;
        this.lastCol = lastCol;
        this.style = style;
    }


    public void merge(int firstRow, int lastRow, int firstCol, int lastCol, PoiStyleConfig style) {
        if (this.firstRow > firstRow) {
            this.firstRow = firstRow;
            this.style.setBorderTop(style.getBorderTop());
        }
        if (this.lastRow < lastRow) {
            this.lastRow = lastRow;
            this.style.setBorderBottom(style.getBorderBottom());
        }
        if (this.firstCol > firstCol) {
            this.firstCol = firstCol;
            this.style.setBorderLeft(style.getBorderLeft());
        }
        if (this.lastCol < lastCol) {
            this.lastCol = lastCol;
            this.style.setBorderRight(style.getBorderRight());
        }
    }

    public void setLastRow(int lastRow, PoiStyleConfig style) {
        if (this.lastRow < lastRow) {
            this.lastRow = lastRow;
            this.style.setBorderBottom(style.getBorderBottom());
        }
    }
}
