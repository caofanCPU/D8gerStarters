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
import com.xyz.caofancpu.core.CollectionFunUtil;
import com.xyz.caofancpu.core.VerbalExpressionUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;


/**
 * 字符串对齐工具类
 *
 * @author D8GER
 */
@Slf4j
public class StringAlignUtil {

    /**
     * White char regex
     */
    public static final Pattern WHITE_CHAR_PATTERN = Pattern.compile("(?:\\s)+");
    /**
     * Line begins with English comma regex
     */
    public static final Pattern START_WITH_ENGLISH_COMMA_PATTERN = Pattern.compile("^(?:,)+");
    /**
     * Compatibility separator pattern, support one or more lineBreak | English comma as ',' | Chinese comma as '，'
     */
    public static final Pattern ORIGIN_COMPATIBILITY_SEPARATOR = Pattern.compile("((?:\\n|(?:\\r\\n))|(?:,)|(?:，))+");

    /**
     * Handling multi lines by conventional separator
     *
     * @param originText
     * @return
     */
    public static List<String> handleSplitMultiLines(@NonNull String originText) {
        String legalText = originText.replaceAll(ORIGIN_COMPATIBILITY_SEPARATOR.pattern(), SymbolConstantUtil.ENGLISH_COMMA)
                .replaceAll(WHITE_CHAR_PATTERN.pattern(), SymbolConstantUtil.EMPTY)
                .replaceAll(START_WITH_ENGLISH_COMMA_PATTERN.pattern(), SymbolConstantUtil.EMPTY);
        String splitSymbol = SymbolConstantUtil.ENGLISH_COMMA;
        return CollectionFunUtil.splitDelimitedStringToList(legalText, splitSymbol, String::toString);
    }

    /**
     * Format SQL columns, for example, add function or rename by using 'AS'
     *
     * @param originText
     * @param formatAlignment
     * @param prefix
     * @param suffix
     * @param formatSQL
     * @param formatAsCamel
     * @return
     */
    public static String formatSQLColumn(@NonNull String originText, Alignment formatAlignment, @NonNull String prefix, @NonNull String suffix, boolean formatSQL, boolean formatAsCamel) {
        List<String> stringList = handleSplitMultiLines(originText);
        List<String> completeFixList = CollectionFunUtil.transToList(stringList, item -> prefix + item + suffix);
        int singleLineMaxChars = CollectionFunUtil.max(completeFixList, String::length).intValue();
        if (Objects.isNull(formatAlignment)) {
            formatAlignment = Alignment.LEFT;
        }
        List<String> formattedLineList = formatSQLColumn(singleLineMaxChars, formatAlignment, completeFixList);
        if (formatSQL && formatAsCamel) {
            stringList = CollectionFunUtil.transToList(stringList, StringAlignUtil::cleanUnderLineForSQLAliasName);
        }
        Map<Integer, String> indexMap = CollectionFunUtil.transToMap(stringList, stringList::indexOf, Function.identity());
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < formattedLineList.size(); i++) {
            String column = indexMap.get(i);
            result.append(formattedLineList.get(i));
            if (formatSQL) {
                result.append(SymbolConstantUtil.SPACE).append(SymbolConstantUtil.SPACE).append("AS").append(SymbolConstantUtil.SPACE).append(SymbolConstantUtil.SPACE).append(column);
            }
            result.append(SymbolConstantUtil.ENGLISH_COMMA).append(SymbolConstantUtil.NEXT_LINE);
        }
        return StringUtils.isEmpty(result) ? result.toString() : result.deleteCharAt(result.lastIndexOf(SymbolConstantUtil.ENGLISH_COMMA)).toString();
    }

    public static String cleanUnderLineForSQLAliasName(@NonNull String columnName) {
        String result = columnName;
        for (int i = 0; i < 4; i++) {
            if (VerbalExpressionUtil.CAMEL_UNDERLINE_2_NO_UNDERLINE_UNCAPITALIZE.matcher(result).matches()) {
                break;
            }
            result = VerbalExpressionUtil.camelUnderLineNameConverter(result);
        }
        return result;
    }

    /**
     * Format by splitSymbol, such as ',' or NEXT_LINE
     *
     * @param originText
     * @param splitSymbol
     * @param currentAlignment
     * @return
     */
    public static String formatBySplitSymbol(String originText, String splitSymbol, Alignment currentAlignment) {
        List<String> stringList = CollectionFunUtil.splitDelimitedStringToList(originText, splitSymbol, String::toString);
        return formatMultiLine(stringList, currentAlignment);
    }

    /**
     * MultiLine format
     *
     * @param stringList
     * @param currentAlignment
     * @return
     */
    public static String formatMultiLine(List<String> stringList, Alignment currentAlignment) {
        int singleLineMaxChars = CollectionFunUtil.max(stringList, String::length).intValue();
        return format(singleLineMaxChars, currentAlignment, stringList);
    }

    /**
     * Format multi-string
     *
     * @param singleLineMaxChars
     * @param currentAlignment
     * @param stringList
     * @return
     */
    public static String format(int singleLineMaxChars, Alignment currentAlignment, List<String> stringList) {
        checkAlignmentParam(singleLineMaxChars, currentAlignment);
        StringBuilder result = new StringBuilder();
        for (String wanted : stringList) {
            switch (currentAlignment) {
                case RIGHT:
                    pad(result, singleLineMaxChars - wanted.length());
                    result.append(wanted);
                    break;
                case CENTER:
                    int toAdd = singleLineMaxChars - wanted.length();
                    pad(result, toAdd / 2);
                    result.append(wanted);
                    pad(result, toAdd - toAdd / 2);
                    break;
                case LEFT:
                    result.append(wanted);
                    pad(result, singleLineMaxChars - wanted.length());
                    break;
            }
            result.append(SymbolConstantUtil.NEXT_LINE);
        }
        return result.toString();
    }

    /**
     * Format sql columns, return multi-lines
     *
     * @param stringList
     * @param singleLineMaxChars
     * @param currentAlignment
     * @return
     */
    public static List<String> formatSQLColumn(int singleLineMaxChars, Alignment currentAlignment, List<String> stringList) {
        String result = format(singleLineMaxChars, currentAlignment, stringList);
        return CollectionFunUtil.splitDelimitedStringToList(result, SymbolConstantUtil.NEXT_LINE, String::toString);
    }

    /**
     * Supplementary space
     *
     * @param to
     * @param howMany
     */
    public static void pad(StringBuilder to, int howMany) {
        for (int i = 0; i < howMany; i++) {
            to.append(SymbolConstantUtil.SPACE);
        }
    }

    /**
     * Split text, especially is paragraph
     *
     * @param text
     * @param singleLineMaxChars
     * @return
     */
    public static List<String> splitInputText(String text, int singleLineMaxChars) {
        List<String> list = new ArrayList<>();
        if (StringUtils.isBlank(text)) {
            return list;
        }
        if (singleLineMaxChars < 0) {
            throw new IllegalArgumentException("singleLineMaxChars must be positive.");
        }
        for (int i = 0; i < text.length(); i = i + singleLineMaxChars) {
            list.add(text.substring(i, Math.min(i + singleLineMaxChars, text.length())));
        }
        return list;
    }

    /**
     * Basic check
     *
     * @param singleLineMaxChars
     * @param align
     */
    public static void checkAlignmentParam(int singleLineMaxChars, Alignment align) {
        if (singleLineMaxChars < 0) {
            throw new IllegalArgumentException("singleLineMaxChars must be positive.");
        }
        if (align != Alignment.LEFT && align != Alignment.CENTER && align != Alignment.RIGHT) {
            throw new IllegalArgumentException("invalid justification arg.");
        }
    }

    /**
     * Format paragraph
     *
     * @param text
     * @param singleLineMaxChars
     * @param currentAlignment
     * @return
     */
    public String formatText(String text, int singleLineMaxChars, Alignment currentAlignment) {
        List<String> stringList = splitInputText(text, singleLineMaxChars);
        return format(singleLineMaxChars, currentAlignment, stringList);
    }

    public enum Alignment {
        LEFT,
        CENTER,
        RIGHT,
        ;

        public static Alignment fromName(String name) {
            return CollectionFunUtil.findAnyInArrays(Alignment.values(), Alignment::name, item -> item.equalsIgnoreCase(name));
        }
    }
}
