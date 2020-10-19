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

package com.xyz.caofancpu.excel.xml.util;

import com.xyz.caofancpu.excel.tmp.Tmp;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Stack;

/**
 * 字符串处理
 *
 * @author D8GER
 * @author guanxiaochen
 */
public class XStringUtil {
    /**
     * 获取数值结束的位置及数值 起始位必须是数字
     */
    public static Tmp<Integer, Number> getNum(String eval) {
        boolean hasSpot = false;
        char[] chars = eval.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char aChar = chars[i];
            if ('.' == aChar) {
                hasSpot = true;
                continue;
            }
            if (Character.isDigit(aChar)) {
                continue;
            }

            if ('d' == aChar || 'D' == aChar) {
                return new Tmp<>(i, Double.valueOf(eval.substring(0, i)));
            }
            if ('f' == aChar || 'F' == aChar) {
                return new Tmp<>(i, Float.valueOf(eval.substring(0, i)));
            }
            return new Tmp<>(i - 1, Integer.valueOf(eval.substring(0, i)));
        }
        if (hasSpot) {
            return new Tmp<>(eval.length() - 1, Double.valueOf(eval));
        }
        return new Tmp<>(eval.length() - 1, Integer.valueOf(eval));
    }

    /**
     * 数组内任意字符在字符串内的起始位置
     *
     * @param str
     * @param searchChars
     * @param start       start index
     * @return
     */
    public static int indexOfAny(String str, char[] searchChars, int start) {
        if (StringUtils.isEmpty(str) || ArrayUtils.isEmpty(searchChars)) {
            return StringUtils.INDEX_NOT_FOUND;
        }
        int csLen = str.length();
        for (int i = start; i <= csLen - 1; i++) {
            char ch = str.charAt(i);
            for (char searchChar : searchChars) {
                if (searchChar == ch) {
                    return i;
                }
            }
        }
        return StringUtils.INDEX_NOT_FOUND;
    }

    /**
     * 数组内任意字符在字符串内的最后位置
     *
     * @param str
     * @param searchChars
     * @return
     */
    public static int lastIndexOfAny(String str, char[] searchChars) {
        if (StringUtils.isEmpty(str) || ArrayUtils.isEmpty(searchChars)) {
            return StringUtils.INDEX_NOT_FOUND;
        }
        int csLen = str.length();
        for (int i = csLen - 1; i >= 0; i--) {
            char ch = str.charAt(i);
            for (char searchChar : searchChars) {
                if (searchChar == ch) {
                    return i;
                }
            }
        }
        return StringUtils.INDEX_NOT_FOUND;
    }


    public static int getParenthesesStart(String eval, int end) {
        Stack<Pair<Character, Integer>> stack = new Stack<>();
        int start = -1;
        int current = 0;
        while (eval.indexOf('(', current) != -1 || eval.indexOf(')', current) != -1) {
            int left = eval.indexOf('(', current);
            int right = eval.indexOf(')', current);
            if (left > -1 && left < right) {
                stack.push(new Pair<Character, Integer>('(', left));
                current = left + 1;
            } else {
                if (right == end) {
                    start = stack.peek().getValue();
                    break;
                } else {
                    stack.pop();
                    current = right + 1;
                }
            }
        }
        return start;
    }


    public static int getParenthesesEnd(String eval, int start) {
        Stack<Pair<Character, Integer>> stack = new Stack<>();
        int end = -1;
        int current = 0;
        while (eval.indexOf('(', current) != -1 || eval.indexOf(')', current) != -1) {
            int left = eval.indexOf('(', current);
            int right = eval.indexOf(')', current);
            if (left > -1 && left < right) {
                stack.push(new Pair<Character, Integer>('(', left));
                current = left + 1;
            } else {
                Pair<Character, Integer> leftObjectPair = stack.pop();
                if (leftObjectPair.getValue().equals(start)) {
                    end = right;
                    break;
                } else {
                    current = right + 1;
                }
            }
        }
        return end;
    }

    public static void main(String[] args) {
        System.out.println(getParenthesesStart("((4+5)+(1+2))", 11));
        System.out.println(getParenthesesEnd("((4+5)+(1+2))", 7));
    }

    static class Pair<K, V> {
        K key;
        V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }
}
