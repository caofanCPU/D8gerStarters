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

import com.xyz.caofancpu.excel.exception.ExcelException;
import com.xyz.caofancpu.excel.xml.ISymbol;
import com.xyz.caofancpu.excel.xml.ISymbolParser;
import com.xyz.caofancpu.excel.xml.util.XExpressionUtil;
import com.xyz.caofancpu.excel.xml.util.XStringUtil;

/**
 * xml运算符解析
 */
public enum Symbol implements ISymbol {

    ADD('+') {
        @Override
        public Object parse(ISymbolParser parser, String key, int index, Object object) {
            return XExpressionUtil.add(parser.parseExpression(key.substring(0, index)), object);
        }
    },

    SUB('-') {
        @Override
        public Object parse(ISymbolParser parser, String key, int index) {
            if (index == 0) {
                //仅仅是取负值
                Object value = parser.parseExpression(key.substring(1));
                if (!(value instanceof Number)) {
                    return null;
                }
                return -((Number) value).doubleValue();
            }
            return super.parse(parser, key, index);
        }

        @Override
        public Object parse(ISymbolParser parser, String key, int index, Object object) {
            return XExpressionUtil.sub(parser.parseExpression(key.substring(0, index)), object);
        }
    },

    MUL('*') {
        @Override
        public Object parse(ISymbolParser parser, String key, int index, Object object) {
            return XExpressionUtil.mul(parser.parseExpression(key.substring(0, index)), object);
        }
    },

    DIV('/') {
        @Override
        public Object parse(ISymbolParser parser, String key, int index, Object object) {
            return XExpressionUtil.div(parser.parseExpression(key.substring(0, index)), object);
        }
    },

    MOD('%') {
        @Override
        public Object parse(ISymbolParser parser, String key, int index, Object object) {
            return XExpressionUtil.mod(parser.parseExpression(key.substring(0, index)), object);
        }
    },

    EQ('=') {
        @Override
        public Object parse(ISymbolParser parser, String key, int index, Object object) {
            char c1 = key.charAt(index - 1);
            if ('=' == c1) {
                return parse(parser, key, index - 1, object);
            }
            if ('!' == c1) {
                return NEQ.parse(parser, key, index - 1, object);
            }
            if ('>' == c1) {
                return GTE.parse(parser, key, index - 1, object);
            }
            if ('<' == c1) {
                return LTE.parse(parser, key, index - 1, object);
            }
            Object o = parser.parseExpression(key.substring(0, index));
            if (object == null && o == null) {
                return true;
            }
            return !(object == null || o == null) && object.equals(o);
        }
    },

    GT('>') {
        @Override
        public Object parse(ISymbolParser parser, String key, int index, Object object) {
            if (!(object instanceof Number)) {
                return true;
            }
            Object o = parser.parseExpression(key.substring(0, index));
            if (!(o instanceof Number)) {
                return false;
            }
            return ((Number) o).doubleValue() > ((Number) object).doubleValue();
        }
    },

    LT('<') {
        @Override
        public Object parse(ISymbolParser parser, String key, int index, Object object) {
            if (!(object instanceof Number)) {
                return false;
            }
            Object o = parser.parseExpression(key.substring(0, index));
            if (!(o instanceof Number)) {
                return true;
            }
            return ((Number) o).doubleValue() < ((Number) object).doubleValue();
        }
    },

    GTE('≥') {
        @Override
        public Object parse(ISymbolParser parser, String key, int index) {
            return parse(parser, key, index, parser.parseExpression(key.substring(index + 2)));
        }

        @Override
        public Object parse(ISymbolParser parser, String key, int index, Object object) {
            if (!(object instanceof Number)) {
                return true;
            }
            Object o = parser.parseExpression(key.substring(0, index));
            if (!(o instanceof Number)) {
                return false;
            }
            return ((Number) o).doubleValue() >= ((Number) object).doubleValue();
        }
    },

    LTE('≤') {
        @Override
        public Object parse(ISymbolParser parser, String key, int index) {
            return parse(parser, key, index, parser.parseExpression(key.substring(index + 2)));
        }

        @Override
        public Object parse(ISymbolParser parser, String key, int index, Object object) {
            if (!(object instanceof Number)) {
                return false;
            }
            Object o = parser.parseExpression(key.substring(0, index));
            if (!(o instanceof Number)) {
                return true;
            }
            return ((Number) o).doubleValue() <= ((Number) object).doubleValue();
        }
    },

    PARENTHESIS(')') {
        @Override
        public Object parse(ISymbolParser parser, String key, int index) {
            int startIndex = XStringUtil.getParenthesesStart(key, index);
            if (startIndex <= 0) {
                return parser.parseExpression(key.substring(startIndex + 1, index));
            }

            String prev = key.substring(0, startIndex);

            // 前边是表达式
            int prevIndex = XStringUtil.lastIndexOfAny(prev, EXPRESSION_MID);
            if (prevIndex > -1) {
                // 先算括号
                Symbol symbol = Symbol.create(key.charAt(prevIndex));
                Object object = symbol.parse(parser, prev, prevIndex, parser.parseExpression(key.substring(startIndex + 1, index).trim()));
                if (index < key.length() - 1) {
                    return parser.getProperty(object, key.substring(index + 1));
                }
                return object;
            }

            // 大写,静态方法
            if (Character.isUpperCase(key.charAt(0))) {
                return parser.invokeStaticMethod(key);
            }

            // 方法,前边是点
            int spotIndexB = prev.lastIndexOf(SPOT);
            if (spotIndexB <= 0) {
                throw new ExcelException("表达式错误:[" + key + "]");
            }
            //获取对象
            Object valueB = parser.parseExpression(prev.substring(0, spotIndexB).trim());
            if (valueB == null) {
                return null;
            }
            //获取对象方法
            return parser.getProperty(valueB, key.substring(spotIndexB + 1).trim());
        }
    },

    VERTICAL('|') {
        @Override
        public Object parse(ISymbolParser parser, String key, int index, Object object) {
            char c1 = key.charAt(index - 1);
            if ('|' == c1) {
                return OR.parse(parser, key, index - 1, object);
            }

            if (!(object instanceof Number)) {
                return null;
            }
            Object o = parser.parseExpression(key.substring(0, index));
            if (!(o instanceof Number)) {
                return null;
            }
            return ((Number) object).intValue() | ((Number) o).intValue();
        }
    },

    OR('‖') {
        @Override
        public Object parse(ISymbolParser parser, String key, int index) {
            return parse(parser, key, index, parser.parseExpression(key.substring(index + 2)));
        }

        @Override
        public Object parse(ISymbolParser parser, String key, int index, Object object) {
            Boolean o = XExpressionUtil.getBoolean(parser.parseExpression(key.substring(0, index - 1)));
            if (o) {
                return true;
            }
            return XExpressionUtil.getBoolean(object);
        }
    },

    WITH('&') {
        @Override
        public Object parse(ISymbolParser parser, String key, int index, Object object) {
            if ('&' == key.charAt(index - 1)) {
                return AND.parse(parser, key, index - 1, object);
            }
            if (!(object instanceof Number)) {
                return null;
            }
            Object o = parser.parseExpression(key.substring(0, index));
            if (!(o instanceof Number)) {
                return null;
            }
            return ((Number) object).intValue() & ((Number) o).intValue();
        }
    },

    AND('§') {
        @Override
        public Object parse(ISymbolParser parser, String key, int index) {
            return parse(parser, key, index, parser.parseExpression(key.substring(index + 2)));
        }

        @Override
        public Object parse(ISymbolParser parser, String key, int index, Object object) {
            Boolean o = XExpressionUtil.getBoolean(parser.parseExpression(key.substring(0, index - 1)));
            if (!o) {
                return false;
            }
            return XExpressionUtil.getBoolean(object);
        }
    },

    TILDE('~') {
        @Override
        public Object parse(ISymbolParser parser, String key, int index, Object object) {
            if (!(object instanceof Number)) {
                return null;
            }
            return ~((Number) object).intValue();
        }
    },

    QUESTION('?') {
        @Override
        public Object parse(ISymbolParser parser, String key, int index) {
            if (index == 0) {
                return null;
            }
            // 前面是括号
            String prev = key.substring(0, index).trim();
            if (prev.charAt(prev.length() - 1) == ')') {
                int start = XStringUtil.getParenthesesStart(prev, prev.length() - 1);
                Object valueE = parser.parseExpression(prev.substring(start + 1, prev.length() - 1));
                if (start < 0) {
                    return parse(parser, key, index, valueE);
                }
                return Symbol.parse(parser, prev.substring(0, start), parse(parser, key, index, valueE));
            }

            // 前面是表达式
            int indexMid = XStringUtil.lastIndexOfAny(prev, EXPRESSION_MID);
            if (indexMid < 0) {
                return parse(parser, key, index, parser.parseExpression(prev));
            }

            Symbol symbol = Symbol.create(key.charAt(indexMid));
            return parse(parser, key, indexMid, symbol.parse(parser, prev, indexMid, parser.parseExpression(prev.substring(indexMid + 1))));
        }

        @Override
        public Object parse(ISymbolParser parser, String key, int index, Object object) {
            int indexA = key.lastIndexOf(":");
            if (indexA < 0) {
                return null;
            }
            Boolean bObject = XExpressionUtil.getBoolean(object);
            if (bObject) {
                return parser.parseExpression(key.substring(key.indexOf("?") + 1, indexA));
            }
            return parser.parseExpression(key.substring(indexA + 1));
        }
    },

    NOT('!') {
        @Override
        public Object parse(ISymbolParser parser, String key, int index, Object object) {
            return !XExpressionUtil.getBoolean(object);
        }
    },

    NEQ('≠') {
        @Override
        public Object parse(ISymbolParser parser, String key, int index) {
            return parse(parser, key, index, parser.parseExpression(key.substring(index + 2)));
        }

        @Override
        public Object parse(ISymbolParser parser, String key, int index, Object object) {
            Object eq = EQ.parse(parser, key, index, object);
            return !XExpressionUtil.getBoolean(eq);
        }
    },

    ;

    private final char symbol;

    Symbol(char symbol) {
        this.symbol = symbol;
    }

    public static Symbol create(char symbol) {
        for (Symbol item : Symbol.values()) {
            if (symbol == item.symbol) {
                return item;
            }
        }
        throw new RuntimeException("表达式错误");
    }

    public static Object parse(ISymbolParser parser, String key, Object object) {
        int index = XStringUtil.lastIndexOfAny(key, EXPRESSION_MID);
        if (index < 0) {
            return object;
        }
        Symbol symbol = Symbol.create(key.charAt(index));
        return symbol.parse(parser, key, index, object);
    }

    public char getSymbol() {
        return symbol;
    }

}
