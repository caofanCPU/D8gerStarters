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

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * 样式
 *
 * @author D8GER
 * @author guanxiaochen
 */
@Data
@Accessors(chain = true)
public class PoiStyle {
    /**
     * cell字体
     */
    private String fontName;
    /**
     * cell字体大小
     */
    private Short fontSize;
    /**
     * cell字体大小
     */
    private Boolean fontBold;
    /**
     * cell字体色
     */
    private IndexedColors fontColor;
    /**
     * cell背景色
     */
    private IndexedColors bgColor;

    /**
     * 自动换行
     */
    private Boolean wrapText;

    /**
     * 水平对齐
     */
    private HorizontalAlignment align;
    /**
     * 上下对齐
     */
    private VerticalAlignment vAlign;

    /**
     * 下边框
     */
    private BorderStyle borderBottom;
    /**
     * 左边框
     */
    private BorderStyle borderLeft;
    /**
     * 上边框
     */
    private BorderStyle borderTop;
    /**
     * 右边框
     */
    private BorderStyle borderRight;

    /**
     * 格式
     */
    private String styleFormat;

    public CellStyle createCellStyle(Workbook wb) {
        CellStyle centerStyle = wb.createCellStyle();
        if (fontName != null || fontSize != null || fontColor != null || fontBold != null) {
            Font font = wb.createFont();
            if (fontName != null) {
                font.setFontName(fontName);
            }
            if (fontSize != null) {
                font.setFontHeightInPoints(fontSize);
            }
            if (fontBold != null) {
                font.setBold(fontBold);
            }
            if (fontColor != null) {
                font.setColor(fontColor.getIndex());
            }
            centerStyle.setFont(font);
        }
        if (wrapText != null) {
            centerStyle.setWrapText(wrapText);
        }
        if (styleFormat == null && bgColor != null) {
            centerStyle.setFillForegroundColor(bgColor.getIndex());
            centerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        if (align != null) {
            centerStyle.setAlignment(align);
        }
        if (vAlign != null) {
            centerStyle.setVerticalAlignment(vAlign);
        }
        if (borderBottom != null) {
            centerStyle.setBorderBottom(borderBottom);
        }
        if (borderLeft != null) {
            centerStyle.setBorderLeft(borderLeft);
        }
        if (borderTop != null) {
            centerStyle.setBorderTop(borderTop);
        }
        if (borderRight != null) {
            centerStyle.setBorderRight(borderRight);
        }
        if (styleFormat != null) {
            centerStyle.setDataFormat(wb.createDataFormat().getFormat(styleFormat));
        }
        return centerStyle;
    }

    public String getKey() {
        return "" + fontName + "-" + fontSize + "-" + fontBold + "-" + fontColor + "-" + bgColor + "-" + wrapText + "-" + align + "-" + vAlign + "-" + borderBottom + "-" + borderLeft + "-" + borderTop + "-" + borderRight + "-" + styleFormat;
    }
}
