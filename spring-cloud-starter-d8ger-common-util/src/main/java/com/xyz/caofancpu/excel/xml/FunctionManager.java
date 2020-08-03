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

package com.xyz.caofancpu.excel.xml;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 对象管理
 */
public class FunctionManager {
    static final Map<String, Function> FUNCTION_MAP = new HashMap<>();
    private static final Map<String, Class<?>> STATIC_CLASS_MAP = new HashMap<>();

    public static void initEval(String eval, Function function) {
        FUNCTION_MAP.put(eval, function);
    }

    public static void initClass(String name, Class<?> zClass) {
        STATIC_CLASS_MAP.put(name, zClass);
    }

    public static Class<?> getClassName(String className) {
        Class<?> aClass = STATIC_CLASS_MAP.get(className);
        if (aClass != null) {
            return aClass;
        }
        try {
            aClass = Class.forName(className);
            STATIC_CLASS_MAP.put(className, aClass);
            return aClass;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
