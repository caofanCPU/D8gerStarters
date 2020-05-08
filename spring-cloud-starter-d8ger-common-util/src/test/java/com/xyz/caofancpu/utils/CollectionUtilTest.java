package com.xyz.caofancpu.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.xyz.caofancpu.core.CollectionUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
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
 * 4.姓名首字母排序比较器
 *
 * @author D8GER
 */
public class CollectionUtilTest {

    public static void main(String[] args) {
        testgroupIndexToMapWithReferKey();
    }

    public static void testSumTopK() {
        List<Long> list = Lists.newArrayList(100L, 99L, 44L, 889L);
        BigDecimal minTop2Sum = CollectionUtil.sumTopK(list, Long::longValue, Comparator.comparing(Long::longValue), 2);
        BigDecimal maxTop2Sum = CollectionUtil.sumTopK(list, Long::longValue, Comparator.comparing(Long::longValue).reversed(), 2);
        System.out.println("最小前2: " + minTop2Sum.longValue() + "\n最大前2: " + maxTop2Sum.longValue());
    }

    public static void testListToCollection() {
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

    public static void testListToMap() {
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
    public static void testListToMapForDistinct() {
        List<TestStudent> studentList = new ArrayList<>();
        TestStudent testStudent1 = new TestStudent().setStudentId(1223).setStudentName("渣渣辉");
        TestStudent testStudent2 = new TestStudent().setStudentId(1223).setStudentName("渣渣辉");
        studentList.add(testStudent1);
        studentList.add(testStudent2);
        Map<String, TestStudent> studentMap1 = CollectionUtil.transToMap(HashMap::new, studentList, TestStudent::getStudentName);
        Map<Integer, TestStudent> studentMap2 = CollectionUtil.transToMap(HashMap::new, studentList, TestStudent::getStudentId);
    }

    public static void testListByMerge() {
        List<TestExamData> sourceList = loadTestExamStudentDatas();
        Map<TestExamData, List<TestStudent>> examStudentListHashMap = CollectionUtil.transToMapByMerge(HashMap::new, sourceList, Function.identity(), TestExamData::getStudentList);
        Map<TestStudent, List<TestExamData>> studentExamHashMap = CollectionUtil.reverseKV(examStudentListHashMap, Function.identity(), Function.identity());

        int a = 1;
    }

    public static void testgroupIndexToMapWithReferKey() {
        TestExamData exam1 = new TestExamData().setId(3333).setExamName("E-A1");
        TestExamData exam2 = new TestExamData().setId(4444).setExamName("E-B2");
        TestExamData exam3 = new TestExamData().setId(5555).setExamName("E-C3");
        List<TestExamData> examDataList = Lists.newArrayList(exam1, exam2, exam3, exam3);
        Set<Integer> ids = Sets.newHashSet(3333, 4444, 5555, 6666);
        Map<Integer, List<String>> resultMap1 = CollectionUtil.groupIndexToMap(examDataList, ids, TestExamData::getId, TestExamData::getExamName);
        TreeMap<Integer, LinkedList<String>> resultMap2 = CollectionUtil.groupIndexToMap(TreeMap::new, LinkedList::new, examDataList, ids, TestExamData::getId, TestExamData::getExamName);
        int a = 1;

    }

    public static void testFindOrExist() {
        List<D8gerEnum> sourceList = Lists.newArrayList(D8gerEnum.YESTERDAY, D8gerEnum.TOMORROW);
        boolean existAdmin = CollectionUtil.exist(sourceList, item -> D8gerEnum.YESTERDAY.equals(item));
        boolean existCombine = CollectionUtil.exist(sourceList, item -> D8gerEnum.TOMORROW.equals(item));
        boolean existTeach = CollectionUtil.exist(sourceList, item -> D8gerEnum.TODAY.equals(item));
    }

    public static void testNameComparator() {
        List<String> strings = Arrays.asList(
                "丁海寅",
                "周杰伦",
                "胡歌",
                "谢霆锋",
                "陈伟霆",
                "陈道明"
        );
        System.out.println(CollectionUtil.join(strings, ","));
        Comparator<String> nameComparator = CollectionUtil.getNameComparator(String::intern);
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
