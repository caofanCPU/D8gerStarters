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

package com.xyz.caofancpu.extra;

import com.xyz.caofancpu.constant.SymbolConstantUtil;

import java.util.Objects;

/**
 * 测试常用工具类
 *
 * @author D8GER
 */
public class NormalUseForTestUtil {

    public static void outNextLine() {
        System.out.println(SymbolConstantUtil.EMPTY);
    }

    public static void out(Object text) {
        System.out.println(text);
    }

    public static void outWithoutLn(Object text) {
        System.out.print(text);
    }

    public static void outWithSpace(Object text) {
        System.out.print(text + SymbolConstantUtil.SPACE);
    }

    /**
     * 对象转String
     *
     * @param source
     * @return
     */
    public static String convertToString(Object source) {
        return Objects.isNull(source) ? null : source.toString();
    }

    /**
     * 对象转Integer
     *
     * @param source
     * @return
     */
    public static Integer convertToInteger(Object source) {
        if (Objects.isNull(source)) {
            return null;
        }
        return Integer.parseInt(source.toString());
    }

    /**
     * 对象转Long
     *
     * @param source
     * @return
     */
    public static Long convertToLong(Object source) {
        if (Objects.isNull(source)) {
            return null;
        }
        return Long.parseLong(source.toString());
    }
}
