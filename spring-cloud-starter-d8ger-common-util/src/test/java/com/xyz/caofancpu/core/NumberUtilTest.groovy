package com.xyz.caofancpu.core

import groovy.util.logging.Slf4j
import spock.lang.Specification
import spock.lang.Unroll

/**
 *
 *
 * @author D8GER<caofan.d8ger@bytedance.com>
 * @date 05/12/2023 17:52
 */
@Slf4j
class NumberUtilTest extends Specification {


    // 本测试用例初始化逻辑
    def setup() {
    }

    // 静态类通用逻辑mock
    static class Mock {
        // to be continued if you need
        // for more details, https://bytedance.feishu.cn/wiki/wikcnO3IC3I1stgMeQb1h5hqNZe
    }

    @Unroll
    def "format Thousandths Origin View where number=#number then expect: #expectedResult"() {
        expect:
        NumberUtil.formatThousandthsNormalView(number) == expectedResult

        where:
        number << [0, 0.01,
                   0.00, BigDecimal.valueOf(0.00), new BigDecimal("0.00").setScale(2, BigDecimal.ROUND_HALF_UP),
                   9999999.9999999, new BigDecimal("9999999.99").setScale(8, BigDecimal.ROUND_HALF_UP)]
        expectedResult << ["0", "0.01",
                           "0", "0", "0",
                           "10,000,000", "9,999,999.99"]
    }

    @Unroll
    def "format Thousandths Normal View where number=#number then expect: #expectedResult"() {
        expect:
        NumberUtil.formatOriginView(number) == expectedResult

        where:
        number << [0, 0.01,
                   0.00, BigDecimal.valueOf(0.00), new BigDecimal("0.00").setScale(2, BigDecimal.ROUND_HALF_UP),
                   9999999.9999999, new BigDecimal("9999999.9999990").setScale(8, BigDecimal.ROUND_HALF_UP)]
        expectedResult << ["0", "0.01",
                           "0.00", "0.0", "0.00",
                           "9999999.9999999", "9999999.99999900"]
    }

    @Unroll
    def "format Thousandths Origin View where source=#source then expect: #expectedResult"() {
        expect:
        NumberUtil.formatThousandthsOriginView(source) == expectedResult

        where:
        source << [0 as BigDecimal, 99.0 as BigDecimal, 99999.00010 as BigDecimal]
        expectedResult << ["0", "99", "99,999.0001"]
    }

    @Unroll
    def "formatOriginView #expectedResult"() {
        expect:
        NumberUtil.formatThousandthsView(88888.0123450, customFormat) == expectedResult

        where:
        customFormat << [
                NumberUtil.THOUSANDS_FORMAT_INTEGER_PRECISION,
                NumberUtil.THOUSANDS_FORMAT_ONE_PRECISION,
                NumberUtil.THOUSANDS_FORMAT_NORMAL_PRECISION,
                NumberUtil.THOUSANDS_FORMAT_THREE_PRECISION,
                NumberUtil.THOUSANDS_FORMAT_FOUR_PRECISION,
                NumberUtil.THOUSANDS_FORMAT_FIVE_PRECISION,
                NumberUtil.THOUSANDS_FORMAT_SIX_PRECISION
        ]
        expectedResult << ["88,888", "88,888", "88,888.01", "88,888.012", "88,888.0123", "88,888.01235", "88,888.012345"]
    }

    @Unroll
    def "testWrap"() {
        given:

        expect:
        NumberUtil.wrapStringFieldAmountValue(moneyMock, MoneyMock::getAmountValue, MoneyMock::setAmountValue, 4)
        NumberUtil.wrapFieldAmountValue(moneyMock, MoneyMock::getAmount, MoneyMock::setAmount, 4)
        assert moneyMock.getAmountValue() == "88.7762"
        assert moneyMock.getAmount().toString() == "88.7762"

        where:
        moneyMock << [new MoneyMock().setAmountValue("88.77615").setAmount(new BigDecimal("88.77615"))]
    }

    @Unroll
    def "testGenerateFormat0"() {
        given:

        expect:
        NumberUtil.generateFormat(integerSplitNum, precisionNum) == expectedResult
        where:
        integerSplitNum << [0, 0]
        precisionNum << [0, 1]
        expectedResult << ["#", "#.#"]
    }

    @Unroll
    def "testGenerateFormat1"() {
        given:

        expect:
        NumberUtil.generateFormat(3, precisionNum) == expectedResult
        where:
        precisionNum << [0, 1, 2, 3, 4, 5, 6, 10]
        expectedResult << [",###", ",###.#", ",###.##", ",###.###", ",###.####", ",###.#####", ",###.######", ",###.##########"]
    }

    @Unroll
    def "testGenerateFormat2"() {
        given:

        expect:
        NumberUtil.generateFormat(3, precisionNum, true) == expectedResult
        where:
        precisionNum << [0, 1, 2, 3, 4, 5, 6, 10]
        expectedResult << [",000", ",000.0", ",000.00", ",000.000", ",000.0000", ",000.00000", ",000.000000", ",000.0000000000"]
    }

    @Unroll
    def "testGenerateFormatX"() {
        given:
        double a = 0;
        expect:
        NumberUtil.useCommonDf(NumberUtil.generateFormat(integerSplitNum, precisionNum)).format(a) == expectedResult
        NumberUtil.useCommonDf(NumberUtil.generateFormat(integerSplitNum, precisionNum, true)).format(a) == expectedResult2
        where:
        integerSplitNum << [
                2, 1, 0, 0
        ]
        precisionNum << [
                -1, 0, 1, 2
        ]
        expectedResult << [
                "0", "0", "0", "0"
        ]
        expectedResult2 << [
                "00", "0", "0.0", "0.00"
        ]
    }

    @Unroll
    def "testGenerateFormat3"() {
        given:
        double a = 9999.9876540;
        expect:
        NumberUtil.useCommonDf(NumberUtil.generateFormat(integerSplitNum, precisionNum)).format(a) == expectedResult
        NumberUtil.useCommonDf(NumberUtil.generateFormat(integerSplitNum, precisionNum, true)).format(a) == expectedResult2
        where:
        integerSplitNum << [
                3, 3, 3, 3, 3, 3,
                2, 2, 2, 2, 2, 2,
                0, 0, 0, 0, 0, 3
        ]
        precisionNum << [
                0, 1, 2, 3, 7, -1,
                0, 1, 2, 3, 7, -1,
                0, 1, 2, 3, 7, -1
        ]
        expectedResult << [
                "10,000", "10,000", "9,999.99", "9,999.988", "9,999.987654", "10,000",
                "1,00,00", "1,00,00", "99,99.99", "99,99.988", "99,99.987654", "1,00,00",
                "10000", "10000", "9999.99", "9999.988", "9999.987654", "10,000"
        ]
        expectedResult2 << [
                "10,000", "10,000.0", "9,999.99", "9,999.988", "9,999.9876540", "10,000",
                "1,00,00", "1,00,00.0", "99,99.99", "99,99.988", "99,99.9876540", "1,00,00",
                "10000", "10000.0", "9999.99", "9999.988", "9999.9876540", "10,000"
        ]
    }

    @Unroll
    def "testGenerateFormat4"() {
        given:
        double a = 9999.9876540
        boolean fillWithZero = true
        expect:
        NumberUtil.useCommonDf(NumberUtil.generateFormat(integerSplitNum, precisionNum, fillWithZero)).format(a) == expectedResult
        where:
        integerSplitNum << [0, 0, 3, 2, 6]
        precisionNum << [7, 8, 8, 8, 8]
        expectedResult << ["9999.9876540", "9999.98765400", "9,999.98765400", "99,99.98765400", "009999.98765400"]
    }

    @Unroll
    def "testFormatView0"() {
        given:
        double a = 9999.9876540
        expect:
        NumberUtil.formatView(BigDecimal.valueOf(a), integerSplitNum, precisionNum) == expectedResult
        NumberUtil.formatDoubleView(a, integerSplitNum, precisionNum) == expectedResult
        where:
        integerSplitNum << [0, 0, 0, 0, 0]
        precisionNum << [1, 2, 3, 7, 8]
        expectedResult << ["10000", "9999.99", "9999.988", "9999.987654", "9999.987654"]
    }

    @Unroll
    def "testFormatFillZeroView0"() {
        given:
        double a = 9999.9876540
        expect:
        NumberUtil.formatViewFillWithZero(BigDecimal.valueOf(a), integerSplitNum, precisionNum) == expectedResult
        where:
        integerSplitNum << [0, 0, 0, 0, 0]
        precisionNum << [1, 2, 3, 7, 8]
        expectedResult << ["10000.0", "9999.99", "9999.988", "9999.9876540", "9999.98765400"]
    }

    @Unroll
    def "testFormatFillZeroView1"() {
        given:
        double a = 0
        expect:
        NumberUtil.formatViewFillWithZero(BigDecimal.valueOf(a), integerSplitNum, precisionNum) == expectedResult
        where:
        integerSplitNum << [0, 0, 0, 0, -1, 3]
        precisionNum << [-1, 0, 1, 2, 2, 2]
        expectedResult << ["0", "0", "0.0", "0.00", "0.00", "000.00"]
    }

    @Unroll
    def "testFormatFilledWithZeroView0"() {
        given:
        double a = 0
        expect:
        NumberUtil.formatFilledWithZeroView(BigDecimal.valueOf(a), integerSplitNum, precisionNum) == expectedResult
        where:
        integerSplitNum << [0, 0, 0, 0, -1, 3]
        precisionNum << [-1, 0, 1, 2, 2, 2]
        expectedResult << ["0", "0", "0.0", "0.00", "0.00", "000.00"]
    }

    @Unroll
    def "testFormatNormalFilledWithZeroView0"() {
        given:
        double a = 0
        expect:
        NumberUtil.formatNormalFilledWithZeroView(BigDecimal.valueOf(a)) == expectedResult
        where:
        expectedResult << ["0.00"]
    }

    @Unroll
    def "testFormatNormalFilledWithZeroPercentView0"() {
        given:
        double a = 0
        expect:
        NumberUtil.formatNormalFilledWithZeroPercentView(BigDecimal.valueOf(a)) == expectedResult
        where:
        expectedResult << ["0.00%"]
    }

    @Unroll
    def "testFormatNormalFilledWithZeroPercentView1"() {
        given:
        double a = 86.02
        expect:
        NumberUtil.formatNormalFilledWithZeroPercentView(BigDecimal.valueOf(a)) == expectedResult
        where:
        expectedResult << ["86.02%"]
    }

    @Unroll
    def "testFormatFilledWithZeroPercentView0"() {
        given:
        double a = 0
        expect:
        NumberUtil.formatFilledWithZeroPercentView(BigDecimal.valueOf(a), integerSplitNum, precisionNum) == expectedResult
        where:
        integerSplitNum << [0, 0, 0, 0, -1, 3]
        precisionNum << [-1, 0, 1, 2, 2, 2]
        expectedResult << ["0%", "0%", "0.0%", "0.00%", "0.00%", "000.00%"]
    }

    @Unroll
    def "testFormatFilledWithZeroPercentView1"() {
        given:
        double a = 3333.45670
        expect:
        NumberUtil.formatFilledWithZeroPercentView(BigDecimal.valueOf(a), integerSplitNum, precisionNum) == expectedResult
        where:
        integerSplitNum << [0, -1, 6, 0, 0]
        precisionNum << [0, -1, 3, 6, 2]
        expectedResult << ["3333%", "3333%", "003333.457%", "3333.456700%", "3333.46%"]
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme