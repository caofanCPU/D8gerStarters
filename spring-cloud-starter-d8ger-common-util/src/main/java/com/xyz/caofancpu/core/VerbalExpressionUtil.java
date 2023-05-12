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

package com.xyz.caofancpu.core;

import com.xyz.caofancpu.constant.SymbolConstantUtil;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import ru.lanwen.verbalregex.VerbalExpression;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则处理工具类
 * VerbalExpression.regex().startOfLine().capt().find("a").oneOrMore().then("X").endCapt().endOfLine().build()  -->  ^((?:a)+(?:X))$
 *
 * Tips: https://github.com/VerbalExpressions/JavaVerbalExpressions
 * 1. DO NOT USE or(), take oneOf() | add(Regex string) place of it
 * 2. USE multi segment capt()+endCapt() for easy reading
 * 3. add() is very powerful, some times it's easy to express OR logic
 *
 * @author D8GER
 */
public class VerbalExpressionUtil {
    /**
     * Judge current system is WINDOWS, by the way, WINDOWS is real ***...
     */
    public static boolean CURRENT_OS_IS_WINDOWS = Objects.equals(System.getProperty("os.name").toLowerCase(), "windows");

    /**
     * Uppercase regular expression
     */
    public static final Pattern HUMP_TO_UNDERLINE = Pattern.compile("[A-Z]");

    /**
     * No '_' or '-' and begin with [A-Z] regex, it will trigger to execute CamelToUnderline when regex detect is true
     */
    public static final Pattern CAMEL_UNDERLINE_1_NO_UNDERLINE_CAPITALIZE = Pattern.compile("^(?![_-])(?:[A-Z])[a-zA-Z0-9\\W]+$");

    /**
     * No '_' or '-' and begin with [a-z] regex, it will trigger to execute CamelToUnderline when regex detect is true
     */
    public static final Pattern CAMEL_UNDERLINE_2_NO_UNDERLINE_UNCAPITALIZE = Pattern.compile("^(?![_-])(?:[a-z])[a-zA-Z0-9\\W]+$");

    /**
     * No upper case regex, it will trigger to execute LowerCaseToUpperCase when regex detect is true
     */
    public static final Pattern CAMEL_UNDERLINE_3_NO_UPPER_CASE = Pattern.compile("^(?![A-Z])[a-z0-9\\W_-]+$");

    /**
     * No lower case, it will trigger to execute UpperCaseToCamel when regex detect is true
     */
    public static final Pattern CAMEL_UNDERLINE_4_NO_LOWER_CASE = Pattern.compile("^(?![a-z])[A-Z0-9\\W_-]+$");

    /**
     * Swagger field | interface position order regular match expression
     */
    public static final Pattern SWAGGER_MODEL_PATTERN = Pattern.compile("(((?:position)|(?:order))(?:\\s)*(?:=)(?:\\s)*(?:\\d)*)");
    /**
     * When we clear white chars, considering spaces can be part of data we should except spaces in JSON string
     */
    public static final Pattern WHITE_CHAR_IN_JSON_REGEX_0 = Pattern.compile("(?:[\\t\\n\\x0B\\f\\r])+");
    /**
     * Beauty JSON view regex
     */
    public static final Pattern WHITE_CHAR_IN_JSON_REGEX_1 = Pattern.compile("(?:\")+[ ]*[:：]+[ ]*");
    /**
     * JSON string definition regex
     */
    public static final Pattern JSON_STRING_JUDGE_REGEX = Pattern.compile("^(?:\\{).*(?:})$");
    /**
     * Common IPV4 find regex
     */
    public static final Pattern IP_PATTERN = Pattern.compile("((\\d){1,3}(\\.)){3}(\\d){1,3}");
    /**
     * Complete IPV4 find regex
     */
    public static final Pattern IPV4_PATTERN = Pattern.compile("^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$");
    /**
     * Complete IPV6 find regex
     */
    public static final Pattern IPV6_PATTERN = Pattern.compile("^([\\da-fA-F]{1,4}:){7}[\\da-fA-F]{1,4}$");
    /**
     * Phone regex
     */
    public static Pattern PHONE_REGEX = Pattern.compile("^1[0-9]{10}$");
    /**
     * Email regex
     */
    public static Pattern EMAIL_REGEX = Pattern.compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
    /**
     * Password validate regex, rule for C4_3 | C4_4
     */
    public static Pattern PWD_REGEX = Pattern.compile("^(?![a-zA-Z]+$)(?![A-Z0-9]+$)(?![A-Z\\W_]+$)(?![a-z0-9]+$)(?![a-z\\W_]+$)(?![0-9\\W_]+$)[a-zA-Z0-9\\W_]{8,30}$");

    /**
     * Java file as source code, which prefix path
     */
    public static Pattern PREFIX_JAVA_SOURCE_FILE_PATH = Pattern.compile("^(?:.*)(?:[/\\\\]*)(?:src)(?:[/\\\\]+)(?:main)(?:[/\\\\]+)(?:java)(?:[/\\\\]+)");

    /**
     * File path split symbol
     */
    public static Pattern FILE_PATH_SPLIT_SYMBOL = Pattern.compile("(?:[/\\\\]+)");

    /**
     * File path prefix split in Windows OS
     */
    public static Pattern WINDOWS_PREFIX_JAVA_SOURCE_FILE_PATH = Pattern.compile("(?:[a-zA-Z]*:\\.*)");

    /**
     * Swagger field | interface position order regular replacement
     *
     * @param originString
     * @param replaceString
     * @return
     */
    public static String regexHandleSwaggerModelProperty(String originString, final String replaceString) {
        Matcher matcher = SWAGGER_MODEL_PATTERN.matcher(originString);
        return matcher.replaceAll(replaceString);
    }

    /**
     * Create a regular expression object
     *
     * @param matchKeyWord
     * @return
     */
    public static VerbalExpression buildRegex(String matchKeyWord) {
        return VerbalExpression.regex().capt().find(matchKeyWord).endCapt().build();
    }

    /**
     * Extract matched content list by pattern
     *
     * @param originContext
     * @param pattern
     * @return
     */
    public static List<String> extractMatchContent(@NonNull String originContext, Pattern pattern) {
        List<String> resultList = new ArrayList<>();
        Matcher matcher = pattern.matcher(originContext);
        while (matcher.find()) {
            // add current matched group value
            resultList.add(matcher.group());
        }
        return resultList;
    }

    /**
     * Camel convert to underline
     * For example:
     * CaoFAn --> cao_f_an --> CAO_F_AN --> CaoFAn
     *
     * @param originName
     * @return
     */
    public static String camelToUnderLineName(String originName) {
        return StringUtils.lowerCase(StringUtils.uncapitalize(originName).replaceAll(HUMP_TO_UNDERLINE.pattern(), "_$0"));
    }


    /**
     * CaoFAn -->(Uncapitalize) caoFAn -->(CamelToUnderline) cao_f_an -->(LowerCaseToUpperCase) CAO_F_AN -->(UpperCaseToCamel) CaoFAn
     *
     * @param originName
     * @return
     */
    public static String camelUnderLineNameConverter(@NonNull String originName) {
        int matchNo = 0;
        if (CAMEL_UNDERLINE_1_NO_UNDERLINE_CAPITALIZE.matcher(originName).matches()) {
            matchNo = 1;
        }
        if (CAMEL_UNDERLINE_2_NO_UNDERLINE_UNCAPITALIZE.matcher(originName).matches()) {
            matchNo = 2;
        }
        if (CAMEL_UNDERLINE_3_NO_UPPER_CASE.matcher(originName).matches()) {
            matchNo = 3;
        }
        if (CAMEL_UNDERLINE_4_NO_LOWER_CASE.matcher(originName).matches()) {
            matchNo = 4;
        }
        if (matchNo == 0) {
            return originName;
        }
        String result = originName;
        switch (matchNo) {
            case 1:
                // Uncapitalize
                result = StringUtils.uncapitalize(originName);
                break;
            case 2:
                // CamelToUnderline
                result = StringUtils.lowerCase(originName.replaceAll(HUMP_TO_UNDERLINE.pattern(), "_$0"));
                break;
            case 3:
                // LowerCaseToUpperCase
                result = StringUtils.upperCase(originName);
                break;
            case 4:
                // UpperCaseToCamel
                String[] words = originName.split("[_-]");
                List<String> resultItemWordList = new ArrayList<>(words.length);
                for (String word : words) {
                    resultItemWordList.add(StringUtils.capitalize(StringUtils.lowerCase(word)));
                }
                result = CollectionFunUtil.join(resultItemWordList, SymbolConstantUtil.EMPTY);
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * url path correction, remove rare '/' to keep just one '/' and begin with it
     * compatible windows file path
     *
     * @param property
     * @return
     */
    public static String correctUrl(String property) {
        String resultPrefix = File.separator;
        try {
            if (CURRENT_OS_IS_WINDOWS) {
                String[] splits = property.split(SymbolConstantUtil.ENGLISH_COLON);
                resultPrefix = splits[0] + SymbolConstantUtil.ENGLISH_COLON;
                if (splits.length == 1 || StringUtils.isBlank(splits[1])) {
                    return resultPrefix + "/";
                }
                property = splits[1];
            } else {
                property = resultPrefix + property;
            }
        } catch (Throwable e) {
            throw new RuntimeException("Illegal file path, please check carefully!");
        }
        VerbalExpression regex = VerbalExpression.regex()
                .capt()
                .find("\\").oneOrMore()
                .or("/").oneOrMore()
                .endCapt()
                .build();
        String tempResult = executePatternRex(regex, property, "/");
        return CURRENT_OS_IS_WINDOWS ? resultPrefix + tempResult : tempResult;
    }

    public static void main(String[] args) {
        VerbalExpression regex1 = VerbalExpression.regex()
                .startOfLine().anything()
                .capt().oneOf("/", "\\\\").zeroOrMore().endCapt()
                .capt().find("src").oneOf("/", "\\\\").oneOrMore().endCapt()
                .capt().find("main").oneOf("/", "\\\\").oneOrMore().endCapt()
                .capt().find("java").oneOf("/", "\\\\").oneOrMore().endCapt()
                .build();


        VerbalExpression regex = VerbalExpression.regex()
                .capt().digit().oneOrMore().endCapture()                           // 3
                .capt().digit().oneOrMore().endCapture()                           // 4
                .capt().range("0", "1").count(1).endCapture()                      // 1 (or 0)
                .capt().find("http://localhost:20").digit().count(3).endCapture()  // http://localhost:20001
                .capt().range("0", "1").count(1).endCapture()                      // again 1 (or 0)
                .capt().digit().oneOrMore().endCapture()                           // 63528800 (lots of digits)
                .capt().range("0", "1").count(1).endCapture()                      // again 1 (or 0)
                .capt().digit().oneOrMore().endCapture()                           // again lots of digit
                .capt().digit().oneOrMore().endCapture()                           // ... and again
                .capt().range("0", "1").count(1).endCapture()                      // 1 (or 0)
                .capt().digit().oneOrMore().endCapture()                           // ... and again
                .capt().find("STR").range("0", "2").count(1).endCapture()          // at last STR1
                .build();
        System.out.println(regex.toString());
        System.out.println(regex1.toString());
        System.out.println(PREFIX_JAVA_SOURCE_FILE_PATH.pattern());
    }

    /**
     * Convert path string to package,
     * for example: /ModuleName//src/main/java/com/xyz/caofancpu/d8ger/test --> com.xyz.caofancpu.d8ger.test
     * Compatible with WINDOWS: D:/ModuleName//src\main\\java/com/xyz/caofancpu/d8ger/test --> com.xyz.caofancpu.d8ger.test
     *
     * @param originPath
     * @return
     */
    public static String convertPathToPackage(String originPath) {
        String first = originPath.replaceAll(PREFIX_JAVA_SOURCE_FILE_PATH.pattern(), SymbolConstantUtil.EMPTY);
        String second = first.replaceAll(FILE_PATH_SPLIT_SYMBOL.pattern(), SymbolConstantUtil.ENGLISH_FULL_STOP);
        if (StringUtils.isBlank(second) || second.length() < 2) {
            throw new RuntimeException("Illegal file path, please check carefully!");
        }
        if (CURRENT_OS_IS_WINDOWS) {
            String winR = second.replaceAll(WINDOWS_PREFIX_JAVA_SOURCE_FILE_PATH.pattern(), SymbolConstantUtil.EMPTY);
            if (StringUtils.isBlank(winR)) {
                throw new RuntimeException("Illegal file path, please check carefully!");
            }
            return winR;
        }
        return Objects.equals(second.charAt(0), '.') ? second.substring(1) : second;
    }

    public static String executePatternRex(VerbalExpression regexExpression, String originText, String replacer) {
        Pattern pattern = Pattern.compile(regexExpression.toString());
        Matcher matcher = pattern.matcher(originText);
        return matcher.replaceAll(replacer);
    }

    /**
     * 美化多个换行符
     * (?:\\n|(?:\\r\\n))+
     *
     * @param source
     * @return
     */
    public static String beautyNextLine(@NonNull String source) {
        VerbalExpression regex = VerbalExpression.regex()
                .lineBreak().oneOrMore()
                .build();
        return executePatternRex(regex, source, SymbolConstantUtil.NEXT_LINE);
    }

    /**
     * 清除空白字符
     * (?:\s)+
     *
     * @param source
     * @return
     */
    public static String cleanWhiteChar(@NonNull String source) {
        VerbalExpression regex = VerbalExpression.regex()
                .space().oneOrMore()
                .build();
        return executePatternRex(regex, source, SymbolConstantUtil.EMPTY);
    }

    /**
     * 清除JSON字符串中的空白字符, 不应包括空格
     *
     * @param source
     * @return
     */
    public static String cleanJSONWhiteChar(@NonNull String source) {
        return source.replaceAll(WHITE_CHAR_IN_JSON_REGEX_0.pattern(), SymbolConstantUtil.EMPTY)
                .replaceAll(WHITE_CHAR_IN_JSON_REGEX_1.pattern(), SymbolConstantUtil.ENGLISH_DOUBLE_QUOTES + SymbolConstantUtil.ENGLISH_COLON);
    }
}
