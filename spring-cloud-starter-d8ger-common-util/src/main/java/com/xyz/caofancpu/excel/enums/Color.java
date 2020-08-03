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

package com.xyz.caofancpu.excel.enums;

/**
 * 颜色
 */
public enum Color {
    BLACK,
    WHITE,
    RED,
    BRIGHT_GREEN,
    BLUE,
    YELLOW,
    PINK,
    TURQUOISE,
    DARK_RED,
    GREEN,
    DARK_BLUE,
    DARK_YELLOW,
    VIOLET,
    TEAL,
    GREY_25_PERCENT,
    GREY_50_PERCENT,
    CORNFLOWER_BLUE,
    MAROON,
    LEMON_CHIFFON,
    ORCHID,
    CORAL,
    ROYAL_BLUE,
    LIGHT_CORNFLOWER_BLUE,
    SKY_BLUE,
    LIGHT_TURQUOISE,
    LIGHT_GREEN,
    LIGHT_YELLOW,
    PALE_BLUE,
    ROSE,
    LAVENDER,
    TAN,
    LIGHT_BLUE,
    AQUA,
    LIME,
    GOLD,
    LIGHT_ORANGE,
    ORANGE,
    BLUE_GREY,
    GREY_40_PERCENT,
    DARK_TEAL,
    SEA_GREEN,
    DARK_GREEN,
    OLIVE_GREEN,
    BROWN,
    PLUM,
    INDIGO,
    GREY_80_PERCENT,
    AUTOMATIC,
    ;

    public static Color create(String name) {
        if (name == null) {
            return null;
        }
        return valueOf(name.toUpperCase());
    }
}
