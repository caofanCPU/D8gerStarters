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

package com.xyz.caofancpu.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.xyz.caofancpu.core.CollectionUtil;
import com.xyz.caofancpu.extra.NormalUseForTestUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 集合工具类方法使用示例
 * 规律：入参 带有Supplier<?>就是可以自定义结果收集容器
 * <p>
 * 1.集合(包括数组)判空或判非空 {@link CollectionUtil#isEmpty}
 * 2.操作Collection, 常见的就是List, 以List为例
 * - List转换List/Set
 * - List转Map
 * - List中查找元素
 * - List/数组, 元素根据指定分隔符拼接 {@link CollectionUtil#join}
 * 3.操作Map
 * -
 * -
 * 4.姓名首字母排序比较器, 支持null值的增序比较器降序比较器
 *
 * @author D8GER
 */
@RunWith(JUnit4.class)
public class CollectionUtilTest {

    @Before
    public void before() {
        NormalUseForTestUtil.out("---------测试前---------");
    }

    @After
    public void after() {
        NormalUseForTestUtil.out("---------测试后---------");
    }

    @Test
    public void testTransToCollWithFlatMap() {
        List<Set<Integer>> dataList = Lists.newArrayList(Sets.newHashSet(1, 2), Sets.newHashSet(3, 2));
        List<Integer> arrayList = CollectionUtil.transToCollWithFlatMap(ArrayList::new, dataList, Function.identity());
        Set<Integer> hashSet = CollectionUtil.transToCollWithFlatMap(HashSet::new, dataList, Function.identity());
        HashSet<String> bossSet = CollectionUtil.enhanceTransToCollWithFlatMap(HashSet::new, dataList, Function.identity(), String::valueOf);
        NormalUseForTestUtil.out(CollectionUtil.show(arrayList));
        NormalUseForTestUtil.out(CollectionUtil.show(hashSet));
        NormalUseForTestUtil.out(CollectionUtil.show(bossSet));
    }

    @Test
    public void testMapBasic() {
        // Map.merge, 可以指定函数对结果进行合并, 当值为null时则清除
        Map<String, List<Integer>> map = new HashMap<>();
        map.merge("A", Lists.newArrayList(1, 2, 3), (oldList, newList) -> {
            oldList.addAll(newList);
            return oldList;
        });
        NormalUseForTestUtil.out(CollectionUtil.showMap(map));
        map.merge("A", Lists.newArrayList(4, 5, 6), (oldList, newList) -> {
            oldList.addAll(newList);
            return oldList;
        });
        NormalUseForTestUtil.out(CollectionUtil.showMap(map));

        // Map.compute|computeIfAbsent|computeIfPresent, 根据指定函数赋值, 当值为null时则清除
        map.computeIfAbsent("B", init -> Lists.newArrayList()).addAll(Lists.newArrayList(20, 21));
        map.computeIfAbsent("B", init -> Lists.newArrayList()).add(22);
        NormalUseForTestUtil.out(CollectionUtil.showMap(map));
        map.computeIfPresent("B", (k, vList) -> {
            vList.add(222);
            return vList;
        });
        NormalUseForTestUtil.out(CollectionUtil.showMap(map));

        map.compute("B", (k, vList) -> null);
        NormalUseForTestUtil.out(CollectionUtil.showMap(map));
        map.compute("B", (k, vList) -> Lists.newArrayList(5, 2, 0));
        NormalUseForTestUtil.out(CollectionUtil.showMap(map));
    }

    @Test
    public void testTopK() {
        Integer[] nums = IntStream.rangeClosed(1, 9).boxed().toArray(Integer[]::new);
        ArrayUtils.shuffle(nums);

        List<Integer> source = Arrays.asList(nums);
        NormalUseForTestUtil.out("原始数据: " + Arrays.toString(source.toArray()));
        List<Integer> top5 = CollectionUtil.filterTopK(source, Function.identity(), Comparator.comparing(Integer::intValue), 5);
        List<Integer> removeTop5 = CollectionUtil.removeTopK(source, Function.identity(), Comparator.comparing(Integer::intValue), 5);
        NormalUseForTestUtil.out("前5个元素: " + Arrays.toString(top5.toArray()));
        NormalUseForTestUtil.out("剔除前5个元素后剩余元素: " + Arrays.toString(removeTop5.toArray()));
    }

    @Test
    public void testSortMap() {
        Map<Integer, Integer> kvMap = new HashMap<>(8, 0.75f);
        kvMap.put(11, 9);
        kvMap.put(4, 6);
        kvMap.put(31, 7);
        kvMap.put(2, 8);
        LinkedHashMap<Integer, Integer> kAscOrderResultMap = CollectionUtil.sortByKey(kvMap);
        LinkedHashMap<Integer, Integer> kDAscOrderResultMap = CollectionUtil.sortByKey(kvMap, true);
        LinkedHashMap<Integer, Integer> vAscOrderResultMap = CollectionUtil.sortByValue(kvMap);
        LinkedHashMap<Integer, Integer> vDAscOrderResultMap = CollectionUtil.sortByValue(kvMap, true);
        LinkedHashMap<Integer, Integer> keySubtractValueAscOrderResultMap = CollectionUtil.sortByKeyMapper(kvMap, key -> key - kvMap.get(key));
        LinkedHashMap<Integer, Integer> keySubtractValueDescOrderResultMap = CollectionUtil.sortByKeyMapper(kvMap, key -> key - kvMap.get(key), true);

        kAscOrderResultMap.forEach((k, v) -> NormalUseForTestUtil.out("<" + k + ", " + v + ">"));
        NormalUseForTestUtil.outNextLine();
        kDAscOrderResultMap.forEach((k, v) -> NormalUseForTestUtil.out("<" + k + ", " + v + ">"));
        NormalUseForTestUtil.outNextLine();
        vAscOrderResultMap.forEach((k, v) -> NormalUseForTestUtil.out("<" + k + ", " + v + ">"));
        NormalUseForTestUtil.outNextLine();
        vDAscOrderResultMap.forEach((k, v) -> NormalUseForTestUtil.out("<" + k + ", " + v + ">"));
        NormalUseForTestUtil.outNextLine();
        keySubtractValueAscOrderResultMap.forEach((k, v) -> NormalUseForTestUtil.out("<" + k + ", " + v + ">"));
        NormalUseForTestUtil.outNextLine();
        keySubtractValueDescOrderResultMap.forEach((k, v) -> NormalUseForTestUtil.out("<" + k + ", " + v + ">"));
    }

    @Test
    public void testFilterAndTransArray() {
        List<TestExamData> sourceList = loadTestExamDatas();
        String[] resultArray = CollectionUtil.filterAndTransArray(sourceList, item -> item.getId() > 2001, TestExamData::getExamName, String[]::new);
        NormalUseForTestUtil.out(Arrays.toString(resultArray));
    }

    @Test
    public void testSumTopK() {
        List<Long> list = Lists.newArrayList(100L, 99L, 44L, 889L);
        BigDecimal minTop2Sum = CollectionUtil.sumTopK(list, Long::longValue, Comparator.comparing(Long::longValue), 2);
        BigDecimal maxTop2Sum = CollectionUtil.sumTopK(list, Long::longValue, Comparator.comparing(Long::longValue).reversed(), 2);
        System.out.println("最小前2: " + minTop2Sum.longValue() + "\n最大前2: " + maxTop2Sum.longValue());
    }

    @Test
    public void testListToCollection() {
        List<TestExamData> sourceList = loadTestExamDatas();
        // 转List
        List<String> examNameList = CollectionUtil.transToList(sourceList, TestExamData::getExamName);
        // 转set
        Set<Integer> examIdList = CollectionUtil.transToSet(sourceList, TestExamData::getId);

        // 如果需要去重且有序，可以指定收集结果的容器
        TreeSet<String> treeSet = CollectionUtil.transToCollection(TreeSet::new, sourceList, TestExamData::getExamName);

        // 去重, 返回去重后的元素列表
        List<TestExamData> distinctedExamList = CollectionUtil.distinctList(sourceList, Function.identity());

        // 根据元素的某个字段去重, 返回去重后的元素列表
        List<TestExamData> distinctedExamListById = CollectionUtil.distinctListByField(sourceList, Comparator.comparingInt(TestExamData::getId));
        List<TestExamData> distinctedExamListByName = CollectionUtil.distinctListByField(sourceList, Comparator.comparing(TestExamData::getExamName));

        // 对List中查找指定字段值的一个元素: 第一个 或者 任意一个
        TestExamData firstExamData = CollectionUtil.findFirst(sourceList, TestExamData::getId, 2001);
        TestExamData oneExamData = CollectionUtil.findAny(sourceList, TestExamData::getId, 2001);

        // 对List中查找指定字段值的所有元素
        List<TestExamData> specialValueExamDataLIst = CollectionUtil.findAll(sourceList, TestExamData::getId, 2001);

        // 对于List或者数组, 经常需要将元素用分隔符拼接为字符串, 这时可以
        String examNameJoinResult1 = CollectionUtil.join(examNameList, ", ");
        String examNameJoinResult2 = CollectionUtil.join(examNameList.toArray(new String[0]), ",");
    }

    @Test
    public void testListToMap() {
        List<TestExamData> sourceList = loadTestExamDatas();
        // 转Map
        Map<Integer, TestExamData> idMap = CollectionUtil.transToMap(HashMap::new, sourceList, TestExamData::getId);

        // 转Map时，希望返回的有序的Map
        LinkedHashMap<Integer, TestExamData> idLinkedHashMap = CollectionUtil.transToMap(LinkedHashMap::new, sourceList, TestExamData::getId);
        TreeMap<Integer, TestExamData> idTreeMap = CollectionUtil.transToMap(TreeMap::new, sourceList, TestExamData::getId);

        // 转map时，又想对值进行一些操作
        Map<Integer, String> idNameMap = CollectionUtil.transToMap(HashMap::new, sourceList, TestExamData::getId, TestExamData::getExamName);

        // 对List分组转为Map, 分组后的结果一般为 Map<K, List<V>>, 最简单的
        Map<Integer, List<TestExamData>> idExamsMap = CollectionUtil.groupIndexToMap(sourceList, TestExamData::getId);

        // 除了一般分组，希望返回的Map是有序的
        TreeMap<Integer, List<TestExamData>> idExamsTreeMap = CollectionUtil.groupIndexToMap(TreeMap::new, sourceList, TestExamData::getId);

        // 接下来的一个方法很少用，但是功能很全: 支持指定Map, 指定Map值收集容器, 还可以对值进行操作得到其他对象, 例如
        // Map按照考试ID有序, 对于每个考试ID，其对应的考试名称列表去重且有序
        LinkedHashMap<Integer, TreeSet<String>> sortedKeyAndValueExamsLinkedHashMap = CollectionUtil.groupIndexToMap(LinkedHashMap::new, TreeSet::new, sourceList, TestExamData::getId, TestExamData::getExamName);
    }

    /**
     * List转Map, 主要起到去重功能
     */
    @Test
    public void testListToMapForDistinct() {
        List<TestStudent> studentList = new ArrayList<>();
        TestStudent testStudent1 = new TestStudent().setStudentId(1223).setStudentName("渣渣辉");
        TestStudent testStudent2 = new TestStudent().setStudentId(1223).setStudentName("渣渣辉");
        studentList.add(testStudent1);
        studentList.add(testStudent2);
        Map<String, TestStudent> studentMap1 = CollectionUtil.transToMap(HashMap::new, studentList, TestStudent::getStudentName);
        Map<Integer, TestStudent> studentMap2 = CollectionUtil.transToMap(HashMap::new, studentList, TestStudent::getStudentId);
    }

    @Test
    public void testListByMerge() {
        List<TestExamData> sourceList = loadTestExamStudentDatas();
        Map<TestExamData, List<TestStudent>> examStudentListHashMap = CollectionUtil.transToMapByMerge(HashMap::new, sourceList, Function.identity(), TestExamData::getStudentList);
        Map<TestStudent, List<TestExamData>> studentExamHashMap = CollectionUtil.reverseKV(examStudentListHashMap, Function.identity(), Function.identity());

        int a = 1;
    }

    @Test
    public void testGroupIndexToMapWithReferKey() {
        TestExamData exam1 = new TestExamData().setId(3333).setExamName("E-A1");
        TestExamData exam2 = new TestExamData().setId(4444).setExamName("E-B2");
        TestExamData exam3 = new TestExamData().setId(5555).setExamName("E-C3");
        List<TestExamData> examDataList = Lists.newArrayList(exam1, exam2, exam3, exam3);
        Set<Integer> ids = Sets.newHashSet(3333, 4444, 5555, 6666);
        Map<Integer, List<String>> resultMap1 = CollectionUtil.groupIndexToMap(examDataList, ids, TestExamData::getId, TestExamData::getExamName);
        TreeMap<Integer, LinkedList<String>> resultMap2 = CollectionUtil.groupIndexToMap(TreeMap::new, LinkedList::new, examDataList, ids, TestExamData::getId, TestExamData::getExamName);
        int a = 1;

    }

    @Test
    public void testFindOrExist() {
        List<D8gerEnum> sourceList = Lists.newArrayList(D8gerEnum.YESTERDAY, D8gerEnum.TOMORROW);
        boolean existAdmin = CollectionUtil.exist(sourceList, D8gerEnum.YESTERDAY::equals);
        boolean existCombine = CollectionUtil.exist(sourceList, D8gerEnum.TOMORROW::equals);
        boolean existTeach = CollectionUtil.exist(sourceList, D8gerEnum.TODAY::equals);
    }

    @Test
    public void testAscOrDescComparator() {
        List<Integer> numbers = Arrays.asList(null, 4, 7, 2, null, 3, null);
        numbers.sort(CollectionUtil.getAscComparator(Function.identity()));
        System.out.println(CollectionUtil.show(numbers));
        numbers.sort(CollectionUtil.getDescComparator(Function.identity()));
        System.out.println(CollectionUtil.show(numbers));
    }

    @Test
    public void testNameComparator() {
        List<String> strings = Arrays.asList(
                null,
                "丁海寅",
                "周杰伦",
                "胡歌",
                null,
                "谢霆锋",
                "陈伟霆",
                "陈道明",
                null
        );
        System.out.println(CollectionUtil.join(strings, ","));
        Comparator<String> nameComparator = CollectionUtil.getNameComparator(Function.identity());
        strings.sort(nameComparator);
        System.out.println(CollectionUtil.join(strings, ","));
    }

    /**
     * list操作测试数据, 含有重复数据
     *
     * @return
     */
    private static List<TestExamData> loadTestExamDatas() {
        List<TestExamData> examDataList = IntStream.rangeClosed(1, 4)
                .boxed()
                .map(i -> new TestExamData().setId(2000 + i).setExamName("考试[" + i + "]"))
                .collect(Collectors.toList());
        examDataList.add(examDataList.get(0));
        return examDataList;
    }

    /**
     * List-Map操作数据
     * 主要信息：张三 参加了考试 1,2 ; 李四参加了考试 3,2; 王五参加了考试 1,2,3
     * 问题1：如何得到每场考试参加的人数, 将此问题的结果保存在Map<examName, List<TestStudent> 中
     * 问题2: 在问题1的结果中, Map<examName, List<TestStudent>描述了 每场考试对应的学生, 如何得到每个学生对应的开始的列表呢？
     * <p>
     * 问题1可以用 {@link CollectionUtil#transToMapByMerge} 解决
     * 问题1可以用键值反转 {@link CollectionUtil#reverseKV} 解决
     *
     * @return
     */
    private static List<TestExamData> loadTestExamStudentDatas() {
        TestExamData exam1 = new TestExamData().setId(3333).setExamName("E-A1");
        TestExamData exam2 = new TestExamData().setId(4444).setExamName("E-B2");
        TestExamData exam3 = new TestExamData().setId(5555).setExamName("E-C3");

        TestStudent zhangsan = new TestStudent().setStudentId(1).setStudentName("张三");
        TestStudent lisi = new TestStudent().setStudentId(1).setStudentName("李四");

        List<TestStudent> exam1StudentList = Lists.newArrayList(zhangsan);

        List<TestStudent> exam2StudentList = Lists.newArrayList(zhangsan, lisi);

        List<TestStudent> exam3StudentList = Lists.newArrayList(lisi);

        exam1.setStudentList(exam1StudentList);
        exam2.setStudentList(exam2StudentList);
        exam3.setStudentList(exam3StudentList);

        TestStudent wangwu = new TestStudent().setStudentId(333).setStudentName("王五");
        List<TestStudent> examStudentList_1 = Lists.newArrayList(wangwu);
        TestExamData exam1_1 = new TestExamData().setId(3333).setExamName("E-A1");
        TestExamData exam2_1 = new TestExamData().setId(4444).setExamName("E-B2");
        TestExamData exam3_1 = new TestExamData().setId(5555).setExamName("E-C3");
        exam1_1.setStudentList(examStudentList_1);
        exam2_1.setStudentList(examStudentList_1);
        exam3_1.setStudentList(examStudentList_1);

        return Lists.newArrayList(exam1, exam2, exam3, exam1_1, exam2_1, exam3_1);
    }

    @Accessors(chain = true)
    private enum D8gerEnum {
        YESTERDAY(1, "昨天"),
        TODAY(2, "今天"),
        TOMORROW(3, "明天"),

        ;

        private final int value;
        private final String title;

        D8gerEnum(int value, String title) {
            this.value = value;
            this.title = title;
        }
    }

    @Data
    @Accessors(chain = true)
    private static class TestExamData {
        private Integer id;
        private String examName;
        private List<TestStudent> studentList;
    }

    @Data
    @Accessors(chain = true)
    private static class TestStudent {
        private Integer studentId;
        private String studentName;
    }

}
