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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xyz.caofancpu.constant.SymbolConstantUtil;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 数字运算工具类
 *
 * @author D8GER
 */
@Slf4j
public class NumberUtil {

    /**
     * 汉语中数字大写
     */
    private static final String[] CN_UPPER_NUMBER = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};

    /**
     * 汉语中货币单位大写，设计类似占位符
     */
    private static final String[] CN_UPPER_MONEY_UNIT = {"分", "角", "元", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "兆", "拾", "佰", "仟"};

    /**
     * 特殊字符：整
     */
    private static final String CN_FULL = "整";

    /**
     * 特殊字符：负
     */
    private static final String CN_NEGATIVE = "负";

    /**
     * 数字精度，默认值为2
     */
    private static final int DEFAULT_PRECISION = 2;

    /**
     * 数字精度，虚拟货币默认值为8
     */
    private static final int VIRTUAL_COIN_PRECISION = 8;

    /**
     * 100, 百分数分母值
     */
    private static final int ONE_HUNDRED = 100;

    /**
     * 特殊字符：零元整
     */
    private static final String CN_ZERO_FULL = "零元" + CN_FULL;

    /**
     * 千分位, 保留原始数据精度
     */
    public static final String THOUSANDS_FORMAT_WITH_ORIGIN_PRECISION = generateFormat(3, 10);
    /**
     * 千分位, 整数
     */
    public static final String THOUSANDS_FORMAT_INTEGER_PRECISION = generateFormat(3, 0);
    public static final int NORMAL_PRECISION = 2;
    public static final int FOUR_PRECISION = 4;
    /**
     * 千分位, 固定1位小数
     */
    public static final String THOUSANDS_FORMAT_ONE_PRECISION = generateFormat(3, 1);
    /**
     * 千分位, 固定2位小数
     */
    public static final String THOUSANDS_FORMAT_NORMAL_PRECISION = generateFormat(3, 2);
    /**
     * 千分位, 固定3位小数
     */
    public static final String THOUSANDS_FORMAT_THREE_PRECISION = generateFormat(3, 3);
    /**
     * 千分位, 固定4位小数
     */
    public static final String THOUSANDS_FORMAT_FOUR_PRECISION = generateFormat(3, 4);
    /**
     * 千分位, 固定5位小数
     */
    public static final String THOUSANDS_FORMAT_FIVE_PRECISION = generateFormat(3, 5);
    /**
     * 千分位, 固定6位小数
     */
    public static final String THOUSANDS_FORMAT_SIX_PRECISION = generateFormat(3, 6);

    /**
     * 获取指定位数的随机数
     *
     * @param digit
     */
    public static Integer getRandomInteger(int digit) {
        if (digit >= 32) {
            digit = 31;
        }
        return (int) ((Math.random() * 9 + 1) * Math.pow(10, digit - 1));
    }

    public static String convertToCN(@NonNull BigDecimal numberOfMoney) {
        StringBuilder sb = new StringBuilder();
        int signum = numberOfMoney.signum();
        // 零元整
        if (signum == 0) {
            return CN_ZERO_FULL;
        }
        // 金额的四舍五入
        long number = numberOfMoney.movePointRight(DEFAULT_PRECISION)
                .setScale(0, BigDecimal.ROUND_HALF_UP)
                .abs()
                .longValue();
        // 小数点后两位值
        long scale = number % ONE_HUNDRED;
        int numUnit = 0;
        int numIndex = 0;
        boolean getZero = false;
        // 判断最后两位数，一共四种情况: 00 = 0, 01 = 1, 10, 11
        if (!(scale > 0)) {
            numIndex = 2;
            number = number / 100;
            getZero = true;
        }
        if ((scale > 0) && (!(scale % 10 > 0))) {
            numIndex = 1;
            number = number / 10;
            getZero = true;
        }
        int zeroSize = 0;
        while (number > 0) {
            // 每次获取到最后一个数
            numUnit = (int) (number % 10);
            if (numUnit > 0) {
                if ((numIndex == 9) && (zeroSize >= 3)) {
                    sb.insert(0, CN_UPPER_MONEY_UNIT[6]);
                }
                if ((numIndex == 13) && (zeroSize >= 3)) {
                    sb.insert(0, CN_UPPER_MONEY_UNIT[10]);
                }
                sb.insert(0, CN_UPPER_MONEY_UNIT[numIndex]);
                sb.insert(0, CN_UPPER_NUMBER[numUnit]);
                getZero = false;
                zeroSize = 0;
            } else {
                ++zeroSize;
                if (!getZero) {
                    sb.insert(0, CN_UPPER_NUMBER[numUnit]);
                }
                if (numIndex == 2) {
                    sb.insert(0, CN_UPPER_MONEY_UNIT[numIndex]);
                } else if (((numIndex - 2) % 4 == 0) && (number % 1000 > 0)) {
                    sb.insert(0, CN_UPPER_MONEY_UNIT[numIndex]);
                }
                getZero = true;
            }
            // 让number每次都去掉最后一个数
            number = number / 10;
            ++numIndex;
        }
        // 如果signum == -1，则说明输入的数字为负数，就在最前面追加特殊字符：负
        if (signum == -1) {
            sb.insert(0, CN_NEGATIVE);
        }
        // 输入的数字小数点后两位为"00"的情况，则要在最后追加特殊字符：整
        if (!(scale > 0)) {
            sb.append(CN_FULL);
        }
        return sb.toString();
    }

    /**
     * 将数字字符串转换为中文大写金额
     *
     * @param str
     */
    public static String parseMoneyCN(String str) {
        BigDecimal numberOfMoney = new BigDecimal(str);
        return convertToCN(numberOfMoney);
    }

    /**
     * 虚拟货币对象转BigDecimal, 默认8位小数, 默认采用四舍五入保留2位小数
     *
     * @param price
     */
    public static BigDecimal convertVirtualCoinPrice(@NonNull Object price) {
        return convertToBigDecimal(price, VIRTUAL_COIN_PRECISION, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 对象转BigDecimal, 默认采用四舍五入保留2位小数
     *
     * @param source
     * @param newScale
     */
    public static BigDecimal convertToBigDecimal(@NonNull Object source, int newScale) {
        if (newScale <= 0) {
            newScale = DEFAULT_PRECISION;
        }
        return convertToBigDecimal(source, newScale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 对象转BigDecimal, 默认采用四舍五入保留2位小数
     *
     * @param source
     * @param newScale
     * @param roundingMode
     */
    public static BigDecimal convertToBigDecimal(@NonNull Object source, int newScale, int roundingMode) {
        if (newScale <= 0) {
            newScale = DEFAULT_PRECISION;
        }
        if (roundingMode <= 0) {
            roundingMode = BigDecimal.ROUND_HALF_UP;
        }
        return new BigDecimal(source.toString()).setScale(newScale, roundingMode);
    }

    /**
     * 对象转BigDecimal, 默认采用四舍五入保留2位小数
     *
     * @param source
     */
    public static BigDecimal convertToDefaultBigDecimal(@NonNull Object source) {
        return convertToBigDecimal(source, DEFAULT_PRECISION, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 计算百分比(2位精度), 返回字符串 xx.00%
     *
     * @param value
     * @param referValue
     */
    public static String calculateViewPercent(@NonNull Long value, @NonNull Long referValue) {
        BigDecimal x = BigDecimal.valueOf(value);
        BigDecimal y = BigDecimal.valueOf(referValue);
        BigDecimal oneHundred = BigDecimal.valueOf(ONE_HUNDRED);
        return x.divide(y, DEFAULT_PRECISION, BigDecimal.ROUND_HALF_UP).multiply(oneHundred).toString() + SymbolConstantUtil.PERCENT;
    }


    /**
     * 计算百分数, 返回结果为Map<元素索引, 百分数值>
     * 示例: [5, 10, 15]
     * precision=0时: Map.Entry[<0, 17>, <1, 33>, <2, 50>], => 元素5(在原始数值列表的索引为0)的百分占比为17%, 元素10的百分占比为33%, 元素15的百分占比为50%
     * precision=1时: Map.Entry[<0, 16.7>, <1, 33.3>, <2, 50.0>]
     * precision=2时: Map.Entry[<0, 16.67>, <1, 3.33>, <2, 50.00>]
     *
     * @param source    原始数值列表
     * @param precision 百分数小数的位数, 0相当于求x%; 1相当于求x.y%; 2相当于求x.yy%
     * @param <T>
     */
    public static <T extends Number> Map<Integer, BigDecimal> calculateDefaultPercentage(List<T> source, int precision) {
        return calculateDistributionValueByPercentage(source, precision, BigDecimal.valueOf(ONE_HUNDRED));
    }

    /**
     * 根据参考值referValue及数字元素所占百分比, 计算 referValue * 百分比的结果
     * 示例: 商品价格(单位:元)列表[5, 10, 15], 达到了满30元减10元的条件, 那么利用本方法求得每个商品的分摊优惠价
     * precision=0时: Map.Entry[<0, 2>, <1, 3>, <2, 5>]
     * precision=1时: Map.Entry[<0, 1.7>, <1, 3.3>, <2, 5.0>]
     * precision=2时: Map.Entry[<0, 1.67>, <1, 3.33>, <2, 5.00>]
     * <p>
     * 注意: 由于价格用元表示, 与用分表示, 数值相差100倍, 那么如果用元计算时需要2位精度, 等价于用分计算时保留0位精度, 同上例用分表示结果如下:
     * 商品价格(单位:分)列表[500, 1000, 1500], 达到了满3000分减1000分的条件, 那么利用本方法求得每个商品的分摊优惠价
     * precision=0时: Map.Entry[<0, 167>, <1, 333>, <2, 500>], 该结果与用元表示时结果一致
     *
     * 计算列表中各数字元素所占的百分比, 在四舍五入的情况下保证百分比和为1, 且尽可能保证结果的方差最小
     * 示例: [3, 4, 5] ==> 占比数值     [0.25000000, 0.33333333, 0.41666667]
     * 占比数值 * 100 ==> 转换为百分数   [25.000000, 33.333333, 41.666667]
     * 百分数向下取整  ==>              [25, 33, 41]
     * 每个百分比的偏差值 ==>            [0, 0.333333, 0.666667]
     * 偏差值逆序排列    ==>            [0.666667, 0.333333, 0]
     * 因向下取整得到的百分比差值总和      100 - (25+33+41) = 1
     * 将总偏差1分配下去, 每次分配1, 正好分配给最大偏差值0.666667对应的41: 41+1=42, 完成分配
     * (将总偏差值以1为单位, 依次分配给最大的, 第二大的, 直到分配完为止)
     * 得到结果: [25, 33, 42]
     * <p>
     * 精度推算的原理, 利用0.xxYY = 百分之xx.YY = 万分之xxYY
     * 算法原理参考: https://revs.runtime-revolution.com/getting-100-with-rounded-percentages-273ffa70252b
     *
     * @param source
     * @param precision  百分位的精度, 0代表x%, 1代表x.y%, 2代表x.yy%, 依次类推
     * @param referValue 参考基数, 默认参考值为1 ==> 计算结果为百分比数值
     * @return Map<数字元素索引, 结果值>
     */
    public static <T extends Number> Map<Integer, BigDecimal> calculateDistributionValueByPercentage(List<T> source, int precision, BigDecimal referValue) {
        if (CollectionFunUtil.isEmpty(source)) {
            return Maps.newHashMap();
        }
        if (precision < 0) {
            precision = 0;
        }
        if (Objects.isNull(referValue)) {
            referValue = BigDecimal.ONE;
        }

        // 根据精度要求需要扩大的倍数
        BigDecimal timesValue = BigDecimal.valueOf(Math.pow(10, precision));
        BigDecimal originSum = CollectionFunUtil.sum(source, Number::doubleValue);
        Map<Integer, BigDecimal> percentageMap = Maps.newHashMap();
        for (int i = 0; i < source.size(); i++) {
            T item = source.get(i);
            BigDecimal value = BigDecimal.valueOf(item.doubleValue())
                    .multiply(timesValue)
                    .multiply(referValue)
                    .divide(originSum, 8, BigDecimal.ROUND_HALF_UP);
            percentageMap.put(i, value);
        }
        // 所有占比结果向下取整
        Map<Integer, BigDecimal> downPercentageMap = Maps.newHashMap();
        percentageMap.forEach((index, percentage) -> downPercentageMap.put(index, percentage.setScale(0, BigDecimal.ROUND_DOWN)));
        int downPercentageSum = CollectionFunUtil.sum(downPercentageMap.values(), BigDecimal::doubleValue).intValue();
        // 占比因向下取整得到的偏差
        int deltaPercentageSum = referValue.multiply(timesValue).intValue() - downPercentageSum;
        Map<Integer, BigDecimal> deltaPercentageMap = Maps.newHashMap();
        percentageMap.forEach((index, percentage) -> deltaPercentageMap.put(index, percentage.subtract(downPercentageMap.get(index))));
        // 按照偏差由大到小排序
        LinkedHashMap<Integer, BigDecimal> deltaPercentageSortedByValueMap = CollectionFunUtil.sortByValue(deltaPercentageMap, true);
        Map<Integer, BigDecimal> resultMap = Maps.newLinkedHashMap();
        for (Integer index : deltaPercentageSortedByValueMap.keySet()) {
            if (deltaPercentageSum > 0) {
                downPercentageMap.put(index, downPercentageMap.get(index).add(BigDecimal.ONE));
                deltaPercentageSum--;
            }
            resultMap.put(index, downPercentageMap.get(index).divide(timesValue, precision, BigDecimal.ROUND_DOWN));
        }

        return resultMap;
    }

    /**
     * 将价格由分转为元, 得到字符串结果
     * 示例: 123456L ==> 1234.56元
     *
     * @param price
     */
    public static String convertViewPriceFromFenToYuan(@NonNull Long price) {
        return convertPriceFromFenToYuan(price).toString() + CN_UPPER_MONEY_UNIT[2];
    }

    /**
     * 将分转为元, 得到BigDecimal结果
     *
     * @param price
     * @return
     */
    public static BigDecimal convertPriceFromFenToYuan(@NonNull Long price) {
        BigDecimal value = BigDecimal.valueOf(price);
        BigDecimal oneHundred = BigDecimal.valueOf(ONE_HUNDRED);
        return value.divide(oneHundred, DEFAULT_PRECISION, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * value
     * 计算两个数的相对占比百分数, 返回 ------------ 的百分比表示, 默认保留百分位的2位小数
     * referValue
     * 示例: (5, 15, 4) ==> 33.3333%
     * (15, 5, 4) ==> 300.0000%
     *
     * @param value      分子
     * @param referValue 分母
     * @param precision  百分位小数的精度
     */
    public static <T extends Number> String calculateViewPercent(@NonNull T value, @NonNull T referValue, int precision) {
        if (precision < 0) {
            precision = DEFAULT_PRECISION;
        }
        BigDecimal x = BigDecimal.valueOf(value.doubleValue());
        BigDecimal y = BigDecimal.valueOf(referValue.doubleValue());
        BigDecimal oneHundred = BigDecimal.valueOf(ONE_HUNDRED);
        return x.multiply(oneHundred).divide(y, precision, BigDecimal.ROUND_HALF_UP).toString() + SymbolConstantUtil.PERCENT;
    }

    /**
     * 对于列表指定最少选择数, 最多选择数, 返回可以选的组合情况 + 不可以选的组合情况
     *
     * @param source
     * @param minSelect
     * @param maxSelect
     * @param <T>
     */
    public static <T> Map<Boolean, List<List<T>>> calculateAndGroupCombNChooseK(@NonNull List<T> source, int minSelect, int maxSelect) {
        if (!(minSelect >= 0 && minSelect <= maxSelect && maxSelect <= source.size())) {
            throw new IllegalArgumentException("Please check your params, Rule: 0 <= minSelect <= maxSelect <= source.size()");
        }
        List<Integer> totalSelectNumList = IntStream.rangeClosed(0, source.size()).boxed().collect(Collectors.toList());
        List<Integer> okSelectNumList = IntStream.rangeClosed(minSelect, maxSelect).boxed().collect(Collectors.toList());
        List<Integer> errorSelectNumList = CollectionFunUtil.subtract(ArrayList::new, totalSelectNumList, okSelectNumList);

        Map<Boolean, List<List<T>>> resultMap = new HashMap<>(4, 0.5f);
        resultMap.put(Boolean.TRUE, calculateCombNChooseK(source, okSelectNumList));
        resultMap.put(Boolean.FALSE, calculateCombNChooseK(source, errorSelectNumList));
        return resultMap;
    }

    /**
     * 从N个元素中, 选出k=0, 1, x, y, n多种情况的组合结果
     *
     * @param source
     * @param kList
     * @param <T>
     */
    public static <T> List<List<T>> calculateCombNChooseK(@NonNull List<T> source, @NonNull List<Integer> kList) {
        List<List<T>> resultList = Lists.newArrayList();
        kList.forEach(k -> resultList.addAll(calculateCombNChooseK(source, k)));
        return resultList;
    }

    /**
     * 针对至少有2个元素的列表source, 计算任意k(k>=0)元素的关系组合情况
     *
     * @param source
     * @param k
     * @param <T>
     */
    public static <T> List<List<T>> calculateCombNChooseK(@NonNull List<T> source, int k) {
        List<List<T>> resultList = Lists.newArrayList();
        int[] array = new int[source.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        int[][] combNChooseKResult = calculateCombNChooseK(array, k);
        for (int[] ints : combNChooseKResult) {
            List<T> itemResult = Lists.newArrayList();
            for (int index : ints) {
                itemResult.add(source.get(index));
            }
            resultList.add(itemResult);
        }
        return resultList;
    }

    /**
     * 针对至少有2个元素的列表source, 计算任意两元素的关系组合情况
     *
     * @param source
     */
    public static <T> List<Pair<T, T>> calculateCombNChooseTwo(@NonNull List<T> source) {
        if (CollectionFunUtil.isEmpty(source) || source.size() < 2) {
            throw new RuntimeException("组合数计算至少需要两个元素");
        }
        List<Pair<T, T>> resultList = Lists.newArrayList();
        int k = 2;
        int[] array = new int[source.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        int[][] combNChooseKResult = calculateCombNChooseK(array, k);
        for (int[] ints : combNChooseKResult) {
            T left = source.get(ints[0]);
            T right = source.get(ints[1]);
            resultList.add(Pair.of(left, right));
        }
        return resultList;
    }

    /**
     * 从数组array中选取任意k个元素, 计算所有组合情况
     * 原理参考: https://blog.csdn.net/haiyoushui123456/article/details/84338494
     *
     * @param array
     * @param k
     */
    private static int[][] calculateCombNChooseK(int[] array, int k) {
        int n = array.length;
        checkNK(n, k);
        if (k == 0) {
            return new int[1][0];
        }
        int combNum = calculateNChooseK(n, Math.min(k, (n - k)));
        int[][] comb = new int[combNum][k];
        int rowEndIndex = n - k + 1;
        for (int i = 0, k1 = k - 1; i < rowEndIndex; i++) {
            // Fill the right-most side.
            comb[i][k1] = array[k1 + i];
        }
        for (int begin = k - 2; begin >= 0; begin--) {
            int rowLen = rowEndIndex;
            int previousRowEndIndex = rowEndIndex;
            for (int i = 0; i < rowEndIndex; i++) {
                comb[i][begin] = array[begin];
            }
            for (int next = begin + 1, limit = begin + n - k; next <= limit; next++) {
                int selectionNum = n - k + 1 + begin - next;
                int allPossibleNum = n - next;
                rowLen = rowLen * selectionNum / allPossibleNum;
                int rowBeginIndex = rowEndIndex;
                rowEndIndex = rowBeginIndex + rowLen;
                int nextVal = array[next];
                for (int i = rowBeginIndex; i < rowEndIndex; i++) {
                    comb[i][begin] = nextVal;
                    for (int j = begin + 1; j < k; j++) {
                        comb[i][j] = comb[previousRowEndIndex - rowLen + i - rowBeginIndex][j];
                    }
                }
            }
        }
        return comb;
    }

    private static void checkNK(int n, int k) {
        // N must be a positive integer.
        if (k < 0 || k > n) {
            throw new IllegalArgumentException("K must be an integer between 0 and N.");
        }
    }

    private static int calculateNChooseK(int n, int k) {
        if (n > 31) {
            throw new IllegalArgumentException("N must be less than or equal to 31");
        }
        checkNK(n, k);
        k = Math.min(k, (n - k));
        if (k <= 1) {
            // C(n, 0) = 1, C(n, 1) = n
            return k == 0 ? 1 : n;
        }
        int limit = Integer.MAX_VALUE >> (31 - n);
        int cnk = 0;
        for (int i = 3; i < limit; i++) {
            if (Integer.bitCount(i) == k) {
                cnk++;
            }
        }
        return cnk;
    }


    /**
     * 常规千分位固定两位展示, 去除无效小数0
     *
     * @param number
     */
    public static String formatThousandthsNormalView(BigDecimal number) {
        return formatView(number, useCommonDf(THOUSANDS_FORMAT_NORMAL_PRECISION));
    }

    /**
     * 常规千分位固定两位百分比展示, 保留精度0
     *
     * @param number
     */
    public static String formatNormalFilledWithZeroPercentView(BigDecimal number) {
        return formatFilledWithZeroPercentView(number, 0, 2);
    }

    /**
     * 常规千分位百分比, 保留精度0
     *
     * @param number
     */
    public static String formatFilledWithZeroPercentView(
        BigDecimal number,
        int integerSplitNum, int precisionNum) {
        return formatFilledWithZeroView(number, integerSplitNum, precisionNum) + SymbolConstantUtil.PERCENT;
    }

    /**
     * 常规千分位固定两位展示, 保留精度0
     *
     * @param number
     */
    public static String formatNormalFilledWithZeroView(BigDecimal number) {
        return formatFilledWithZeroView(number, 0, 2);
    }

    /**
     * 常规千分位, 保留精度0
     *
     * @param number
     */
    public static String formatFilledWithZeroView(BigDecimal number, int integerSplitNum, int precisionNum) {
        return formatViewFillWithZero(number, integerSplitNum, precisionNum);
    }

    /**
     * 金额精度展示处理, 去除无效小数0
     *
     * @param source
     * @param decimalFormat
     */
    public static String formatView(BigDecimal source, @NonNull DecimalFormat decimalFormat) {
        if (Objects.isNull(source) || BigDecimal.ZERO.compareTo(source) == 0) {
            return BigDecimal.ZERO.toPlainString();
        }
        return decimalFormat.format(source);
    }

    /**
     * 金额精度展示处理, 去除无效小数0
     *
     * @param source
     * @param integerSplitNum
     * @param precisionNum
     */
    public static String formatView(BigDecimal source, int integerSplitNum, int precisionNum) {
        return formatView(source, new DecimalFormat(generateFormat(integerSplitNum, precisionNum)));
    }

    /**
     * 金额精度展示处理, 支持指定精度0
     *
     * @param source
     * @param integerSplitNum
     * @param precisionNum
     */
    public static String formatViewFillWithZero(BigDecimal source, int integerSplitNum, int precisionNum) {
        if (Objects.isNull(source)) {
            source = BigDecimal.ZERO;
        }
        DecimalFormat decimalFormat = new DecimalFormat(generateFormat(integerSplitNum, precisionNum, true));
        return decimalFormat.format(source);
    }

    /**
     * 金额精度展示处理, 去除无效小数0
     *
     * @param source
     * @param integerSplitNum
     * @param precisionNum
     */
    @Deprecated
    public static String formatDoubleView(Double source, int integerSplitNum, int precisionNum) {
        if (Objects.isNull(source)) {
            source = 0D;
        }
        DecimalFormat df = new DecimalFormat(generateFormat(integerSplitNum, precisionNum));
        return df.format(source);
    }

    /**
     * 千分位原始精度展示, 保持原有精度, 0.00
     *
     * @param number
     */
    public static String formatOriginView(BigDecimal number) {
        if (Objects.isNull(number)) {
            return BigDecimal.ZERO.toPlainString();
        }
        return number.toPlainString();
    }

    /**
     * 指定千分位格式, 去除无效小数0
     *
     * @param number
     * @param format
     */
    public static String formatThousandthsView(BigDecimal number, String format) {
        DecimalFormat customDf = useCommonDf(format);
        return formatView(number, customDf);
    }

    /**
     * 千分位原始精度展示, 去除无效小数0, 最大限度保持原有精度
     *
     * @param number
     */
    public static String formatThousandthsOriginView(BigDecimal number) {
        if (Objects.isNull(number)) {
            number = BigDecimal.ZERO;
        }
        return useCommonDf(THOUSANDS_FORMAT_WITH_ORIGIN_PRECISION).format(number);
    }

    /**
     * 使用四舍五入的DecimalFormat对象
     *
     * @param format
     */
    public static DecimalFormat useCommonDf(String format) {
        DecimalFormat df = new DecimalFormat(format);
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df;
    }

    /**
     * 格式化
     *
     * @param e
     * @param getter
     * @param setter
     * @param <E>
     */
    public static <E> void wrapStringFieldAmountValue(E e, Function<E, String> getter, BiConsumer<E, String> setter, int precision) {
        if (precision < 0) {
            precision = NORMAL_PRECISION;
        }
        if (Objects.nonNull(e) && Objects.nonNull(getter.apply(e))) {
            String result = new BigDecimal(getter.apply(e))
                .setScale(precision, RoundingMode.HALF_UP)
                .toPlainString();
            setter.accept(e, result);
        }
    }

    public static <E> void wrapFieldAmountValue(E e, Function<E, BigDecimal> getter, BiConsumer<E, BigDecimal> setter, int precision) {
        if (precision < 0) {
            precision = NORMAL_PRECISION;
        }
        if (Objects.nonNull(e) && Objects.nonNull(getter.apply(e))) {
            BigDecimal result = new BigDecimal(getter.apply(e)
                .setScale(precision, RoundingMode.HALF_UP)
                .toPlainString());
            setter.accept(e, result);
        }
    }

    /**
     * 计算数据的格式化表达式
     *
     * @param integerSplitNum 整数部分分隔位数, 如果为0代表整数部分不进行分割, 维持原数
     * @param precisionNum    小数部分精度位数, 如果为0代表取整, 可能会进位, eg: 99.9->100
     * @param fillWithZero    不足部分是否用0填充
     */
    public static String generateFormat(int integerSplitNum, int precisionNum, boolean fillWithZero) {
        if (integerSplitNum <= 0) {
            integerSplitNum = 0;
        }
        if (precisionNum <= 0) {
            precisionNum = 0;
        }
        String symbol = fillWithZero ? BigInteger.ZERO.toString() : SymbolConstantUtil.HASHTAG;
        String integerPartFormat;
        if (integerSplitNum == 0) {
            // 0代表整数不分割, 那就留一个符号
            integerPartFormat = symbol;
        } else {
            integerPartFormat = SymbolConstantUtil.ENGLISH_COMMA
                + CollectionFunUtil.join(Collections.nCopies(integerSplitNum, symbol), SymbolConstantUtil.EMPTY);
        }

        String precisionPartFormat;
        if (precisionNum == 0) {
            precisionPartFormat = SymbolConstantUtil.EMPTY;
        } else {
            precisionPartFormat = SymbolConstantUtil.ENGLISH_FULL_STOP
                + CollectionFunUtil.join(Collections.nCopies(precisionNum, symbol), SymbolConstantUtil.EMPTY);
        }
        String result = integerPartFormat + precisionPartFormat;
        if (log.isDebugEnabled()) {
            log.info("generateFormat result: {}", Objects.equals(result, SymbolConstantUtil.EMPTY) ? "EMPTY" : result);
        }
        return StringUtils.isNotBlank(result) ? result : symbol;
    }

    /**
     * 计算数据的格式化表达式, 默认数据填充为常规格式, 省去无效零
     *
     * @param integerSplitNum 整数部分分隔位数
     * @param precisionNum    小数部分精度位数
     */
    public static String generateFormat(int integerSplitNum, int precisionNum) {
        return generateFormat(integerSplitNum, precisionNum, false);
    }

}
