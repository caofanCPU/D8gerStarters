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
import lombok.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 树形List操作工具类
 * 要求： E必须具备id, [pid/children]字段, 主键可比较
 *
 * @author D8GER
 */
public class WrapTreeUtil {

    /**
     * 根据节点深度裁剪树
     * 处理方式：递归遍历找到目标深度的元素, 将其子集置空, 剩余的元素直接返回, 且返回的也是树型结构
     *
     * @param sourceNestedList 原始嵌套List
     * @param depth            指定深度
     * @param childrenFunction 子集获取函数
     * @param depthFunction    深度操作函数
     * @param <S>              S元素必须可序列化
     */
    public static <I extends Comparable<I>, S extends Serializable> List<S> cutTreeElementByDepth(
            List<S> sourceNestedList,
            @NonNull I depth,
            @NonNull Function<? super S, ? extends List<S>> childrenFunction,
            @NonNull Function<? super S, ? extends I> depthFunction) {
        return sourceNestedList.stream()
                .filter(Objects::nonNull)
                .map(currentElement -> {
                    // 当前元素为叶子节点, 无法对子集进行操作, 可以直接返回, 不需要复制对象
                    if (CollectionUtil.isEmpty(childrenFunction.apply(currentElement))) {
                        return currentElement;
                    }
                    // 深拷贝对象
                    S newElement = JSONUtil.deepCloneBySerialization(currentElement);
                    // 拿到子集引用
                    List<S> children = childrenFunction.apply(newElement);
                    // 深度未达限制值且为非叶子节点, 那么递归调用
                    if (depthFunction.apply(newElement).compareTo(depth) < 0) {
                        // 递归获取子集返回的list结果
                        List<S> cList = cutTreeElementByDepth(children, depth, childrenFunction, depthFunction);
                        // 子集置空
                        children.clear();
                        // 再设置子集结果
                        children.addAll(cList);
                    } else {
                        // 子集置空
                        children.clear();
                    }
                    return newElement;
                })
                .collect(Collectors.toList());
    }

    /**
     * 收集末级节点
     * 对于给定的列表,  顺序遍历父节点->子节点,
     * 将[满足深度限制的非叶子节点]或[小于深度限制的叶子节点]
     * 添加到结果收集容器中
     * <p>
     * 与{@link #collectRelativeTreeLeafElements}的区别：
     * 如果深度限制值很小, 那么收集容器中会有较多的非叶子节点
     * 对于嵌套型List来说, 其子节点集合的数据仍在收集容器中
     * 考虑到数据使用率的问题, 使用本方法将废弃数据置空
     * <p>
     * 代价/风险：借助JSONObject及元素的序列化实现深拷贝
     *
     * @param nonNestedList       非嵌套平铺List数据源
     * @param depth               叶子节点深度限制
     * @param pidFunction         pid操作函数表达式
     * @param idFunction          id操作函数表达式
     * @param setChildrenFunction 设置子集列表函数表达式
     * @param getChildrenFunction 获取子集列表函数表达式
     * @param depthFunction       深度操作函数
     * @param mapper              结果搜集操作函数
     * @param sortNoFunction      设置排序函数表达式
     * @param <S>                 S元素必须可序列化
     * @return
     */
    public static <I extends Comparable<I>, S extends Serializable, T> List<T> pureSelectRelativeTreeLeafByDepth(
            List<S> nonNestedList,
            @NonNull I depth,
            @NonNull Function<? super S, ? extends I> pidFunction,
            @NonNull Function<? super S, ? extends I> idFunction,
            @NonNull BiFunction<? super S, List<S>, ? super S> setChildrenFunction,
            @NonNull Function<? super S, ? extends List<S>> getChildrenFunction,
            @NonNull Function<? super S, ? extends I> depthFunction,
            @NonNull Function<? super S, ? extends T> mapper,
            Function<? super S, ? extends I> sortNoFunction) {
        List<S> nestedTreeElements = initOriginTreeByPid(nonNestedList, pidFunction, idFunction, setChildrenFunction, sortNoFunction);
        List<S> collector = new ArrayList<>();
        collectRelativeTreeLeafByDepth(collector, nestedTreeElements, depth, getChildrenFunction, depthFunction, Function.identity());
        if (CollectionUtil.isEmpty(collector)) {
            return new ArrayList<>();
        }
        return collector.stream()
                .filter(Objects::nonNull)
                .map(item -> {
                    if (CollectionUtil.isEmpty(getChildrenFunction.apply(item))) {
                        return mapper.apply(item);
                    }
                    // 深拷贝对象, 根据childrenFunction处理其子节点
                    S newElement = JSONUtil.deepCloneBySerialization(item);
                    List<S> children = getChildrenFunction.apply(newElement);
                    // 子集置空
                    children.clear();
                    return mapper.apply(newElement);
                })
                .collect(Collectors.toList());
    }

    /**
     * 对于给定的列表, 找出指定节点ID的子树
     * 然后顺序遍历子树父节点->子节点, 遍历到的元素添加到结果收集容器中
     *
     * @param nonNestedList       非嵌套平铺List数据源
     * @param targetRootId        目标子树根节点ID
     * @param pidFunction         pid操作函数表达式
     * @param idFunction          id操作函数表达式
     * @param setChildrenFunction 设置子集列表函数表达式
     * @param getChildrenFunction 获取子集列表函数表达式
     * @param mapper              获取结果函数表达式
     * @param sortNoFunction      设置排序函数表达式
     * @return
     */
    public static <I extends Comparable<I>, C, T> List<T> expandSubTreeElements(
            List<C> nonNestedList,
            @NonNull I targetRootId,
            @NonNull Function<? super C, ? extends I> pidFunction,
            @NonNull Function<? super C, ? extends I> idFunction,
            @NonNull BiFunction<? super C, List<C>, ? super C> setChildrenFunction,
            @NonNull Function<? super C, ? extends List<C>> getChildrenFunction,
            @NonNull Function<? super C, ? extends T> mapper,
            Function<? super C, ? extends I> sortNoFunction) {
        List<C> nestedTreeElements = initOriginTreeByPid(nonNestedList, pidFunction, idFunction, setChildrenFunction, sortNoFunction);
        C subTree = findSubTree(nestedTreeElements, idFunction, getChildrenFunction, targetRootId);
        if (Objects.isNull(subTree)) {
            return Lists.newArrayList();
        }
        List<C> subWrapTreeList = new ArrayList<>();
        subWrapTreeList.add(subTree);
        List<T> collector = new ArrayList<>();
        expandTree(collector, subWrapTreeList, getChildrenFunction, mapper);
        return collector;
    }

    /**
     * 对于给定的列表, 顺序遍历父节点->子节点, 遍历到的元素添加到结果收集容器中
     *
     * @param nonNestedList       非嵌套平铺List数据源
     * @param pidFunction         pid操作函数表达式
     * @param idFunction          id操作函数表达式
     * @param setChildrenFunction 设置子集列表函数表达式
     * @param getChildrenFunction 获取子集列表函数表达式
     * @param mapper              获取结果函数表达式
     * @param sortNoFunction      设置排序函数表达式
     * @return
     */
    public static <I extends Comparable<I>, C, T> List<T> expandNonNestedTreeList(
            List<C> nonNestedList,
            @NonNull Function<? super C, ? extends I> pidFunction,
            @NonNull Function<? super C, ? extends I> idFunction,
            @NonNull BiFunction<? super C, List<C>, ? super C> setChildrenFunction,
            @NonNull Function<? super C, ? extends List<C>> getChildrenFunction,
            @NonNull Function<? super C, ? extends T> mapper,
            Function<? super C, ? extends I> sortNoFunction) {
        List<C> nestedTreeElements = initOriginTreeByPid(nonNestedList, pidFunction, idFunction, setChildrenFunction, sortNoFunction);
        List<T> collector = new ArrayList<>();
        expandTree(collector, nestedTreeElements, getChildrenFunction, mapper);
        return collector;
    }

    /**
     * 对于树型列表, 寻找子树
     *
     * @param nestedTreeElements 嵌套树数据源
     * @param targetRootId       目标子根节点
     * @return
     */
    public static <I extends Comparable<I>, C> C findSubTree(
            List<C> nestedTreeElements,
            @NonNull Function<? super C, ? extends I> idFunction,
            @NonNull Function<? super C, ? extends List<C>> childrenFunction,
            @NonNull I targetRootId) {
        List<C> nonNestedTreeElementList = new ArrayList<>();
        // 将树平铺展开
        expandTree(nonNestedTreeElementList, nestedTreeElements, childrenFunction, Function.identity());
        // 搜寻
        C first = CollectionUtil.findFirst(nonNestedTreeElementList, element -> targetRootId.equals(idFunction.apply(element)));
        return Objects.nonNull(first) ? first : null;
    }

    /**
     * 递归展开树, 得到平铺目标元素的平铺列表
     *
     * @param collector          结果搜集器
     * @param nestedTreeElements 树元素列表
     * @param childrenFunction   子集操作函数
     * @param mapper             树元素操作函数
     */
    public static <C, T> void expandTree(
            List<T> collector,
            List<C> nestedTreeElements,
            @NonNull Function<? super C, ? extends List<C>> childrenFunction,
            @NonNull Function<? super C, ? extends T> mapper) {
        if (CollectionUtil.isEmpty(nestedTreeElements)) {
            return;
        }
        nestedTreeElements.stream()
                .filter(Objects::nonNull)
                .forEach(currentElement -> {
                    collector.add(mapper.apply(currentElement));
                    List<C> children = childrenFunction.apply(currentElement);
                    if (CollectionUtil.isNotEmpty(children)) {
                        expandTree(collector, children, childrenFunction, mapper);
                    }
                });
    }

    /**
     * 根据pid递归设置树元素子集, 返回由原始元素构成的子集元素
     * 如果有节点排序值, 并且数据源中节点排序值不存在null值, 则子节点列表按照自然增序排列
     *
     * @param nonNestedList       非嵌套平铺数据源
     * @param pidFunction         pid操作函数
     * @param idFunction          id操作函数
     * @param setChildrenFunction 设置子集操作函数
     * @param sortNoFunction      sortNo操作函数
     * @return
     */
    public static <I extends Comparable<I>, C> List<C> initOriginTreeByPid(
            List<C> nonNestedList,
            @NonNull Function<? super C, ? extends I> pidFunction,
            @NonNull Function<? super C, ? extends I> idFunction,
            @NonNull BiFunction<? super C, List<C>, ? super C> setChildrenFunction,
            Function<? super C, ? extends I> sortNoFunction) {
        TreeMap<I, List<C>> pidMultiMap = nonNestedList.stream()
                .filter(Objects::nonNull)
                .collect(TreeMap::new, (map, c) -> map.computeIfAbsent(pidFunction.apply(c), init -> Lists.newArrayList()).add(c), TreeMap::putAll);
        return initOriginTreeChildItems(pidMultiMap, pidMultiMap.firstKey(), idFunction, setChildrenFunction, sortNoFunction);
    }

    /**
     * 根据深度收集末级节点
     * 对于给定的列表, 顺序遍历父节点->子节点,
     * 将[满足深度限制的非叶子节点]或[小于深度限制的叶子节点]
     * 添加到结果收集容器中
     *
     * @param nonNestedList       数据源
     * @param depth               叶子节点深度限制
     * @param pidFunction         pid操作函数表达式
     * @param idFunction          id操作函数表达式
     * @param setChildrenFunction 设置子集列表函数表达式
     * @param getChildrenFunction 获取子集列表函数表达式
     * @param depthFunction       depth操作函数
     * @param mapper              结果收集函数表达式
     * @param sortNoFunction      自己列表排序字段函数表达式(可选)
     * @return
     */
    public static <I extends Comparable<I>, C, T> List<T> collectRelativeTreeLeafElements(
            List<C> nonNestedList,
            @NonNull I depth,
            @NonNull Function<? super C, ? extends I> pidFunction,
            @NonNull Function<? super C, ? extends I> idFunction,
            @NonNull BiFunction<? super C, List<C>, ? super C> setChildrenFunction,
            @NonNull Function<? super C, ? extends List<C>> getChildrenFunction,
            @NonNull Function<? super C, ? extends I> depthFunction,
            @NonNull Function<? super C, ? extends T> mapper,
            Function<? super C, ? extends I> sortNoFunction) {
        List<C> nestedTreeElements = initOriginTreeByPid(nonNestedList, pidFunction, idFunction, setChildrenFunction, sortNoFunction);
        return selectRelativeTreeLeafByDepth(nestedTreeElements, depth, getChildrenFunction, depthFunction, mapper);
    }

    /**
     * 根据深度限制值收集末级节点
     * 对于给定的嵌套树型列表,  顺序遍历父节点->子节点,
     * 将[满足深度限制的非叶子节点]或[小于深度限制的叶子节点]
     * 添加到结果收集容器中
     * <p>
     * {@link #collectRelativeTreeLeafElements}
     *
     * @param nestedTreeList   数据源
     * @param depth            叶子节点深度限制
     * @param childrenFunction children操作函数表达式
     * @param depthFunction    节点深度操作函数表达式
     * @param mapper           结果收集函数表达式
     * @return
     */
    public static <I extends Comparable<I>, C, T> List<T> selectRelativeTreeLeafByDepth(
            List<C> nestedTreeList,
            @NonNull I depth,
            @NonNull Function<? super C, ? extends List<C>> childrenFunction,
            @NonNull Function<? super C, ? extends I> depthFunction,
            @NonNull Function<? super C, ? extends T> mapper) {
        List<T> collector = new ArrayList<>();
        collectRelativeTreeLeafByDepth(collector, nestedTreeList, depth, childrenFunction, depthFunction, mapper);
        return collector;
    }

    /**
     * 根据pid递归设置树元素子集, 返回由原始元素构成的子集元素
     * 如果有节点排序值, 并且数据源中节点排序值不存在null值, 则子节点列表按照自然增序排列
     *
     * @param pidMultiMap         分类Map
     * @param pid                 pid
     * @param idFunction          id操作函数
     * @param setChildrenFunction 设置子集操作函数
     * @param sortNoFunction      sortNo操作函数
     * @return
     */
    private static <I extends Comparable<I>, C> List<C> initOriginTreeChildItems(
            Map<I, List<C>> pidMultiMap,
            @NonNull I pid,
            @NonNull Function<? super C, ? extends I> idFunction,
            @NonNull BiFunction<? super C, List<C>, ? super C> setChildrenFunction,
            Function<? super C, ? extends I> sortNoFunction) {
        List<C> currentList = pidMultiMap.get(pid);
        if (CollectionUtil.isEmpty(currentList)) {
            return Lists.newArrayList();
        }
        List<C> resultOriginTreeList = currentList.stream()
                .filter(Objects::nonNull)
                .peek(cItem -> {
                    List<C> children = initOriginTreeChildItems(pidMultiMap, idFunction.apply(cItem), idFunction, setChildrenFunction, sortNoFunction);
                    // 内层排序
                    sort(children, sortNoFunction);
                    setChildrenFunction.apply(cItem, children);
                })
                .collect(Collectors.toList());
        // 最外层排序
        return sort(resultOriginTreeList, sortNoFunction);
    }

    /**
     * 收集相对深度限制值得末级节点
     * 对于给定的列表,  顺序遍历父节点->子节点,
     * 将[满足深度限制的非叶子节点]或[小于深度限制的叶子节点]
     * 添加到结果收集容器中
     *
     * @param collector          结果收集容器
     * @param nestedTreeElements 树元素列表
     * @param depth              叶子节点深度限制
     * @param childrenFunction   子集列表操作函数
     * @param depthFunction      深度操作函数
     * @param mapper             结果搜集操作函数
     */
    private static <I extends Comparable<I>, C, T> void collectRelativeTreeLeafByDepth(
            List<T> collector,
            List<C> nestedTreeElements,
            @NonNull I depth,
            @NonNull Function<? super C, ? extends List<C>> childrenFunction,
            @NonNull Function<? super C, ? extends I> depthFunction,
            @NonNull Function<? super C, ? extends T> mapper) {
        if (CollectionUtil.isEmpty(nestedTreeElements)) {
            return;
        }
        nestedTreeElements.stream()
                .filter(Objects::nonNull)
                .forEach(currentElement -> {
                    List<C> children = childrenFunction.apply(currentElement);
                    if (CollectionUtil.isNotEmpty(children) && depthFunction.apply(currentElement).compareTo(depth) < 0) {
                        // 遍历时子节点时, 非叶子节点并且 节点深度小于 深度限制值, 则进行递归查找
                        collectRelativeTreeLeafByDepth(collector, children, depth, childrenFunction, depthFunction, mapper);
                    } else {
                        // 否则满足收集条件, 加入结果容器中
                        collector.add(mapper.apply(currentElement));
                    }
                });
    }

    /**
     * 根据节点排序值函数排序
     *
     * @param currentOriginChildren 当前子节点列表
     * @param sortNoFunction        节点排序值函数
     */
    private static <I extends Comparable<I>, C> List<C> sort(List<C> currentOriginChildren, Function<? super C, ? extends I> sortNoFunction) {
        if (Objects.nonNull(sortNoFunction)) {
            // 子节点列表自然增序
            List<I> currentChildrenSortNoList = CollectionUtil.filterAndTransList(currentOriginChildren, Objects::isNull, sortNoFunction);
            if (CollectionUtil.isEmpty(currentChildrenSortNoList) && CollectionUtil.isNotEmpty(currentOriginChildren) && currentOriginChildren.size() > 1) {
                currentOriginChildren.sort(Comparator.comparing(sortNoFunction));
            }
        }
        return currentOriginChildren;
    }


}