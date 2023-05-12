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
import com.xyz.caofancpu.core.CollectionFunUtil;
import com.xyz.caofancpu.core.NumberUtil;
import junit.framework.TestCase;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 常用数字运算工具类测试用例
 */
public class OriginNumberUtilTest extends TestCase {

    /**
     * 计算优惠价格分配值, 以[价格单位:分, 小数精度位数为0]为例
     * (分转换为元时2位小数)
     * 两个商品价格分别为 10000分(100元), 14000分(140元), 优惠1000分(10元)
     *
     * @throws Exception
     */
    public void testCalculateDistributionValueByPercentage()
            throws Exception {
        List<Long> priceFenList = Lists.newArrayList(200L, 200L, 200L, 100L);
        BigDecimal reductionPriceFen = BigDecimal.valueOf(99L);
        Map<Integer, BigDecimal> resultFenMap = NumberUtil.calculateDistributionValueByPercentage(priceFenList, 2, reductionPriceFen);
        resultFenMap.forEach((index, value) -> System.out.println("价格[" + priceFenList.get(index) + "分]对应的优惠价为: " + value + "分"));

        System.out.print("\n------换算为元, 对比结果-------\n");

        // 下面将分首先转换为元, 用来结果对比
        List<BigDecimal> priceYuanList = CollectionFunUtil.transToList(priceFenList, NumberUtil::convertPriceFromFenToYuan);
        BigDecimal reductionPriceYuan = reductionPriceFen.divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
        Map<Integer, BigDecimal> resultYuanMap = NumberUtil.calculateDistributionValueByPercentage(priceYuanList, 2, reductionPriceYuan);
        resultYuanMap.forEach((index, value) -> System.out.println("价格[" + priceYuanList.get(index) + "元]对应的优惠价为: " + value + "元"));

        System.out.print("\n------百分占比结果-------\n");
        Map<Integer, BigDecimal> percentageMap = NumberUtil.calculateDefaultPercentage(priceFenList, 0);
        percentageMap.forEach((index, value) -> System.out.println("价格[" + priceFenList.get(index) + "分]对应的百分占比为: " + value));
        percentageMap = NumberUtil.calculateDefaultPercentage(priceFenList, 1);
        percentageMap.forEach((index, value) -> System.out.println("价格[" + priceFenList.get(index) + "分]对应的百分占比为: " + value));
        percentageMap = NumberUtil.calculateDefaultPercentage(priceFenList, 2);
        percentageMap.forEach((index, value) -> System.out.println("价格[" + priceFenList.get(index) + "分]对应的百分占比为: " + value));
        percentageMap = NumberUtil.calculateDefaultPercentage(priceYuanList, 0);
        percentageMap.forEach((index, value) -> System.out.println("价格[" + priceYuanList.get(index) + "元]对应的百分占比为: " + value));
        percentageMap = NumberUtil.calculateDefaultPercentage(priceYuanList, 1);
        percentageMap.forEach((index, value) -> System.out.println("价格[" + priceYuanList.get(index) + "元]对应的百分占比为: " + value));
        percentageMap = NumberUtil.calculateDefaultPercentage(priceYuanList, 2);
        percentageMap.forEach((index, value) -> System.out.println("价格[" + priceYuanList.get(index) + "元]对应的百分占比为: " + value));

    }

}