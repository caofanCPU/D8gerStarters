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

import com.xyz.caofancpu.excel.enums.ListAlign;
import lombok.Getter;

/**
 * excel行列坐标信息
 *
 * @author D8GER
 * @author guanxiaochen
 */
@Getter
public class AreaIndex {
    ListAlign align;
    /**
     * 行坐标
     */
    int row = 0;
    /**
     * 列坐标
     */
    int cel = 0;

    /**
     * 最远坐标位置, 如果是向下排列,本值的意思是右边cel已使用的最远坐标点(Down?cel:row)
     * 1111
     * 222222
     * 3333
     * 44444
     * max = 2.length
     */
    int max = 0;

    public AreaIndex(ListAlign align) {
        this.align = align;
    }

    public AreaIndex(ListAlign align, int row, int cel) {
        this.align = align;
        this.row = row;
        this.cel = cel;
        this.max = align == ListAlign.DOWN ? cel : row;
    }

    public void addSplit(int split) {
        if (align == ListAlign.DOWN) {
            this.row += split;
        } else {
            this.cel += split;
        }
    }

    public void setRow(int row) {
        if (align == ListAlign.DOWN) {
            this.row = row;
        } else if (row > max) {
            this.max = row;
        }
    }

    public void setCel(int cel) {
        if (align == ListAlign.RIGHT) {
            this.cel = cel;
        } else if (cel > max) {
            this.max = cel;
        }
    }

    public int getMaxRow() {
        if (align == ListAlign.DOWN) {
            return row;
        }
        return max;
    }

    public int getMaxCel() {
        if (align == ListAlign.RIGHT) {
            return cel;
        }
        return max;
    }
}
