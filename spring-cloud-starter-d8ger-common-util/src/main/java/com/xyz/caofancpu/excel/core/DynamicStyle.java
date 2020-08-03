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

import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.util.function.Function;

/**
 * 样式
 */
@Setter
@Accessors(chain = true)
public class DynamicStyle<F> extends PoiStyle {

    /**
     * 动态cell字体
     */
    private Function<F, String> dFontName;
    /**
     * 动态cell字体大小
     */
    private Function<F, Short> dFontSize;
    /**
     * 动态cell字体大小
     */
    private Function<F, Boolean> dFontBold;
    /**
     * 动态cell字体色
     */
    private Function<F, IndexedColors> dFontColor;
    /**
     * 动态cell背景色
     */
    private Function<F, IndexedColors> dBgColor;
    /**
     * 格式
     */
    private Function<F, String> dStyleFormat;

    /**
     * 解析动态样式
     */
    public DynamicStyle<F> parserDynamic(F object) {
        if (dFontName != null) {
            setFontName(dFontName.apply(object));
        }
        if (dFontSize != null) {
            setFontSize(dFontSize.apply(object));
        }
        if (dFontBold != null) {
            setFontBold(dFontBold.apply(object));
        }
        if (dFontColor != null) {
            setFontColor(dFontColor.apply(object));
        }
        if (dBgColor != null) {
            setBgColor(dBgColor.apply(object));
        }
        if (dStyleFormat != null) {
            setStyleFormat(dStyleFormat.apply(object));
        }
        return this;
    }
}
