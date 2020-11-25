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
import com.xyz.caofancpu.annotation.AttentionDoc;
import com.xyz.caofancpu.annotation.ImportDoc;
import com.xyz.caofancpu.constant.SymbolConstantUtil;
import lombok.NonNull;
import net.sourceforge.pinyin4j.PinyinHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 集合工具类: https://dzone.com/articles/functional-programming-patterns-with-java-8
 *
 * @author D8GER
 */
public class CollectionUtil extends CollectionUtils {

    /**
     * 从集合中截取前面的子集合
     * .          take from head and convert to
     * . +--------------------------------------------------------+
     * . |                                                        v
     * +--------+  contains   +---+  convert   +---+  collect   +---+
     * | source | ----------> | T | ---------> | F | ---------> | R |
     * +--------+             +---+            +---+            +---+
     *
     * @param resultColl 结果收集容器
     * @param source     数据源
     * @param size       截取元素总数
     * @param mapper     元素转换函数
     * @return
     */
    public static <T, F, R extends Collection<F>> R subListHead(Supplier<R> resultColl, Collection<T> source, int size, Function<? super T, ? extends F> mapper) {
        if (size >= source.size()) {
            return transToCollection(resultColl, source, mapper);
        }
        return source.stream().limit(size).filter(Objects::nonNull).map(mapper).collect(Collectors.toCollection(resultColl));
    }

    /**
     * 从集合中截取后面的子集合
     * .          take from tail and convert to
     * . +--------------------------------------------------------+
     * . |                                                        v
     * +--------+  contains   +---+  convert   +---+  collect   +---+
     * | source | ----------> | T | ---------> | F | ---------> | R |
     * +--------+             +---+            +---+            +---+
     *
     * @param resultColl 结果收集容器
     * @param source     数据源
     * @param size       截取元素总数
     * @param mapper     元素转换函数
     * @return
     */
    public static <T, F, R extends Collection<F>> R subListTail(Supplier<R> resultColl, Collection<T> source, int size, Function<? super T, ? extends F> mapper) {
        if (size >= source.size()) {
            return transToCollection(resultColl, source, mapper);
        }
        return source.stream().skip(source.size() - size).filter(Objects::nonNull).map(mapper).collect(Collectors.toCollection(resultColl));
    }

    /**
     * 对列表元素字段执行指定函数后, 按照comparator排列, 取前k个子列表
     * <p>
     * [coll]-- contains -->[E]-- convert -->[T]
     * [coll]-- convert -->[List\[T\]]-- sort and take top K -->[Result: List\[T\]]
     *
     * @param coll       数据源
     * @param mapper     元素转换函数
     * @param comparator 排序比较器
     * @param k          取前k个元素
     * @return
     */
    public static <T, E> List<T> filterTopK(@NonNull Collection<E> coll, Function<? super E, ? extends T> mapper, Comparator<T> comparator, int k) {
        if (k <= 0) {
            return emptyList();
        }
        List<T> tList = transToList(coll, mapper);
        if (k > coll.size()) {
            tList.sort(comparator);
            return tList;
        }

        return tList.stream().sorted(comparator).limit(k).collect(Collectors.toList());
    }

    /**
     * 对列表元素字段执行指定函数后, 按照comparator排列, 排除前k个元素后的子列表
     *
     * @param coll       数据源
     * @param mapper     元素转换函数
     * @param comparator 排序比较器
     * @param k          取前k个元素
     * @return
     */
    public static <T, E> List<T> removeTopK(@NonNull Collection<E> coll, Function<? super E, ? extends T> mapper, Comparator<T> comparator, int k) {
        if (k > coll.size()) {
            return emptyList();
        }
        List<T> tList = transToList(coll, mapper);
        if (k <= 0) {
            tList.sort(comparator);
            return tList;
        }
        return tList.stream().sorted(comparator).skip(k).collect(Collectors.toList());
    }

    /**
     * 求元素类型相同的两个集合的并集(a ∪ b), 可指定结果容器类型
     *
     * @param resultColl 结果收集容器
     * @param a          数据源a
     * @param b          数据源b
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <E, C extends Collection<E>> C union(Supplier<C> resultColl, @NonNull Collection<E> a, @NonNull Collection<E> b) {
        C result = resultColl.get();
        union(a, b).forEach(item -> result.add((E) item));
        return result;
    }

    /**
     * 求元素类型相同的两个集合的交集(a ∩ b), 可指定结果容器类型
     *
     * @param resultColl 结果收集容器
     * @param a          数据源a
     * @param b          数据源b
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <E, C extends Collection<E>> C intersection(Supplier<C> resultColl, @NonNull Collection<E> a, @NonNull Collection<E> b) {
        C result = resultColl.get();
        intersection(a, b).forEach(item -> result.add((E) item));
        return result;
    }

    /**
     * 求元素类型相同的两个集合的交集的补集((a ∪ b) - (a ∩ b)), 可指定结果容器类型
     *
     * @param resultColl 结果收集容器
     * @param a          数据源a
     * @param b          数据源b
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <E, C extends Collection<E>> C disjunction(Supplier<C> resultColl, @NonNull Collection<E> a, @NonNull Collection<E> b) {
        C result = resultColl.get();
        disjunction(a, b).forEach(item -> result.add((E) item));
        return result;
    }

    /**
     * 求元素类型相同的两个集合的差集(a - ( a ∩ b)), 可指定结果容器类型
     *
     * @param resultColl 结果收集容器
     * @param a          数据源a
     * @param b          数据源b
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <E, C extends Collection<E>> C subtract(Supplier<C> resultColl, @NonNull Collection<E> a, @NonNull Collection<E> b) {
        C result = resultColl.get();
        subtract(a, b).forEach(item -> result.add((E) item));
        return result;
    }

    /**
     * 对列表元素指定函数(字段为数字类型)按照comparator排列后, 求前k个元素的和
     *
     * @param coll                数据源
     * @param numberValueFunction 目标数值字段函数
     * @param comparator          排序比较器
     * @param k                   取前k个元素
     * @return
     */
    public static <F extends Number, T> BigDecimal sumTopK(@NonNull Collection<T> coll, Function<? super T, ? extends F> numberValueFunction, Comparator<F> comparator, int k) {
        if (k <= 0 || k > coll.size()) {
            k = coll.size();
        }
        double sumTopK = coll.stream()
                .filter(Objects::nonNull)
                .map(numberValueFunction)
                .sorted(comparator)
                .mapToDouble(Number::doubleValue)
                .limit(k)
                .sum();
        return BigDecimal.valueOf(sumTopK);
    }

    /**
     * 对列表元素指定函数(字段为数字类型)求和
     *
     * @param coll                数据源
     * @param numberValueFunction 目标数值字段函数
     * @return
     */
    public static <F extends Number, T> BigDecimal sum(@NonNull Collection<T> coll, Function<? super T, ? extends F> numberValueFunction) {
        double sum = coll.stream()
                .filter(Objects::nonNull)
                .map(numberValueFunction)
                .mapToDouble(Number::doubleValue)
                .sum();
        return BigDecimal.valueOf(sum);
    }

    /**
     * 将集合元素依次以指定方式合并, 返回最终结果
     * 常见的行为就是求和
     *
     * @param coll   数据源
     * @param merger 数据合并行为
     * @return
     */
    public static <E> E reduce(@NonNull Collection<E> coll, BinaryOperator<E> merger) {
        if (CollectionUtil.isEmpty(coll)) {
            return null;
        }
        return coll.stream().filter(Objects::nonNull).reduce(merger).orElse(null);
    }

    /**
     * 按照集合元素的某个字段依次以指定方式合并, 返回最终结果
     * 常见的行为就是求和
     *
     * @param coll        数据源
     * @param fieldMapper 元素字段函数
     * @param merger      数据合并行为
     * @return
     */
    public static <E, T> T reduceByField(@NonNull Collection<E> coll, Function<? super E, ? extends T> fieldMapper, BinaryOperator<T> merger) {
        if (CollectionUtil.isEmpty(coll)) {
            return null;
        }
        // reduce方法的第3个参数用于并行时结果合并, 对于单向流, 不需要合并故而写作 (a, b) -> null
        // 并且当使用并行流时, 一定要注意, 初始值initValue会参与每个并行的计算, 所以并行流下initValue必须为0或者null之类的
        // 故本方法进行数据聚合时初始值为null
        return coll.stream().filter(Objects::nonNull)
                .reduce(null,
                        (a, b) -> Objects.isNull(a) ? fieldMapper.apply(b) : merger.apply(a, fieldMapper.apply(b)),
                        (parallelResultA, parallelResultB) -> null
                );
    }

    /**
     * 根据合并key及合并方式数据进行汇聚
     * .           convert
     * +---------------------------------------------------------------------------------------------+
     * |                                                                                             v
     * +--------+  contains   +---------+  mergeKey   +---+  with T   +-----------------+  collect   +-------------------+
     * | source | ----------> |    E    | ----------> | K | --------> | Map.Entry<K, T> | ---------> | Result: Map<K, T> |
     * +--------+             +---------+             +---+           +-----------------+            +-------------------+
     * .                        |                                       ^
     * .                        | merger                                |
     * .                        v                                       |
     * .                      +---------+  with K                       |
     * .                      |    T    | ------------------------------+
     * .                      +---------+
     *
     * @param mapColl  结果容器
     * @param coll     数据源
     * @param mergeKey 合并数据的key, 也是元素的字段函数, 相同key的元素执行mergeHandler合并运算
     * @param merger   数据合并行为
     * @param <T>      元素类型
     * @param <K>      元素的字段Key类型
     * @param <M>      结果容器类型
     * @return
     */
    public static <T, K, M extends Map<K, T>> M reduceToMap(Supplier<M> mapColl, Collection<T> coll, Function<? super T, ? extends K> mergeKey, BinaryOperator<T> merger) {
        M result = mapColl.get();
        if (CollectionUtil.isEmpty(coll)) {
            return result;
        }
        return coll.stream().filter(Objects::nonNull)
                .reduce(result,
                        (preResult, e) -> {
                            K key = mergeKey.apply(e);
                            T old = preResult.get(key);
                            if (Objects.nonNull(old)) {
                                result.put(key, merger.apply(old, e));
                            }
                            return result;
                        },
                        (parallelResultA, parallelResultB) -> null
                );
    }

    /**
     * 先根据分类key字段对数据源分组, 再根据合并key及合并方式分别对每组数据进行汇聚
     * .                                                                                                                         convert to
     * .                                                                          +--------------------------------------------------------------+
     * .                                                                          |                                                              v
     * +--------+  classifyKeyMapper   +--------------------------+  1CK with   +---------+  contains   +---+  mergeKey   +----+  with T       +------------+
     * | source | -------------------> |     Map<CK, List<T>>     | ----------> | List<T> | ----------> | T | ----------> | MK | ------------> | Map<MK, T> |
     * +--------+                      +--------------------------+             +---------+             +---+             +----+               +------------+
     * .                                 |                                                                |                      with MK         ^
     * .                                 | all CK convert to                                              +--------------------------------------+
     * .                                 v
     * .                               +--------------------------+
     * .                               | Map<CK, Map<MK, List<T>> |
     * .                               +--------------------------+
     *
     * @param resultMapColl      结果容器
     * @param mergeResultMapColl 数据合并的结果
     * @param coll               数据源
     * @param classifyKeyMapper  分组key计算函数
     * @param mergeKeyMapper     数据合并key计算函数, 相同key的数据按照合并方式进行汇聚
     * @param merger             数据合并方式
     * @param <T>                元素类型
     * @param <CK>               分类key的类型
     * @param <MK>               合并key的类型
     * @return
     */
    public static <T, CK, MK, R extends Map<CK, Map<MK, T>>, G extends Map<MK, T>> Map<CK, Map<MK, T>> classifyAndReduceToNestedMap(Supplier<R> resultMapColl, Supplier<G> mergeResultMapColl, Collection<T> coll, Function<T, CK> classifyKeyMapper, Function<T, MK> mergeKeyMapper, BinaryOperator<T> merger) {
        Map<CK, List<T>> classifiedMap = CollectionUtil.groupIndexToMap(coll, classifyKeyMapper);
        return CollectionUtil.transToMapEnhance(
                resultMapColl,
                classifiedMap.entrySet(),
                Map.Entry::getKey,
                entry -> CollectionUtil.reduceToMap(mergeResultMapColl, entry.getValue(), mergeKeyMapper, merger)
        );
    }

    /**
     * 对列表元素指定函数(字段为数字类型)求平均
     *
     * @param coll                数据源
     * @param numberValueFunction 目标数值字段函数
     * @return
     */
    public static <F extends Number, T> BigDecimal average(@NonNull Collection<T> coll, Function<? super T, ? extends F> numberValueFunction) {
        Double average = coll.stream()
                .filter(Objects::nonNull)
                .map(numberValueFunction)
                .collect(Collectors.averagingDouble(Number::doubleValue));
        return BigDecimal.valueOf(average);
    }

    /**
     * 对列表元素指定函数(字段为数字类型), 求最大值
     *
     * @param coll                数据源
     * @param numberValueFunction 目标数值字段函数
     * @return
     */
    public static <F extends Number, T> BigDecimal max(@NonNull Collection<T> coll, Function<? super T, ? extends F> numberValueFunction) {
        double max = coll.stream()
                .filter(Objects::nonNull)
                .map(numberValueFunction)
                .mapToDouble(Number::doubleValue)
                .max()
                .orElse(0d);
        return BigDecimal.valueOf(max);
    }

    /**
     * 对列表元素指定函数(字段为数字类型), 求最小值
     *
     * @param coll                数据源
     * @param numberValueFunction 目标数值字段函数
     * @return
     */
    public static <F extends Number, T> BigDecimal min(@NonNull Collection<T> coll, Function<? super T, ? extends F> numberValueFunction) {
        double min = coll.stream()
                .filter(Objects::nonNull)
                .map(numberValueFunction)
                .mapToDouble(Number::doubleValue)
                .min()
                .orElse(0d);
        return BigDecimal.valueOf(min);
    }

    /**
     * 对集合元素指定字段检测重复, 返回非空重复元素
     *
     * @param coll   数据源
     * @param mapper 元素目标字段
     * @return
     */
    public static <T, F> Set<F> probeRepeatValueSet(Collection<T> coll, Function<? super T, ? extends F> mapper) {
        if (isEmpty(coll)) {
            return emptySet();
        }
        List<F> elementList = transToList(coll, mapper);
        List<F> withoutNullElementList = transToList(elementList, Function.identity());
        Set<F> noRepeatElementSet = new HashSet<>(withoutNullElementList);
        return subtract(HashSet::new, withoutNullElementList, noRepeatElementSet);
    }

    /**
     * 将按照分隔符固定拼接的字符串转换为指定[数字或其他类型]类型的List
     *
     * @param source      数据源
     * @param splitSymbol 分隔符
     * @param mapper      元素转换函数
     * @return
     */
    public static <T> List<T> splitDelimitedStringToList(@NonNull String source, @NonNull String splitSymbol, Function<String, T> mapper) {
        return transToList(Arrays.asList(source.split(splitSymbol)), mapper);
    }

    /**
     * Map判空
     *
     * @param sourceMap 数据源
     * @return boolean 判断结果
     */
    public static <K, V> boolean isEmpty(Map<K, V> sourceMap) {
        return Objects.isNull(sourceMap) || sourceMap.isEmpty();
    }

    public static <K, V> boolean isNotEmpty(Map<K, V> sourceMap) {
        return !isEmpty(sourceMap);
    }

    /**
     * 展示Map内容
     *
     * @param kvMap 数据源
     * @return
     */
    public static <K, V> String showMap(Map<K, V> kvMap) {
        if (isEmpty(kvMap)) {
            return SymbolConstantUtil.EMPTY;
        }
        List<String> entryShowList = transToList(
                kvMap.entrySet(),
                entry -> SymbolConstantUtil.LESS_THEN
                        + entry.getKey().toString()
                        + SymbolConstantUtil.NORMAL_ENGLISH_COMMA_DELIMITER
                        + (Objects.isNull(entry.getValue()) ? SymbolConstantUtil.NULL_SHOW : entry.getValue().toString())
                        + SymbolConstantUtil.GREATER
        );
        return show(entryShowList);
    }

    /**
     * 转换为Set, 底层默认使用HashSet
     *
     * @param source 数据源
     * @param mapper 字段执行函数
     * @return HashSet
     */
    public static <E, R> Set<R> transToSet(Collection<E> source, Function<? super E, ? extends R> mapper) {
        if (isEmpty(source)) {
            return emptySet();
        }
        return source.stream().filter(Objects::nonNull).map(mapper).collect(Collectors.toSet());
    }

    /**
     * 转换为List, 底层默认使用ArrayList
     *
     * @param source 数据源
     * @param mapper 字段执行函数
     * @return ArrayList
     */
    public static <E, R> List<R> transToList(Collection<E> source, Function<? super E, ? extends R> mapper) {
        if (isEmpty(source)) {
            return emptyList();
        }
        return source.stream().filter(Objects::nonNull).map(mapper).collect(Collectors.toList());
    }

    /**
     * 对数组元素进行转换, 支持结果容器类型, 常用Set/List，HashSet/ArrayList，LinkedSet/LinkedList
     *
     * @param resultColl
     * @param source
     * @param mapper
     * @param <T>
     * @param <F>
     * @param <C>
     * @return
     */
    public static <T, F, C extends Collection<F>> C transFromArray(Supplier<C> resultColl, T[] source, Function<? super T, ? extends F> mapper) {
        if (isEmpty(source)) {
            return resultColl.get();
        }
        return transToCollection(resultColl, Arrays.asList(source), mapper);
    }

    /**
     * 转换为指定的集合，常用Set/List，HashSet/ArrayList，LinkedSet/LinkedList
     *
     * @param resultColl 指定集合容器
     * @param source     数据源
     * @param mapper     字段执行函数
     * @return C
     */
    public static <E, R, C extends Collection<R>> C transToCollection(Supplier<C> resultColl, Collection<E> source, Function<? super E, ? extends R> mapper) {
        if (isEmpty(source)) {
            return resultColl.get();
        }
        return source.stream().filter(Objects::nonNull).map(mapper).collect(Collectors.toCollection(resultColl));
    }

    /**
     * 两层嵌套Collection, 内层为List, 折叠平铺为List, 底层默认使用ArrayList
     * 多层嵌套的可以通过重复调用此方法完成平铺
     *
     * @param source 两层嵌套Collection数据源
     * @param mapper 外层元素获取内Collection的执行函数
     * @return 平铺后收集到的List
     */
    public static <E, R> List<R> transToListWithFlatMap(Collection<E> source, Function<? super E, ? extends List<R>> mapper) {
        if (isEmpty(source)) {
            return emptyList();
        }
        return source.stream().filter(Objects::nonNull).map(mapper).flatMap(List::stream).collect(Collectors.toList());
    }

    /**
     * 两层嵌套Collection, 内层为List, 折叠平铺为Set去重, 底层默认使用HashSet
     * 多层嵌套的可以通过重复调用此方法完成平铺
     *
     * @param source 两层嵌套Collection数据源
     * @param mapper 外层元素获取内Collection的执行函数
     * @return 平铺后收集到的List
     */
    public static <E, R> Set<R> transToSetWithFlatMap(Collection<E> source, Function<? super E, ? extends List<R>> mapper) {
        if (isEmpty(source)) {
            return emptySet();
        }
        return source.stream().filter(Objects::nonNull).map(mapper).flatMap(List::stream).collect(Collectors.toSet());
    }

    /**
     * 两层嵌套Collection(内外层任意)折叠平铺到指定收集容器, 支持常用集合, 例如ArrayList, HashSet等
     * 多层嵌套的可以通过重复调用此方法完成平铺
     *
     * @param resultColl 结果收集容器
     * @param source     两层嵌套Collection数据源
     * @param mapper     外层元素获取内Collection的执行函数
     * @return 平铺后收集到的结果容器
     */
    public static <E, F, T extends Collection<F>, R extends Collection<F>> R transToCollWithFlatMap(Supplier<R> resultColl, Collection<E> source, Function<? super E, ? extends T> mapper) {
        if (isEmpty(source)) {
            return resultColl.get();
        }
        return source.stream().filter(Objects::nonNull).map(mapper).flatMap(Collection::stream).collect(Collectors.toCollection(resultColl));
    }

    /**
     * 两层嵌套Collection(内外层任意)折叠平铺到指定收集容器, 支持常用集合, 例如ArrayList, HashSet等
     * 多层嵌套的可以通过重复调用此方法完成平铺
     * 并支持元素转换
     *
     * @param resultColl    结果容器
     * @param source        两层嵌套Collection数据源
     * @param flatMapper    外层元素获取内Collection的执行函数
     * @param convertMapper 元素转换函数
     * @return 平铺后收集到的结果容器
     */
    public static <E, F, U, T extends Collection<F>, R extends Collection<U>> R enhanceTransToCollWithFlatMap(Supplier<R> resultColl, Collection<E> source, Function<? super E, ? extends T> flatMapper, Function<? super F, ? extends U> convertMapper) {
        if (isEmpty(source)) {
            return resultColl.get();
        }
        return source.stream().filter(Objects::nonNull).map(flatMapper).flatMap(Collection::stream).map(convertMapper).collect(Collectors.toCollection(resultColl));
    }

    /**
     * Collection<Map<K, V>折叠平铺到指定收集容器, 支持常用集合
     * <p>
     * .           convert
     * +--------------------------------------------------------+
     * |                                                        v
     * +--------+  contains   +---+  mapper   +-----+  reduce   +-----------+  convert   +-----------------+  flatmap   +-------------+
     * | source | ----------> | E | --------> | Map | --------> | List[Map] | ---------> | List[Map.Entry] | ---------> | Result: Map |
     * +--------+             +---+           +-----+           +-----------+            +-----------------+            +-------------+
     *
     * @param mapColl 结果收集容器
     * @param source  数据源
     * @param mapper  元素转换为Map的函数
     * @param <E>     数据源元数类型
     * @param <K>     Map中Key的类型
     * @param <V>     Map中Value的类型
     * @param <MS>    元素转换为Map的类型
     * @param <MR>    最终平铺收集容器Map的类型
     * @return
     */
    public static <E, K, V, MS extends Map<K, V>, MR extends Map<K, V>> MR transToMapWithFlatMap(Supplier<MR> mapColl, Collection<E> source, Function<? super E, MS> mapper) {
        if (isEmpty(source)) {
            return mapColl.get();
        }
        List<Map.Entry<K, V>> tmpEntryList = transToCollWithFlatMap(ArrayList::new, transToList(source, mapper), Map::entrySet);
        return transToMapEnhance(mapColl, tmpEntryList, Map.Entry::getKey, Map.Entry::getValue);
    }

    /**
     * 根据元素字段满足一定条件执行过滤, 并转换为Set
     *
     * @param coll      原始数据源
     * @param predicate 筛选条件
     * @param mapper    对筛选出元素进行计算的函数
     * @return HashSet
     */
    public static <F, T> Set<F> filterAndTransSet(Collection<T> coll, Predicate<? super T> predicate, Function<? super T, ? extends F> mapper) {
        if (isEmpty(coll)) {
            return emptySet();
        }
        return coll.stream().filter(Objects::nonNull).filter(predicate).map(mapper).collect(Collectors.toSet());
    }

    /**
     * 踢除满足条件removePredicate的元素字段 并转换为Set
     * 本方法与{@link #filterAndTransSet}筛选逻辑是相反的, 结果是互补的
     *
     * @param coll            原始数据源
     * @param removePredicate 筛选条件
     * @param mapper          对筛选出元素进行计算的函数
     * @return HashSet
     */
    public static <F, T> Set<F> removeAndTransSet(Collection<T> coll, Predicate<? super T> removePredicate, Function<? super T, ? extends F> mapper) {
        if (isEmpty(coll)) {
            return emptySet();
        }
        return coll.stream().filter(Objects::nonNull).filter(item -> !removePredicate.test(item)).map(mapper).collect(Collectors.toSet());
    }

    /**
     * 根据元素字段满足一定条件执行过滤, 并转换为List
     *
     * @param coll      原始数据源
     * @param predicate 筛选条件
     * @param mapper    对筛选出元素进行计算的函数
     * @return ArrayList
     */
    public static <F, T> List<F> filterAndTransList(Collection<T> coll, Predicate<? super T> predicate, Function<? super T, ? extends F> mapper) {
        if (isEmpty(coll)) {
            return emptyList();
        }
        return coll.stream().filter(Objects::nonNull).filter(predicate).map(mapper).collect(Collectors.toList());
    }

    /**
     * 踢除满足条件removePredicate的元素字段 并转换为List
     * 本方法与{@link #filterAndTransList}筛选逻辑是相反的, 结果是互补的
     *
     * @param coll            原始数据源
     * @param removePredicate 筛选条件
     * @param mapper          对筛选出元素进行计算的函数
     * @return ArrayList
     */
    public static <F, T> List<F> removeAndTransList(Collection<T> coll, Predicate<? super T> removePredicate, Function<? super T, ? extends F> mapper) {
        if (isEmpty(coll)) {
            return emptyList();
        }
        return coll.stream().filter(Objects::nonNull).filter(item -> !removePredicate.test(item)).map(mapper).collect(Collectors.toList());
    }

    /**
     * 根据元素字段满足一定条件执行过滤, 并转换为指定集合
     *
     * @param resultColl       结果收集容器
     * @param source           原始数据源
     * @param survivePredicate 保留条件
     * @param mapper           对筛选出元素进行计算的函数
     * @return R
     */
    public static <T, F, R extends Collection<F>> R filterAndTransColl(Supplier<R> resultColl, Collection<T> source, Predicate<? super T> survivePredicate, Function<? super T, ? extends F> mapper) {
        if (isEmpty(source)) {
            return resultColl.get();
        }
        return source.stream().filter(Objects::nonNull).filter(survivePredicate).map(mapper).collect(Collectors.toCollection(resultColl));
    }

    /**
     * 踢除满足条件removePredicate的元素字段 并转换为List
     * 本方法与{@link #filterAndTransColl}筛选逻辑是相反的, 结果是互补的
     *
     * @param resultColl      结果收集容器
     * @param source          原始数据源
     * @param removePredicate 剔除条件
     * @param mapper          对筛选出元素进行计算的函数
     * @return R
     */
    public static <T, F, R extends Collection<F>> R removeAndTransColl(Supplier<R> resultColl, Collection<T> source, Predicate<? super T> removePredicate, Function<? super T, ? extends F> mapper) {
        if (isEmpty(source)) {
            return resultColl.get();
        }
        return source.stream().filter(Objects::nonNull).filter(item -> !removePredicate.test(item)).map(mapper).collect(Collectors.toCollection(resultColl));
    }

    /**
     * 踢除满足条件removePredicate的元素字段 并转换为Array
     * 本方法与{@link #filterAndTransArray}筛选逻辑是相反的, 结果是互补的
     *
     * @param coll            原始数据源
     * @param predicate       筛选条件
     * @param mapper          对筛选出元素进行计算的函数
     * @param resultGenerator 数组收集容器
     * @return Array
     */
    public static <F, T> F[] removeAndTransArray(Collection<T> coll, Predicate<? super T> predicate, Function<? super T, ? extends F> mapper, IntFunction<F[]> resultGenerator) {
        if (isEmpty(coll)) {
            return null;
        }
        return coll.stream().filter(Objects::nonNull).filter(predicate).map(mapper).toArray(resultGenerator);
    }

    /**
     * 根据元素字段满足一定条件执行过滤, 并转换为Array
     *
     * @param coll            原始数据源
     * @param predicate       筛选条件
     * @param mapper          对筛选出元素进行计算的函数
     * @param resultGenerator 数组收集容器
     * @return Array
     */
    public static <F, T> F[] filterAndTransArray(Collection<T> coll, Predicate<? super T> predicate, Function<? super T, ? extends F> mapper, IntFunction<F[]> resultGenerator) {
        if (isEmpty(coll)) {
            // 对于数组而言, null跟空数组 resultGenerator.apply(0)效果一样, 更简洁
            return null;
        }
        return coll.stream().filter(Objects::nonNull).filter(predicate).map(mapper).toArray(resultGenerator);
    }

    /**
     * 获取元素的某个字段集合, 并去重
     *
     * @param source 数据源
     * @param mapper 字段执行函数
     * @return
     */
    public static <E, R> List<R> distinctList(Collection<E> source, Function<? super E, ? extends R> mapper) {
        if (isEmpty(source)) {
            return emptyList();
        }
        return source.stream().filter(Objects::nonNull).map(mapper).distinct().collect(Collectors.toList());
    }

    /**
     * 根据集合元素中指定字段进行去重，返回去重后的元素集合
     *
     * @param coll               数据源
     * @param distinctComparator 元素字段比较器(可以是多个字段的联合比较器)
     * @return 去重后的原始元素集合
     */
    public static <T> List<T> distinctListByField(Collection<T> coll, Comparator<T> distinctComparator) {
        if (isEmpty(coll)) {
            return emptyList();
        }
        return coll.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(distinctComparator)), ArrayList::new));
    }

    /**
     * 分组转换为Map<K, List<V>>，底层默认HashMap<K, ArrayList<V>>
     *
     * @param source    数据源
     * @param kFunction key执行函数
     * @return
     */
    public static <E, K> Map<K, List<E>> groupIndexToMap(Collection<E> source, Function<? super E, ? extends K> kFunction) {
        if (isEmpty(source)) {
            return emptyMap();
        }
        return source.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(kFunction));
    }

    /**
     * 分组转换为Map<K, List<V>>, 支持key函数, value函数, 底层默认HashMap<K, ArrayList<V>>
     *
     * @param source    数据源
     * @param kFunction key执行函数
     * @param vFunction value执行函数
     * @return
     */
    public static <E, K, V> Map<K, List<V>> groupIndexToMap(Collection<E> source, Function<? super E, ? extends K> kFunction, Function<? super E, ? extends V> vFunction) {
        if (isEmpty(source)) {
            return emptyMap();
        }
        return source.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(kFunction, HashMap::new, Collectors.mapping(vFunction, Collectors.toList())));
    }

    /**
     * 分组转换为Map<K, List<V>>, 支持key函数, value函数, 底层默认HashMap<K, ArrayList<V>>
     * Map中会包含所有的key, 对应的List<V>可能为空列表(非null)
     *
     * @param source              数据源
     * @param adjustmentReferKeys 校准参考key集合
     * @param kFunction           分组key执行函数
     * @param vFunction           分组值执行函数
     * @param <E>
     * @param <K>
     * @param <V>
     * @return
     */
    public static <E, K, V> Map<K, List<V>> groupIndexToMap(Collection<E> source, Set<K> adjustmentReferKeys, Function<? super E, ? extends K> kFunction, Function<? super E, ? extends V> vFunction) {
        if (isEmpty(source)) {
            return emptyMap();
        }
        Map<K, List<V>> resultMap = source.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(kFunction, HashMap::new, Collectors.mapping(vFunction, Collectors.toList())));
        if (isNotEmpty(adjustmentReferKeys)) {
            adjustmentReferKeys.forEach(k -> resultMap.putIfAbsent(k, emptyList()));
        }
        return resultMap;
    }

    /**
     * 分组转换为指定的Map<K, List<V>>， 例如TreeMap<K, List<V>>/LinkedHashMap<K, List<V>>
     *
     * @param mapColl   结果收集容器
     * @param source    数据源
     * @param kFunction key执行函数
     * @return
     */
    public static <E, K, M extends Map<K, List<E>>> M groupIndexToMap(Supplier<M> mapColl, Collection<E> source, Function<? super E, ? extends K> kFunction) {
        if (isEmpty(source)) {
            return mapColl.get();
        }
        return source.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(kFunction, mapColl, Collectors.toList()));
    }

    /**
     * 分组转换为指定的Map<K, List<V>>， 例如TreeMap<K, List<V>>/LinkedHashMap<K, List<V>>
     * 并且支持List按照指定字段排序
     *
     * @param mapColl        结果收集容器
     * @param source         数据源
     * @param kGroupFunction key分组执行函数
     * @param sortFunction   排序函数
     * @param vFunction      value执行函数
     * @return
     */
    @ImportDoc("数据源分组排序利器, 推荐使用")
    public static <E, K, S extends Comparable<? super S>, V, M extends Map<K, List<V>>> M groupIndexAndSortToMap(Supplier<M> mapColl, Collection<E> source, Function<? super E, ? extends K> kGroupFunction, Function<? super E, ? extends S> sortFunction, Function<? super E, ? extends V> vFunction) {
        if (isEmpty(source)) {
            return mapColl.get();
        }
        return source.stream().filter(Objects::nonNull).sorted(useCustomFieldComparator(sortFunction, Comparator.comparing(Function.identity()))).collect(Collectors.groupingBy(kGroupFunction, mapColl, Collectors.mapping(vFunction, Collectors.toList())));
    }

    /**
     * 分组转换为指定Map<K, 指定的List<V>>，例如TreeMap<K, LinkedList<V>>/LinkedHashMap<K, LinkedList<V>>
     *
     * @param mapColl   结果收集容器
     * @param vColl     Map结果中value的收集容器
     * @param source    数据源
     * @param kFunction key执行函数
     */
    public static <E, K, M extends Map<K, C>, C extends Collection<E>> M groupIndexToMap(Supplier<M> mapColl, Supplier<C> vColl, Collection<E> source, Function<? super E, ? extends K> kFunction) {
        if (isEmpty(source)) {
            return mapColl.get();
        }
        return source.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(kFunction, mapColl, Collectors.toCollection(vColl)));
    }

    /**
     * 分组转换为指定Map<K, 指定的List<V>>，例如TreeMap<K, LinkedList<V>>/LinkedHashMap<K, LinkedList<V>>
     * 并且可对原始数组元素进行计算(转化)为其他对象
     *
     * @param mapColl        结果收集容器
     * @param vColl          Map结果中value的收集容器
     * @param source         数据源
     * @param kGroupFunction key执行函数
     * @param vFunction      value执行函数
     * @return
     */
    public static <E, K, V, M extends Map<K, C>, C extends Collection<V>> M groupIndexToMap(Supplier<M> mapColl, Supplier<C> vColl, Collection<E> source, Function<? super E, ? extends K> kGroupFunction, Function<? super E, ? extends V> vFunction) {
        if (isEmpty(source)) {
            return mapColl.get();
        }
        return source.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(kGroupFunction, mapColl, Collectors.mapping(vFunction, Collectors.toCollection(vColl))));
    }

    /**
     * 分组转换为指定Map<K, 指定的List<V>>，例如TreeMap<K, LinkedList<V>>/LinkedHashMap<K, LinkedList<V>>
     * 并且可对原始数组元素进行计算(转化)为其他对象
     * Map中会包含adjustmentReferKeys中所有的key, 对应的List<V>可能为空列表(非null)
     *
     * @param mapColl             map收集容器
     * @param vColl               value收集容器
     * @param source              数据源
     * @param adjustmentReferKeys 校准参考key集合
     * @param kGroupFunction      分组key执行函数
     * @param vFunction           分组值执行函数
     * @return
     */
    public static <E, K, V, M extends Map<K, C>, C extends Collection<V>> M groupIndexToMap(Supplier<M> mapColl, Supplier<C> vColl, Collection<E> source, Set<K> adjustmentReferKeys, Function<? super E, ? extends K> kGroupFunction, Function<? super E, ? extends V> vFunction) {
        if (isEmpty(source)) {
            return mapColl.get();
        }
        M resultMap = source.stream().filter(Objects::nonNull).collect(
                Collectors.groupingBy(kGroupFunction, mapColl, Collectors.mapping(vFunction, Collectors.toCollection(vColl))));
        if (isNotEmpty(adjustmentReferKeys)) {
            adjustmentReferKeys.forEach(k -> resultMap.putIfAbsent(k, vColl.get()));
        }
        return resultMap;
    }

    // ====================================================================================================
    // 为避免数据丢失，Steam API底层对Collectors.toMap做了较为硬性的要求
    // 1.toMap首先不允许key重复， 因而分组时需要注意使用KEY字段
    // 2.value不允许为null
    // 因而，以下*ToMap方法在使用时请注意以上两条，而*ToMapEnhance允许key重复，并启用新值替换旧值的机制
    // ====================================================================================================

    /**
     * 转换为Map-Value
     * {@link #transToMap}
     *
     * @param values    数据源
     * @param kFunction key执行函数
     * @return
     */
    @Deprecated
    public static <E, K> Map<K, E> transToMap(Iterable<E> values, Function<? super E, ? extends K> kFunction) {
        if (Objects.isNull(values)) {
            return emptyMap();
        }
        return StreamSupport.stream(values.spliterator(), Boolean.FALSE)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(kFunction, Function.identity()));
    }

    /**
     * 转换为Map-Value
     *
     * @param source    数据源
     * @param kFunction key执行函数
     * @return
     */
    public static <E, K> Map<K, E> transToMap(Collection<E> source, Function<? super E, ? extends K> kFunction) {
        if (isEmpty(source)) {
            return emptyMap();
        }
        return source.stream().filter(Objects::nonNull).collect(Collectors.toMap(kFunction, Function.identity()));
    }

    /**
     * 转换为Map-Value, 重复KEY将抛出异常
     *
     * @param mapColl   结果收集容器
     * @param source    数据源
     * @param kFunction key执行函数
     * @return
     */
    public static <E, K, M extends Map<K, E>> M transToMap(Supplier<M> mapColl, Collection<E> source, Function<? super E, ? extends K> kFunction) {
        if (isEmpty(source)) {
            return mapColl.get();
        }
        return source.stream().filter(Objects::nonNull).collect(Collectors.toMap(kFunction, Function.identity(), nonDuplicateKey(), mapColl));
    }


    /**
     * 转换为Map-Value, 允许重复KEY
     *
     * @param mapColl   结果收集容器
     * @param source    数据源
     * @param kFunction key执行函数
     * @return
     */
    public static <E, K, M extends Map<K, E>> M transToMapEnhance(Supplier<M> mapColl, Collection<E> source, Function<? super E, ? extends K> kFunction) {
        if (isEmpty(source)) {
            return mapColl.get();
        }
        return source.stream().filter(Objects::nonNull).collect(Collectors.toMap(kFunction, Function.identity(), enableNewOnDuplicateKey(), mapColl));
    }

    /**
     * 转换为Map-Value, 重复KEY时新值覆盖旧值
     *
     * @param mapColl   支持返回LinkedHashMap/HashMap
     * @param source
     * @param kFunction
     * @param vFunction
     * @return
     */
    public static <E, K, V, M extends Map<K, V>> M transToMapEnhance(Supplier<M> mapColl, Iterable<E> source, Function<? super E, ? extends K> kFunction, Function<? super E, ? extends V> vFunction) {
        if (Objects.isNull(source)) {
            return mapColl.get();
        }
        return StreamSupport.stream(source.spliterator(), Boolean.FALSE)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(kFunction, vFunction, (oldValue, newValue) -> newValue, mapColl));
    }

    /**
     * 转换为Map-Value
     *
     * @param source    数据源
     * @param kFunction key执行函数
     * @param vFunction value执行函数
     * @return
     */
    public static <E, K, V> Map<K, V> transToMap(Collection<E> source, Function<? super E, ? extends K> kFunction, Function<? super E, ? extends V> vFunction) {
        if (isEmpty(source)) {
            return emptyMap();
        }
        return source.stream().filter(Objects::nonNull).collect(Collectors.toMap(kFunction, vFunction));
    }

    /**
     * 转换为Map-Value, 重复KEY将抛出异常
     *
     * @param mapColl   结果收集容器
     * @param source    数据源
     * @param kFunction key执行函数
     * @param vFunction value执行函数
     * @return
     */
    public static <E, K, V, M extends Map<K, V>> M transToMap(Supplier<M> mapColl, Collection<E> source, Function<? super E, ? extends K> kFunction, Function<? super E, ? extends V> vFunction) {
        if (isEmpty(source)) {
            return mapColl.get();
        }
        return source.stream().filter(Objects::nonNull).collect(Collectors.toMap(kFunction, vFunction, nonDuplicateKey(), mapColl));
    }

    /**
     * 转换为Map-Value, 重复KEY将抛出异常
     * 支持参考校准
     * 示例: adjustmentReferKeys有5个id, 但是source中只有4个id对应的数据
     * 返回Map.keys中仍然保持5个id, 缺少的id对应的value为空列表
     *
     * @param mapColl             结果收集容器
     * @param vColl               Map结果中value的收集容器
     * @param source              数据源
     * @param adjustmentReferKeys 校准参考key集合
     * @param kFunction           key执行函数
     * @param vFunction           value执行函数
     */
    public static <E, K, V, C extends Collection<V>, M extends Map<K, C>> M transToMap(Supplier<M> mapColl, Supplier<C> vColl, Collection<E> source, Set<K> adjustmentReferKeys, Function<? super E, ? extends K> kFunction, Function<? super E, ? extends C> vFunction) {
        if (isEmpty(source)) {
            return mapColl.get();
        }
        M resultMap = source.stream().filter(Objects::nonNull).collect(Collectors.toMap(kFunction, vFunction, nonDuplicateKey(), mapColl));
        if (isNotEmpty(adjustmentReferKeys)) {
            adjustmentReferKeys.forEach(k -> resultMap.putIfAbsent(k, vColl.get()));
        }
        return resultMap;
    }

    /**
     * 可以将两层嵌套集合，转换为Map<K, Collection<V>>，按照K叠加Collection<V>
     *
     * @param mapColl   结果Map收集容器
     * @param source    数据源
     * @param kFunction key执行函数, 决定结果Map的Key的数据类型
     * @param vFunction value执行函数, 决定结果Map的value的数据类型
     * @return
     */
    public static <E, K, V, C extends Collection<V>, M extends Map<K, C>> M transToMapByMerge(Supplier<M> mapColl, Collection<E> source, Function<? super E, ? extends K> kFunction, Function<? super E, ? extends C> vFunction) {
        if (isEmpty(source)) {
            return mapColl.get();
        }
        return source.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        kFunction,
                        vFunction,
                        (collA, collB) -> {
                            collA.addAll(collB);
                            return collA;
                        },
                        mapColl)
                );
    }

    /**
     * 按照指定分隔符将数组元素拼接为字符串
     *
     * @param arr       数组数据源
     * @param separator 分隔符
     * @return
     */
    public static String join(Object[] arr, String separator) {
        if (isEmpty(arr)) {
            return StringUtils.EMPTY;
        }
        return join(Arrays.asList(arr), separator);
    }

    /**
     * 按照指定分隔符将数组元素拼接为字符串
     *
     * @param coll      数据源
     * @param separator 分隔符
     * @return
     */
    public static <T> String join(Collection<T> coll, String separator) {
        if (isEmpty(coll)) {
            return StringUtils.EMPTY;
        }
        if (Objects.isNull(separator)) {
            separator = StringUtils.EMPTY;
        }
        return coll.stream().map(e -> Objects.nonNull(e) ? e.toString() : SymbolConstantUtil.NULL_SHOW).collect(Collectors.joining(separator));
    }

    /**
     * 展示集合
     *
     * @param coll 数据源
     * @return
     */
    public static <T> String show(Collection<T> coll) {
        return join(coll, SymbolConstantUtil.NORMAL_ENGLISH_COMMA_DELIMITER);
    }

    /**
     * 根据Key函数对Map排序
     *
     * @param sourceMap Map数据源
     * @param keyMapper key执行函数, 根据key进行运算后的结果进行排序
     * @param reverse   可选参数, 是否根据Key逆序排列, 默认增序排列
     * @return
     */
    public static <K, T extends Comparable<? super T>, V> LinkedHashMap<K, V> sortByKeyMapper(Map<K, V> sourceMap, Function<? super K, ? extends T> keyMapper, boolean... reverse) {
        Map<T, K> kMap = transToMap(sourceMap.keySet(), keyMapper);
        LinkedHashMap<T, K> sortedMap = sortByKey(kMap, reverse);
        return sortedMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, entry -> sourceMap.get(entry.getValue()), (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    /**
     * 根据Key对Map排序
     *
     * @param sourceMap Map数据源
     * @param reverse   可选参数, 是否根据Key逆序排列, 默认增序排列
     * @return
     */
    public static <K extends Comparable<? super K>, V> LinkedHashMap<K, V> sortByKey(Map<K, V> sourceMap, boolean... reverse) {
        if (isEmpty(sourceMap)) {
            return new LinkedHashMap<>(2, 0.5f, Boolean.FALSE);
        }
        Comparator<Map.Entry<K, V>> comparingByKey = Map.Entry.comparingByKey();
        if (isNotEmpty(reverse) && reverse[0]) {
            comparingByKey = comparingByKey.reversed();
        }
        return sortMap(sourceMap, comparingByKey);
    }

    /**
     * 根据Value对Map排序
     *
     * @param sourceMap Map数据源
     * @param reverse   可选参数, 是否根据Value逆序排列, 默认增序排列
     * @return
     */
    public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue(Map<K, V> sourceMap, boolean... reverse) {
        if (isEmpty(sourceMap)) {
            return new LinkedHashMap<>(2, 0.5f, Boolean.FALSE);
        }
        Comparator<Map.Entry<K, V>> comparingByValue = Map.Entry.comparingByValue();
        if (isNotEmpty(reverse) && reverse[0]) {
            comparingByValue = comparingByValue.reversed();
        }
        return sortMap(sourceMap, comparingByValue);
    }

    /**
     * Find a value in a array, normally used in Enum class
     * <p>
     * .                                                                         no matching then return null
     * .                                                                 +-----------------------------------------+
     * .                                                                 |                                         v
     * .+--------+  foreach   +---+  mapper   +---+  check predicate   +-------+  if match return immediately    +-----------+
     * .| source | ---------> | T | --------> | F | -----------------> | JUDGE | ------------------------------> | Result: F |
     * .+--------+            +---+           +---+                    +-------+                                 +-----------+
     *
     * @param source    数组数据源
     * @param function  元素计算值函数
     * @param predicate 目标值条件
     * @return
     */
    public static <T, F> F findAnyMappedResultInArrays(T[] source, Function<? super T, ? extends F> function, Predicate<? super F> predicate) {
        if (isEmpty(source)) {
            return null;
        }
        return findAnyMappedResult(Arrays.asList(source), function, predicate);
    }

    /**
     * Find a value in a array, normally used in Enum class
     *
     * @param source    数组数据源
     * @param function  元素计算值函数
     * @param predicate 目标值条件
     * @return
     */
    public static <T, F> T findAnyInArrays(T[] source, Function<? super T, ? extends F> function, Predicate<? super F> predicate) {
        if (isEmpty(source)) {
            return null;
        }
        return findAny(Arrays.asList(source), function, predicate);
    }

    /**
     * Find a value in a array, normally used in Enum class
     *
     * @param source   数组数据源
     * @param function 元素计算值函数
     * @param value    目标值
     * @return
     */
    public static <T, F> T findAnyInArrays(T[] source, Function<? super T, ? extends F> function, @NonNull F value) {
        if (isEmpty(source)) {
            return null;
        }
        return findAny(Arrays.asList(source), function, value);
    }

    /**
     * 在coll中依次执行指定字段(函数)mapper, 再根据predicate进行匹配, 匹配任意一个就返回经mapper后的结果, 找不到就返回null
     * .                                                                       no matching return null
     * .                                                               +--------------------------------------------+
     * .                                                               |                                            v
     * .+------+  foreach   +---+  mapper   +---+  check predicate   +-------+  matched then return immediately   +--------+
     * .| coll | ---------> | T | --------> | F | -----------------> | JUDGE | ---------------------------------> | Result |
     * .+------+            +---+           +---+                    +-------+                                    +--------+
     *
     * @param coll
     * @param mapper
     * @param predicate
     * @param <T>
     * @param <F>
     * @return
     */
    public static <T, F> F findAnyMappedResult(Collection<T> coll, Function<? super T, ? extends F> mapper, Predicate<? super F> predicate) {
        if (isEmpty(coll)) {
            return null;
        }
        return coll.stream().filter(Objects::nonNull).map(mapper).filter(predicate).findAny().orElse(null);
    }

    /**
     * 在coll中依次执行指定字段(函数)mapper, 再根据predicate进行匹配, 匹配任意一个就返回原始元素, 找不到就返回null
     *
     * @param coll
     * @param mapper
     * @param predicate
     * @param <T>
     * @param <F>
     * @return
     */
    public static <T, F> T findAny(Collection<T> coll, Function<? super T, ? extends F> mapper, Predicate<? super F> predicate) {
        if (isEmpty(coll)) {
            return null;
        }
        return coll.stream().filter(Objects::nonNull).filter(item -> predicate.test(mapper.apply(item))).findAny().orElse(null);
    }

    /**
     * 在coll中根据指定字段(函数)查找元素, 找到任意一个就返回, 找不到就返回null
     *
     * @param coll     数据源
     * @param function 元素计算值函数
     * @param value    目标值
     * @return
     */
    public static <T, F> T findAny(Collection<T> coll, Function<? super T, ? extends F> function, @NonNull F value) {
        if (isEmpty(coll)) {
            return null;
        }
        return coll.stream().filter(item -> value.equals(function.apply(item))).findAny().orElse(null);
    }

    /**
     * 获取集合元首(第一个元素), 避免get(0)
     *
     * @param source
     * @param <T>
     */
    @AttentionDoc("推荐使用, 替代x.get(0)的写法")
    public static <T> T getHeadMan(Collection<T> source) {
        return isEmpty(source) ? null : source.iterator().next();
    }

    /**
     * 在List中根据自定字段(函数)查找元素，返回找到的第一个元素，找不到就返回null
     *
     * @param coll     数据源
     * @param function 元素计算值函数
     * @param value    目标值
     * @return
     */
    public static <T, F> T findFirst(Collection<T> coll, Function<? super T, ? extends F> function, @NonNull F value) {
        if (isEmpty(coll)) {
            return null;
        }
        return coll.stream().filter(item -> value.equals(function.apply(item))).findFirst().orElse(null);
    }

    /**
     * 根据指定条件查找元素，返回找到的第一个非null元素，找不到就返回null
     * 不支持查找null元素
     *
     * @param coll      数据源
     * @param predicate 查找条件
     * @return
     */
    public static <T> T findFirst(Collection<T> coll, Predicate<? super T> predicate) {
        if (isEmpty(coll)) {
            return null;
        }
        return coll.stream().filter(Objects::nonNull).filter(predicate).findFirst().orElse(null);
    }

    /**
     * 从list里根据唯一字段值 查找所有满足条件不为Null的元素
     *
     * @param coll     数据源
     * @param function 元素计算值函数
     * @param value    目标值
     * @return
     */
    public static <T, F> List<T> findAll(Collection<T> coll, Function<? super T, ? extends F> function, @NonNull F value) {
        if (isEmpty(coll)) {
            return emptyList();
        }
        return coll.stream()
                .filter(Objects::nonNull)
                .filter(item -> value.equals(function.apply(item)))
                .collect(Collectors.toList());
    }

    /**
     * 从list里根据唯一字段值 查找所有满足条件不为Null的元素
     *
     * @param coll      数据源
     * @param predicate 查找条件
     * @return
     */
    public static <T> List<T> findAll(Collection<T> coll, Predicate<? super T> predicate) {
        if (isEmpty(coll)) {
            return emptyList();
        }
        return coll.stream().filter(Objects::nonNull).filter(predicate).collect(Collectors.toList());
    }

    /**
     * 判断元素在list中存在至少一个值，存在就立马返回
     *
     * @param coll     数据源
     * @param function 元素计算值函数
     * @param value    目标值
     * @return
     */
    public static <T, F> boolean existAtLeastOne(Collection<T> coll, Function<? super T, ? extends F> function, @NonNull F value) {
        if (isEmpty(coll)) {
            return false;
        }
        return coll.stream().anyMatch(item -> value.equals(function.apply(item)));
    }

    /**
     * 判断元素在list中是否存在
     *
     * @param coll     数据源
     * @param function 元素计算值函数
     * @param value    目标值
     * @return
     */
    public static <T, F> boolean exist(Collection<T> coll, Function<? super T, ? extends F> function, @NonNull F value) {
        if (isEmpty(coll)) {
            return false;
        }
        return coll.stream().allMatch(item -> value.equals(function.apply(item)));
    }

    /**
     * 判断非null元素在list中是否存在
     *
     * @param coll      数据源
     * @param predicate 查找条件
     */
    public static <T> boolean exist(Collection<T> coll, Predicate<? super T> predicate) {
        if (isEmpty(coll)) {
            return false;
        }
        T existFirstOne = coll.stream().filter(predicate).findFirst().orElse(null);
        return Objects.nonNull(existFirstOne);
    }

    /**
     * 集合只有一个元素
     *
     * @param coll
     * @param <T>
     * @return
     */
    public static <T> boolean onlyExistOne(Collection<T> coll) {
        return isNotEmpty(coll) && coll.size() == 1;
    }

    /**
     * 集合最多只有一个元素
     *
     * @param coll
     * @param <T>
     * @return
     */
    public static <T> boolean mostExistOne(Collection<T> coll) {
        return isNotEmpty(coll) || coll.size() == 1;
    }

    /**
     * Map键值对反转
     * 示例,
     * { examA : [stu1, stu2, stu3], examB: [stu1, stu2] }
     * ⬇
     * {stu1 : [examA, examB], stu2 : [examA, examB], stu3 : [examA]}
     * <p>
     * .+--------------------------+  KV reverse   +--------------------------+
     * .| Map<Exam, List<Student>> | ------------> | Map<Student, List<Exam>> |
     * .+--------------------------+               +--------------------------+
     *
     * @param sourceMap Map数据源
     * @param kFunction 针对Map数据源key的计算函数
     * @param vFunction 针对Map数据源value的计算函数
     * @return
     */
    public static <E1, E2, K1, K2> Map<K2, List<E2>> reverseKV(@NonNull Map<K1, List<E1>> sourceMap, Function<? super K1, ? extends E2> kFunction, Function<? super E1, ? extends K2> vFunction) {
        Map<K2, List<E2>> aux = new HashMap<>(16, 0.75f);
        sourceMap.entrySet().stream()
                .filter(Objects::nonNull)
                .forEach(entry -> entry.getValue().stream()
                        .filter(Objects::nonNull)
                        .forEach(v -> aux.computeIfAbsent(vFunction.apply(v), init -> new ArrayList<>()).add(kFunction.apply(entry.getKey())))
                );
        return aux;
    }

    /**
     * Map键值对反转，支持返回结果自定义收集容器
     * 例如返回LinkedHashMap<K, LinkedList<V>
     * <p>
     * Map<k1, Coll_1<v1>>  ==>  Map<k2, Coll_1<v2>>
     * kFunction.apply(k1) ==> v2
     * vFunction.apply(v1) ==> k2
     *
     * @param mapColl   结果收集容器
     * @param vColl     Map结果中value的收集容器
     * @param sourceMap Map数据源
     * @param kFunction 针对Map数据源key的计算函数
     * @param vFunction 针对Map数据源value的计算函数
     * @return
     */
    public static <V1, V2, K1, K2, C1 extends Collection<V1>, C2 extends Collection<V2>, M1 extends Map<K1, C1>, M2 extends Map<K2, C2>>
    M2 reverseKV(Supplier<M2> mapColl, Supplier<C2> vColl, @NonNull M1 sourceMap, Function<? super K1, ? extends V2> kFunction, Function<? super V1, ? extends K2> vFunction) {
        M2 aux = mapColl.get();
        sourceMap.entrySet().stream()
                .filter(Objects::nonNull)
                .forEach(entry -> entry.getValue().stream()
                        .filter(Objects::nonNull)
                        .forEach(v -> aux.computeIfAbsent(vFunction.apply(v), init -> vColl.get()).add(kFunction.apply(entry.getKey())))
                );
        return aux;
    }

    /**
     * 针对复杂Map中，查找key匹配函数的键值对集合
     * 不满足匹配函数条件时返回空
     *
     * @param sourceMap 数据源
     * @param kFunction 目标key的计算函数
     * @param value     目标值
     * @return
     */
    public static <K, V, T> List<Map.Entry<K, V>> findInMap(Map<K, V> sourceMap, Function<? super K, ? extends T> kFunction, @NonNull T value) {
        if (isEmpty(sourceMap)) {
            return null;
        }
        return sourceMap.entrySet().stream()
                .filter(Objects::nonNull)
                .map(entry -> {
                    if (value.equals(kFunction.apply(entry.getKey()))) {
                        return entry;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 针对复杂Map中，查找key匹配函数的键值对，只取一个
     * 不满足匹配函数条件时返回null
     *
     * @param sourceMap 数据源
     * @param kFunction 目标key的计算函数
     * @param value     目标值
     * @return
     */
    public static <K, V, T> Map.Entry<K, V> findOneInMap(Map<K, V> sourceMap, Function<? super K, ? extends T> kFunction, @NonNull T value) {
        if (isEmpty(sourceMap)) {
            return null;
        }
        return sourceMap.entrySet().stream()
                .filter(Objects::nonNull)
                .filter(entry -> value.equals(kFunction.apply(entry.getKey())))
                .findAny()
                .orElse(null);
    }

    /**
     * 针对复杂Map中，查找key匹配函数的键值对，只取一个
     * 不满足匹配函数条件时返回null
     *
     * @param sourceMap 数据源
     * @param kFunction 目标key的计算函数
     * @param value     目标值
     * @return
     */
    public static <K, V, T> V findOneValue(Map<K, V> sourceMap, Function<? super K, ? extends T> kFunction, @NonNull T value) {
        Map.Entry<K, V> resultEntry = findOneInMap(sourceMap, kFunction, value);
        return Objects.isNull(resultEntry) ? null : resultEntry.getValue();
    }

    /**
     * 剔除请求中值为null的参数
     *
     * @param paramsMap 参数数据源
     * @return
     */
    public static Map<String, Object> removeNullElement(Map<String, Object> paramsMap) {
        if (isEmpty(paramsMap)) {
            return paramsMap;
        }
        // 一般请求参数不会太多，故而使用单向顺序流即可
        // 1.首先构建流，剔除值为空的元素
        Stream<Map.Entry<String, Object>> tempStream = paramsMap.entrySet().stream()
                .filter((entry) -> entry.getValue() != null);
        // 2.从流中恢复map
        return tempStream.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * 从Map中移除类型为文件MultipartFile/File的元素
     *
     * @param paramsMap  参数数据源
     * @param clazzArray 目标类
     * @return
     */
    public static Map<String, Object> removeSpecifiedElement(Map<String, Object> paramsMap, Class<?>[] clazzArray) {
        if (isEmpty(paramsMap) || isEmpty(clazzArray)) {
            return paramsMap;
        }
        Map<String, Object> resultMap = emptyMap();
        // 将流导入Supplier工厂, 需要时即取出来, 取出来时就会构造流, 即新的实例
        Supplier<Stream<Class>> clazzStreamSupplier = () -> Arrays.stream(clazzArray);
        paramsMap.entrySet().stream()
                .filter(Objects::nonNull)
                .forEach(entry -> {
                    Stream<Class> clazzStream = clazzStreamSupplier.get();
                    // 若元素类型与目标类型相同, 则结束本次循环
                    if (clazzStream.anyMatch(clazz -> clazz.isInstance(entry.getValue()))) {
                        return;
                    }
                    resultMap.put(entry.getKey(), entry.getValue());
                });
        return resultMap;
    }


    /**
     * 从数组中移除指定类型的元素
     *
     * @param paramArray 参数数据源
     * @param clazzArray 目标类
     * @return
     */
    public static Object[] removeSpecifiedElement(Object[] paramArray, Class<?>[] clazzArray) {
        if (ArrayUtils.isEmpty(paramArray) || isEmpty(clazzArray)) {
            return paramArray;
        }
        List<Object> resultList = new ArrayList<>(paramArray.length);
        // 将流导入Supplier工厂, 需要时即取出来, 取出来时就会构造流, 即新的实例
        Supplier<Stream<Class>> clazzStreamSupplier = () -> Arrays.stream(clazzArray);
        Arrays.stream(paramArray)
                .filter(Objects::nonNull)
                .forEach(paramObj -> {
                    Stream<Class> clazzStream = clazzStreamSupplier.get();
                    // 若元素类型与目标类型相同, 则结束本次循环
                    // anyMatch属于流操作终止符, 因而每次操作前都需要获得流
                    if (clazzStream.anyMatch(clazz -> clazz.isInstance(paramObj))) {
                        return;
                    }
                    resultList.add(paramObj);
                });
        return resultList.toArray();
    }

    // =====================数组判空、展示、判同==================== //

    public static <T> boolean isEmpty(T[] array) {
        return ArrayUtils.isEmpty(array);
    }

    public static boolean isEmpty(long[] array) {
        return ArrayUtils.isEmpty(array);
    }

    public static boolean isEmpty(int[] array) {
        return ArrayUtils.isEmpty(array);
    }

    public static boolean isEmpty(short[] array) {
        return ArrayUtils.isEmpty(array);
    }

    public static boolean isEmpty(char[] array) {
        return ArrayUtils.isEmpty(array);
    }

    public static boolean isEmpty(byte[] array) {
        return ArrayUtils.isEmpty(array);
    }

    public static boolean isEmpty(double[] array) {
        return ArrayUtils.isEmpty(array);
    }

    public static boolean isEmpty(float[] array) {
        return ArrayUtils.isEmpty(array);
    }

    public static boolean isEmpty(boolean[] array) {
        return ArrayUtils.isEmpty(array);
    }

    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotEmpty(long[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotEmpty(int[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotEmpty(short[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotEmpty(char[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotEmpty(byte[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotEmpty(double[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotEmpty(float[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotEmpty(boolean[] array) {
        return !isEmpty(array);
    }

    public static <T> String showArray(T[] array) {
        return Arrays.toString(array);
    }

    public static String showArray(long[] array) {
        return Arrays.toString(array);
    }

    public static String showArray(int[] array) {
        return Arrays.toString(array);
    }

    public static String showArray(short[] array) {
        return Arrays.toString(array);
    }

    public static String showArray(char[] array) {
        return Arrays.toString(array);
    }

    public static String showArray(byte[] array) {
        return Arrays.toString(array);
    }

    public static String showArray(double[] array) {
        return Arrays.toString(array);
    }

    public static String showArray(float[] array) {
        return Arrays.toString(array);
    }

    public static String showArray(boolean[] array) {
        return Arrays.toString(array);
    }

    public static <T> boolean isEqualsArray(T[] a, T[] a2) {
        return Arrays.equals(a, a2);
    }

    public static boolean isEqualsArray(long[] a, long[] a2) {
        return Arrays.equals(a, a2);
    }

    public static boolean isEqualsArray(int[] a, int[] a2) {
        return Arrays.equals(a, a2);
    }

    public static boolean isEqualsArray(short[] a, short[] a2) {
        return Arrays.equals(a, a2);
    }

    public static boolean isEqualsArray(char[] a, char[] a2) {
        return Arrays.equals(a, a2);
    }

    public static boolean isEqualsArray(byte[] a, byte[] a2) {
        return Arrays.equals(a, a2);
    }

    public static boolean isEqualsArray(double[] a, double[] a2) {
        return Arrays.equals(a, a2);
    }

    public static boolean isEqualsArray(float[] a, float[] a2) {
        return Arrays.equals(a, a2);
    }

    public static boolean isEqualsArray(boolean[] a, boolean[] a2) {
        return Arrays.equals(a, a2);
    }

    public static <T> boolean nonEqualsArray(T[] a, T[] a2) {
        return !isEqualsArray(a, a2);
    }

    public static boolean nonEqualsArray(long[] a, long[] a2) {
        return !isEqualsArray(a, a2);
    }

    public static boolean nonEqualsArray(int[] a, int[] a2) {
        return !isEqualsArray(a, a2);
    }

    public static boolean nonEqualsArray(short[] a, short[] a2) {
        return !isEqualsArray(a, a2);
    }

    public static boolean nonEqualsArray(char[] a, char[] a2) {
        return !isEqualsArray(a, a2);
    }

    public static boolean nonEqualsArray(byte[] a, byte[] a2) {
        return !isEqualsArray(a, a2);
    }

    public static boolean nonEqualsArray(double[] a, double[] a2) {
        return !isEqualsArray(a, a2);
    }

    public static boolean nonEqualsArray(float[] a, float[] a2) {
        return !isEqualsArray(a, a2);
    }

    public static boolean nonEqualsArray(boolean[] a, boolean[] a2) {
        return !isEqualsArray(a, a2);
    }

    // =====================数组判空===================== //

    public static <E> List<E> emptyList() {
        return new ArrayList<>();
    }

    public static <E> List<E> immutableEmptyList() {
        return Collections.emptyList();
    }

    public static <E> Set<E> emptySet() {
        return new HashSet<>();
    }

    public static <E> Set<E> immutableEmptySet() {
        return Collections.emptySet();
    }

    public static <K, V> Map<K, V> emptyMap() {
        return new HashMap<>();
    }

    public static <K, V> Map<K, V> immutableEmptyMap() {
        return Collections.emptyMap();
    }

    /**
     * Returns a merge function, suitable for use in
     * {@link Map#merge(Object, Object, BiFunction) Map.merge()} or
     * throws {@code IllegalStateException}.  This can be used to enforce the
     * assumption that the elements being collected are distinct.
     *
     * @param <T> the type of input arguments to the merge function
     * @return a merge function which always throw {@code IllegalStateException}
     */
    private static <T> BinaryOperator<T> nonDuplicateKey() {
        return (u, v) -> {
            throw new IllegalStateException(String.format("转换Map时不允许重复Key: [%s]", u));
        };
    }

    private static <T> BinaryOperator<T> enableNewOnDuplicateKey() {
        return (oldValue, newValue) -> newValue;
    }

    /**
     * 根据比较器对Map排序
     *
     * @param sourceMap  数据源
     * @param comparator 比较器
     * @return
     */
    private static <K, V> LinkedHashMap<K, V> sortMap(Map<K, V> sourceMap, Comparator<Map.Entry<K, V>> comparator) {
        return sourceMap.entrySet().stream()
                .sorted(comparator)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    /**
     * 根据样本计算排名
     *
     * @param coll
     * @param valueFunction
     */
    public static <T, F extends Comparable<? super F>> List<Pair<Integer, T>> calculateRank(@NonNull Collection<T> coll, Function<? super T, ? extends F> valueFunction) {
        List<T> source = Lists.newArrayList(coll);
        // 对数据源按照值降序排列
        source.sort(getDescComparator(valueFunction));
        List<Pair<Integer, T>> resultList = new ArrayList<>();
        // 排名
        int rank = 1;
        int prevRank = 1;
        T prevValue = null;
        for (T currValue : source) {
            int currRank;
            if (Objects.equals(prevValue, currValue)) {
                // 跟上一个元素等值
                currRank = prevRank;
            } else {
                currRank = rank;
                prevRank = rank;
                prevValue = currValue;
            }
            resultList.add(Pair.of(currRank, currValue));
            rank++;
        }
        return resultList;
    }

    /**
     * 升序排序, null值放到最后
     *
     * @param keyExtractor 排序字段
     * @return 比较器
     */
    public static <T, U extends Comparable<? super U>> Comparator<T> getAscComparator(Function<? super T, ? extends U> keyExtractor) {
        return (Comparator<T> & Serializable) (c1, c2) -> {
            U u2 = keyExtractor.apply(c2);
            if (Objects.isNull(u2)) {
                return -1;
            }
            U u1 = keyExtractor.apply(c1);
            if (Objects.isNull(u1)) {
                return 1;
            }
            return u1.compareTo(u2);
        };
    }

    /**
     * 降序排序, null值放到最后
     *
     * @param keyExtractor 排序字段
     * @return 比较器
     */
    public static <T, U extends Comparable<? super U>> Comparator<T> getDescComparator(Function<? super T, ? extends U> keyExtractor) {
        return (Comparator<T> & Serializable) (c1, c2) -> {
            U u2 = keyExtractor.apply(c2);
            if (Objects.isNull(u2)) {
                return -1;
            }
            U u1 = keyExtractor.apply(c1);
            if (Objects.isNull(u1)) {
                return 1;
            }
            return u2.compareTo(u1);
        };
    }

    /**
     * 根据指定排序器对数据源排序, 返回原始数据类型
     * 默认排序行为: 根据排序字段的compareTo()方法
     *
     * @param coll            数据源
     * @param sortFieldMapper 排序字段函数
     * @param resultMapper    结果元素转换函数
     * @param <T>             原始元素类型
     * @param <F>             排序字段类型
     * @param <R>             结果元素类型
     * @return
     */
    public static <T, F extends Comparable<? super F>, R> List<R> customSortAndTransList(Collection<T> coll, Function<? super T, ? extends F> sortFieldMapper, Function<? super T, ? extends R> resultMapper) {
        return customSortAndTransList(coll, sortFieldMapper, Comparator.comparing(Function.identity()), resultMapper);
    }

    /**
     * 根据指定排序器对数据源排序, 返回原始数据类型
     *
     * @param coll            数据源
     * @param sortFieldMapper 排序字段函数
     * @param sortAction      排序行为
     * @param resultMapper    结果元素转换函数
     * @param <T>             原始元素类型
     * @param <F>             排序字段类型
     * @param <R>             结果元素类型
     * @return
     */
    public static <T, F extends Comparable<? super F>, R> List<R> customSortAndTransList(Collection<T> coll, Function<? super T, ? extends F> sortFieldMapper, Comparator<F> sortAction, Function<? super T, ? extends R> resultMapper) {
        return coll.stream().sorted(useCustomFieldComparator(sortFieldMapper, sortAction)).map(resultMapper).collect(Collectors.toList());
    }

    /**
     * 自定义比较器, 用对象字段作为比较依据
     *
     * @param mapper
     * @param comparator
     * @return
     */
    public static <T, U extends Comparable<? super U>> Comparator<T> useCustomFieldComparator(Function<? super T, ? extends U> mapper, Comparator<U> comparator) {
        return (Comparator<T> & Serializable) (c1, c2) -> comparator.compare(mapper.apply(c1), mapper.apply(c2));
    }

    /**
     * 获取中文姓名比较器
     *
     * @param function 姓名字段执行函数
     * @return
     */
    public static <T> Comparator<T> getNameComparator(Function<T, String> function) {
        return new NameComparator<>(function);
    }

    /**
     * 姓名排序（拼音首字母升序）
     **/
    public static class NameComparator<T> implements Comparator<T> {
        private final Function<T, String> function;

        NameComparator(Function<T, String> function) {
            this.function = function;
        }

        @Override
        public int compare(T o1, T o2) {
            String name1 = function.apply(o1);
            String name2 = function.apply(o2);
            if (Objects.isNull(name2)) {
                return -1;
            }
            if (Objects.isNull(name1)) {
                return 1;
            }
            for (int i = 0; i < name1.length() && i < name2.length(); i++) {
                int codePoint1 = name1.charAt(i);
                int codePoint2 = name2.charAt(i);
                boolean supplementaryCodePoint1 = Character.isSupplementaryCodePoint(codePoint1);
                boolean supplementaryCodePoint2 = Character.isSupplementaryCodePoint(codePoint2);
                if (supplementaryCodePoint1 || supplementaryCodePoint2) {
                    i++;
                }
                if (codePoint1 != codePoint2) {
                    if (supplementaryCodePoint1 || supplementaryCodePoint2) {
                        return codePoint1 - codePoint2;
                    }
                }
                String[] codePointArray1 = PinyinHelper.toHanyuPinyinStringArray((char) codePoint1);
                String[] codePointArray2 = PinyinHelper.toHanyuPinyinStringArray((char) codePoint2);
                String pinyin1 = Objects.isNull(codePointArray1) ? null : codePointArray1[0];
                String pinyin2 = Objects.isNull(codePointArray2) ? null : codePointArray2[0];

                if (Objects.nonNull(pinyin1) && Objects.nonNull(pinyin2)) {
                    // 两个字符都是汉字
                    if (!pinyin1.equals(pinyin2)) {
                        return pinyin1.compareTo(pinyin2);
                    }
                } else {
                    return codePoint2 - codePoint1;
                }
            }
            return name1.length() - name2.length();
        }
    }
}
