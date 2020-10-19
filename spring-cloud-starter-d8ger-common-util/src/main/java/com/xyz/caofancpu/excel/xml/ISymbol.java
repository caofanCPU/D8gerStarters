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

import com.xyz.caofancpu.excel.exception.ExcelException;

/**
 * 运算符解析算法
 *
 * @author D8GER
 * @author guanxiaochen
 */
public interface ISymbol {
    /**
     * 点
     */
    char SPOT = '.';
    /**
     * 运算符
     */
    char[] EXPRESSION = {')', '+', '-', '*', '/', '%', '&', '|', '〜', '!', '>', '<', '=', '?'};
    /**
     * 中间运算符
     */
    char[] EXPRESSION_MID = {'+', '-', '*', '/', '%', '&', '|', '!', '>', '<', '=', '?'};

    default Object parse(ISymbolParser parser, String key, int index) {
        // 前面是表达式
        return parse(parser, key, index, parser.parseExpression(key.substring(index + 1)));
    }

    default Object parse(ISymbolParser parser, String key, int index, Object object) {
        if (index <= 0) {
            //忽略符号
            return object;
        }
        throw new ExcelException("表达式错误:[" + key + "]");
    }
}
