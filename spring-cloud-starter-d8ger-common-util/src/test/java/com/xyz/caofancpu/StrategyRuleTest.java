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

package com.xyz.caofancpu;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.xyz.caofancpu.algorithm.graph.DirectedGraph;
import com.xyz.caofancpu.algorithm.graph.TarjanSSC;
import com.xyz.caofancpu.constant.SymbolConstantUtil;
import com.xyz.caofancpu.core.CollectionUtil;
import com.xyz.caofancpu.extra.NormalUseForTestUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * 策略规则测试
 *
 * @author D8GER
 */
@RunWith(JUnit4.class)
public class StrategyRuleTest {

    private static List<List<String>> loadChooseNameList() {
        List<String> choose1 = Lists.newArrayList(loadRequiredNameList());
        List<String> choose2 = Lists.newArrayList(loadRequiredNameList());
        choose2.addAll(Lists.newArrayList("B"));
        List<String> choose3 = Lists.newArrayList(loadRequiredNameList());
        choose3.addAll(Lists.newArrayList("B", "C"));
        List<String> choose4 = Lists.newArrayList(loadRequiredNameList());
        choose4.addAll(Lists.newArrayList("B", "C", "D", "E"));
        List<String> choose5 = Lists.newArrayList(loadRequiredNameList());
        choose5.addAll(Lists.newArrayList("F"));

        List<String> choose6 = Lists.newArrayList(loadRequiredNameList());
        choose6.addAll(Lists.newArrayList("G", "H"));
        List<String> choose7 = Lists.newArrayList(loadRequiredNameList());
        choose7.addAll(Lists.newArrayList("B", "C", "G", "H", "F"));

        List<List<String>> testList = new ArrayList<>();
        testList.add(choose1);
        testList.add(choose2);
        testList.add(choose3);
        testList.add(choose4);
        testList.add(choose5);
        testList.add(choose6);
        testList.add(choose7);
        return testList;
    }

    private static Map<Integer, Strategy> loadStrategy() {
        Map<Integer, Strategy> strategyMap = Maps.newHashMap();
        StrategyRuleTest.Strategy strategy1 = new StrategyRuleTest.Strategy();
        List<String> configNameList1 = Lists.newArrayList(loadRequiredNameList());
        configNameList1.addAll(Lists.newArrayList("B", "C", "D", "E"));
        strategy1.setConfigNameList(configNameList1).setMin(3).setMax(4);
        strategyMap.put(1, strategy1);

        StrategyRuleTest.Strategy strategy2 = new StrategyRuleTest.Strategy();
        List<String> configNameList2 = Lists.newArrayList(loadRequiredNameList());
        configNameList2.addAll(Lists.newArrayList("G", "H"));
        strategy2.setConfigNameList(configNameList2).setMin(1).setMax(2);
        strategyMap.put(2, strategy2);

        return strategyMap;
    }

    private static List<String> loadAllNameList() {
        return Lists.newArrayList("A₁", "B", "C", "D", "E", "F", "G", "H");
    }

    private static List<String> loadRequiredNameList() {
        return Lists.newArrayList("A₁");
    }

    /**
     * 计算最少最多选择关系
     * 思路:
     * Required:[A₁]
     * Strategy:[A₁,B,C,D,E], min=3, max=4
     * RealStrategy=Strategy-Required=[B,C,D,E]
     * RealMin=min-Required.size=2
     * RealMax=max-Required.size=3
     * if, Choose:[A₁,B,C]
     * - RealChoose=Choose-Required=[B,C]
     * - JudgeChoose=RealChoose ∩ RealStrategy=[B,C], 非空==>匹配当前规则, 需要用当前策略进行约束校验
     * - JudgeChoose=[B,C], RealStrategy=[B,C,D,E], RealMin=2, RealMax=3
     * - ∵ RealMin <= JudgeChoose.size <= RealMax
     * - ∴ 符合该规则, 继续进行其他规则的匹配与约束校验
     * <p>
     * if, Choose:[A₁,B,C,D,E]
     * - RealChoose=Choose-Required=[B,C,D,E]
     * - JudgeChoose=RealChoose ∩ RealStrategy=[B,C,D,E], 非空==>匹配当前规则, 需要用当前策略进行约束校验
     * - JudgeChoose=[B,C,D,E], RealStrategy=[B,C,D,E], RealMin=2, RealMax=3
     * - ∵ JudgeChoose.size > RealMax
     * - ∴ 不符合该规则, 判定失败: "[B,C,D,E]最多选RealMax个"
     * <p>
     * if, Choose:[A₁]
     * - RealChoose=Choose-Required=[]
     * - JudgeChoose=RealChoose ∩ RealStrategy=[], 为空==>不匹配当前规则, continue
     * <p>
     * if, Choose:[A₁, B]
     * - RealChoose=Choose-Required=[B]
     * - JudgeChoose=RealChoose ∩ RealStrategy=[B], 非空==>匹配当前规则, continue
     * - JudgeChoose=[B], RealStrategy=[B,C,D,E], RealMin=2, RealMax=3
     * - ∵ JudgeChoose.size < RealMin
     * - ∴ 不符合该规则, 判定失败: "[B,C,D,E]最少选RealMin个"
     * 示例:
     * 商品列表: [A, B, C, D, E, F, G, H], 必选[A]
     * 配置策略:
     * 1. [A, B],      min=1, max=2
     * 2. [A, C, D]    min=1, max=2
     * 3. [A, E, F, G] min=1, max=2
     * 选择商品(×代表不符合规则, √代表满足规则, ≠代表和规则无关):
     * []              ×, "选择商品不能为空"
     * [A]             √, √规则1, √规则2, √规则3
     * [A,B]           √, √规则1, ≠规则2, ≠规则3
     * [A,C]           √, ≠规则1, √规则2, ≠规则3
     * [A,D]           √, ≠规则1, √规则2, ≠规则3
     * [A,B,C]         √, √规则1, √规则2, ≠规则3
     * [A,B,D]         √, √规则1, √规则2, ≠规则3
     * [A,C,D]         ×, ≠规则1, ×规则2, ≠规则3, "商品C, 商品D, 最多选1个"
     * [A,G]           √, ≠规则1, ≠规则2, √规则3
     * [A,E,G]         ×, ≠规则1, ≠规则2, ×规则3, "商品E, 商品F, 商品G最多选1个"
     * [A,C,E,F]       ×, ≠规则1, √规则2, ×规则3, "商品E, 商品F, 商品G最多选1个"
     */
    @Test
    public void minAndMaxSelectTest() {
        List<String> requiredNameList = loadRequiredNameList();
        Map<Integer, Strategy> strategyMap = loadStrategy();
        loadChooseNameList().forEach(list -> {
            List<String> realChoose = CollectionUtil.subtract(ArrayList::new, list, requiredNameList);
            strategyMap.forEach((id, strategy) -> {
                List<String> configNameList = strategy.getConfigNameList();
                List<String> realStrategy = CollectionUtil.subtract(ArrayList::new, configNameList, requiredNameList);
                List<String> intersection = CollectionUtil.intersection(ArrayList::new, realChoose, realStrategy);
                if (CollectionUtil.isEmpty(intersection)) {
                    // 为空, 跳过
                    NormalUseForTestUtil.out("实选[" + CollectionUtil.join(realChoose, SymbolConstantUtil.ENGLISH_COMMA) + "]与策略[" + CollectionUtil.join(realStrategy, SymbolConstantUtil.ENGLISH_COMMA) + "]无交集, 忽略");
                    return;
                }
                int min = strategy.getMin() - requiredNameList.size();
                int max = strategy.getMax() - requiredNameList.size();
                if (intersection.size() >= min && intersection.size() <= max) {
                    // 符合校验, 跳过
                    NormalUseForTestUtil.out("实选[" + CollectionUtil.join(realChoose, SymbolConstantUtil.ENGLISH_COMMA) + "]符合策略[" + CollectionUtil.join(realStrategy, SymbolConstantUtil.ENGLISH_COMMA) + "]");
                    return;
                }
                if (intersection.size() < min) {
                    NormalUseForTestUtil.out("实选[" + CollectionUtil.join(realChoose, SymbolConstantUtil.ENGLISH_COMMA) + "]不符合策略[" + CollectionUtil.join(realStrategy, SymbolConstantUtil.ENGLISH_COMMA) + "], 原因: 至少选" + min + "个");
                }
                if (intersection.size() > max) {
                    NormalUseForTestUtil.out("实选[" + CollectionUtil.join(realChoose, SymbolConstantUtil.ENGLISH_COMMA) + "]不符合策略[" + CollectionUtil.join(realStrategy, SymbolConstantUtil.ENGLISH_COMMA) + "], 原因: 至多选" + max + "个");
                }
            });
        });
    }

    private static <T> List<T> recursiveSearchRelationship(List<T> keyList, Map<T, List<Pair<T, T>>> relationMap) {
        List<T> resultList = Lists.newArrayList();
        for (T key : keyList) {
            resultList.add(key);
            if (CollectionUtil.isNotEmpty(relationMap.get(key))) {
                List<T> valueList = CollectionUtil.transToList(relationMap.get(key), Pair::getRight);
                resultList.addAll(recursiveSearchRelationship(valueList, relationMap));
            }
        }
        // 递归结束条件
        return resultList;
    }

    /**
     * A->B->C->D, A->Q, A-x-F, F-x-G, G-x-H
     * M->N->O->P, M-x-X, X-x-Y, Y-x-Z
     * <p>
     * if i input [A, M], the resultIndexList should like this:
     * enabled:   [A, B, C, D, Q, M, N, O, P]
     * disabled:  [F, G, H, X, Y, Z]
     *
     * @return
     */
    private static Map<Pair<String, String>, Boolean> loadRelationshipMap() {
        Map<Pair<String, String>, Boolean> dataMap = Maps.newHashMap();
        dataMap.put(Pair.of("A", "B"), Boolean.TRUE);
        dataMap.put(Pair.of("A", "Q"), Boolean.TRUE);
        dataMap.put(Pair.of("B", "C"), Boolean.TRUE);
        dataMap.put(Pair.of("C", "D"), Boolean.TRUE);
        dataMap.put(Pair.of("M", "N"), Boolean.TRUE);
        dataMap.put(Pair.of("N", "O"), Boolean.TRUE);
        dataMap.put(Pair.of("O", "P"), Boolean.TRUE);
        dataMap.put(Pair.of("A", "F"), Boolean.FALSE);
        dataMap.put(Pair.of("F", "G"), Boolean.FALSE);
        dataMap.put(Pair.of("G", "H"), Boolean.FALSE);
        dataMap.put(Pair.of("M", "X"), Boolean.FALSE);
        dataMap.put(Pair.of("X", "Y"), Boolean.FALSE);
        dataMap.put(Pair.of("Y", "Z"), Boolean.FALSE);
        return dataMap;
    }

    /**
     * 连环绑定
     * ↓↽↓↽↑
     * A↣B↣C↣D, 环状结构: B↣C、C↣D、B↣C↣D
     * 连环排斥
     * ↓↽↽↽↑
     * A×M×N,    环状结构: A×M×N
     *
     * @return
     */
    private static Map<Pair<String, String>, Boolean> loadChainRelationshipMap() {
        Map<Pair<String, String>, Boolean> dataMap = Maps.newHashMap();
        dataMap.put(Pair.of("A", "B"), Boolean.TRUE);
        dataMap.put(Pair.of("B", "C"), Boolean.TRUE);
        dataMap.put(Pair.of("C", "B"), Boolean.TRUE);
        dataMap.put(Pair.of("C", "D"), Boolean.TRUE);
        dataMap.put(Pair.of("D", "B"), Boolean.TRUE);
        dataMap.put(Pair.of("D", "C"), Boolean.TRUE);
        dataMap.put(Pair.of("D", "E"), Boolean.TRUE);
        dataMap.put(Pair.of("A", "M"), Boolean.FALSE);
        dataMap.put(Pair.of("M", "N"), Boolean.FALSE);
        dataMap.put(Pair.of("N", "A"), Boolean.FALSE);
        return dataMap;
    }

    private static Map<Pair<String, String>, Boolean> loadChainRelationshipMap2() {
        Map<Pair<String, String>, Boolean> dataMap = Maps.newHashMap();
        dataMap.put(Pair.of("A", "B"), Boolean.TRUE);
        dataMap.put(Pair.of("B", "C"), Boolean.TRUE);
        dataMap.put(Pair.of("B", "E"), Boolean.TRUE);
        dataMap.put(Pair.of("C", "D"), Boolean.TRUE);
        dataMap.put(Pair.of("C", "F"), Boolean.TRUE);
        dataMap.put(Pair.of("D", "F"), Boolean.TRUE);
        dataMap.put(Pair.of("D", "B"), Boolean.TRUE);
        dataMap.put(Pair.of("E", "F"), Boolean.TRUE);
        return dataMap;
    }

    private static Integer calculateSum(List<Integer> ids, Map<Integer, Integer> priceMap) {
        List<Integer> prices = CollectionUtil.transToList(ids, priceMap::get);
        return CollectionUtil.sum(prices, Integer::intValue).intValue();
    }

    @Test
    public void priceSumTest() {
        List<Integer> ids = Lists.newArrayList(74, 73, 75, 76);
        NormalUseForTestUtil.out(CollectionUtil.join(ids, SymbolConstantUtil.ENGLISH_COMMA));
        NormalUseForTestUtil.out("" + calculateSum(ids, loadPrice()));
    }

    /**
     * 绑定与互斥测试
     */
    @Test
    public void bindingAndExclusiveTest() {
        Map<Pair<String, String>, Boolean> relationshipMap = loadRelationshipMap();
        Set<String> positiveSelectedSet = Sets.newHashSet("F", "B", "G", "A");
        // 根据关系分组
        Map<Boolean, List<Pair<String, String>>> bindingOrExclusiveAsKeyMap = CollectionUtil.groupIndexToMap(relationshipMap.entrySet(), Map.Entry::getValue, Map.Entry::getKey);
        Map<Boolean, List<String>> resultRelationshipMap = groupBindingAndExclusiveRelationship(bindingOrExclusiveAsKeyMap, Lists.newArrayList(positiveSelectedSet));
        List<String> exclusiveResultList = resultRelationshipMap.get(Boolean.TRUE);
        List<String> bindingResultList = resultRelationshipMap.get(Boolean.FALSE);

        NormalUseForTestUtil.out("positiveSelect:");
        positiveSelectedSet.forEach(NormalUseForTestUtil::out);
        NormalUseForTestUtil.out("binding:");
        bindingResultList.forEach(NormalUseForTestUtil::out);
        NormalUseForTestUtil.out("exclusive:");
        exclusiveResultList.forEach(NormalUseForTestUtil::out);
    }

    /**
     * 绑定与互斥关系探测循环测试
     */
    @Test
    public void bindingAndExclusiveProbeCycleTest() {
        Map<Pair<String, String>, Boolean> relationshipMap = loadChainRelationshipMap2();
        Map<Boolean, List<Pair<String, String>>> bindingOrExclusiveAsKeyMap = CollectionUtil.groupIndexToMap(relationshipMap.entrySet(), Map.Entry::getValue, Map.Entry::getKey);
        List<Pair<String, String>> bindingRelationshipList = bindingOrExclusiveAsKeyMap.get(Boolean.TRUE);
        List<Pair<String, String>> exclusiveRelationshipList = bindingOrExclusiveAsKeyMap.get(Boolean.FALSE);
        if (CollectionUtil.isEmpty(bindingRelationshipList)) {
            bindingRelationshipList = Lists.newArrayList();
        }
        if (CollectionUtil.isEmpty(exclusiveRelationshipList)) {
            exclusiveRelationshipList = Lists.newArrayList();
        }
        List<ArrayList<String>> bindingCycleList = probeDirectedGraphCycleByTarjanAlgorithm(bindingRelationshipList);
        List<ArrayList<String>> exclusiveCycleList = probeDirectedGraphCycleByTarjanAlgorithm(exclusiveRelationshipList);
        if (CollectionUtil.isEmpty(bindingCycleList) && CollectionUtil.isEmpty(exclusiveCycleList)) {
            NormalUseForTestUtil.out("没有循环, 请放心使用");
            return;
        }

        NormalUseForTestUtil.out("绑定关系环状元素:");
        bindingCycleList.forEach(itemList -> {
            itemList.forEach(NormalUseForTestUtil::outWithSpace);
            NormalUseForTestUtil.outNextLine();
        });
        NormalUseForTestUtil.outNextLine();
        NormalUseForTestUtil.out("排斥关系环状元素:");
        exclusiveCycleList.forEach(itemList -> {
            itemList.forEach(NormalUseForTestUtil::outWithSpace);
            NormalUseForTestUtil.outNextLine();
        });
        NormalUseForTestUtil.outNextLine();
    }

    /**
     * 根据绑定互斥关系, 以及选定的参考元素RSet, 计算绑定关系列表 + 排斥关系列表
     * 思路:
     * 1.选定参考元素RSet所绑定的元素BSet, 是要被绑定的, 其并集 RSet + BSet = bindingResultSet
     * 2.绑定元素并集bindingResultSet所排斥的元素, 记为一级排斥元素ESet
     * 3.一级排斥元素ESet所绑定的元素, 记作二级排斥元素EBSet, 也是要被排斥的
     *
     * @param bindingOrExclusiveAsKeyMap
     * @param calculateReferElementList
     * @param <T>
     * @return
     */
    private <T> Map<Boolean, List<T>> groupBindingAndExclusiveRelationship(Map<Boolean, List<Pair<T, T>>> bindingOrExclusiveAsKeyMap, List<T> calculateReferElementList) {
        // 对于绑定|互斥关系, 再次分组归并, 将一个元素key的所有绑定|互斥关系汇聚在value中
        Map<T, List<Pair<T, T>>> bindingMap = CollectionUtil.groupIndexToMap(bindingOrExclusiveAsKeyMap.get(Boolean.TRUE), Pair::getLeft);
        Map<T, List<Pair<T, T>>> exclusiveMap = CollectionUtil.groupIndexToMap(bindingOrExclusiveAsKeyMap.get(Boolean.FALSE), Pair::getLeft);
        List<T> bindingResultList = recursiveSearchRelationship(calculateReferElementList, bindingMap);
        // 计算排斥: 所有绑定的元素所排斥的都是要排斥的
        Set<T> exclusiveCalculateReferElements = CollectionUtil.union(HashSet::new, calculateReferElementList, bindingResultList);
        // 寻找一级排斥, 一级排斥绑定的所有都是二级排斥
        Set<T> firstLevelExclusiveResults = Sets.newHashSet();
        exclusiveCalculateReferElements.forEach(key -> {
            if (CollectionUtil.isNotEmpty(exclusiveMap.get(key))) {
                firstLevelExclusiveResults.addAll(CollectionUtil.transToList(exclusiveMap.get(key), Pair::getRight));
            }
        });
        List<T> exclusiveResultList = recursiveSearchRelationship(Lists.newArrayList(firstLevelExclusiveResults), bindingMap);

        // 如果输入本身有矛盾, 那么该矛盾元素既在绑定结果中又在排斥结果中
        Set<T> contradictionElementSet = CollectionUtil.intersection(HashSet::new, bindingResultList, exclusiveResultList);
        if (CollectionUtil.isNotEmpty(contradictionElementSet)) {
            throw new IllegalArgumentException("非法操作, 以下不可选: " + CollectionUtil.join(contradictionElementSet, SymbolConstantUtil.ENGLISH_COMMA));
        }
        Map<Boolean, List<T>> resultMap = new HashMap<>(4, 0.75f);
        resultMap.put(Boolean.TRUE, bindingResultList);
        resultMap.put(Boolean.FALSE, exclusiveResultList);
        return resultMap;
    }

    private static Map<Integer, Integer> loadPrice() {
        Map<Integer, Integer> priceMap = Maps.newHashMap();
        priceMap.put(73, 8000);
        priceMap.put(74, 4000);
        priceMap.put(75, 10000);
        priceMap.put(76, 20000);
        priceMap.put(77, 12000);
        priceMap.put(78, 5800);
        priceMap.put(79, 6400);
        priceMap.put(80, 7865);
        priceMap.put(81, 16969);
        return priceMap;
    }

    /**
     * 使用Robert•Tarjan算法计算有向图的强连通分量(SSC)
     * 强连通分量必定是环状结构, 单个元素是自环, 在实际业务中求得的环元素大于1个即可
     *
     * @param relationshipList
     * @param <T>
     * @return
     */
    private static <T> List<ArrayList<T>> probeDirectedGraphCycleByTarjanAlgorithm(List<Pair<T, T>> relationshipList) {
        TarjanSSC<T> tarjan = new TarjanSSC<>(new DirectedGraph<>(relationshipList));
        List<ArrayList<T>> tarjanSSCList = tarjan.calculateByIndex();
        // 打印结果
        tarjanSSCList.forEach(itemList -> {
            itemList.forEach(NormalUseForTestUtil::outWithSpace);
            NormalUseForTestUtil.outNextLine();
        });
        NormalUseForTestUtil.outNextLine();
        return CollectionUtil.filterAndTransList(tarjanSSCList, item -> item.size() > 1, Function.identity());
    }

    @Data
    @Accessors(chain = true)
    public static class Strategy {
        private List<String> configNameList = Lists.newArrayList();
        private Integer min;
        private Integer max;
    }


}
