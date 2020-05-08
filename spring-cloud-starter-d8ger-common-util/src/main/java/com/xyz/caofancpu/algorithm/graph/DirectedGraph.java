package com.xyz.caofancpu.algorithm.graph;

import com.google.common.collect.Lists;
import com.xyz.caofancpu.core.CollectionUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * 有向图
 * <p>
 * 全局统一运行时枚举
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class DirectedGraph<T> {
    /**
     * 有向图顶点数目
     */
    private int vertexNum;
    /**
     * 有向图顶点关系索引列表
     */
    private List<ArrayList<Integer>> vertexIndexList = Lists.newArrayList();
    /**
     * 有向图原始顶点元素与索引的映射关系, 方便查找
     */
    private Map<T, Integer> vertexElementAsKeyMap;
    /**
     * 有向图索引与原始顶点元素的映射关系, 方便查找
     */
    private Map<Integer, T> vertexIndexAsKeyMap;
    /**
     * 有向图原始元素关系列表
     */
    private List<Pair<T, T>> originRelationshipList = Lists.newArrayList();

    public DirectedGraph(List<Pair<T, T>> edgeList) {
        this.addEdgeList(edgeList);
    }

    /**
     * 添加有向图的边
     *
     * @param pair
     * @param autoRefresh 是否自动刷新有向图数据
     */
    public DirectedGraph addEdge(Pair<T, T> pair, boolean autoRefresh) {
        this.originRelationshipList.add(pair);
        if (autoRefresh) {
            this.buildGraph();
        }
        return this;
    }

    /**
     * 批量添加有向图边
     *
     * @param edgeList
     */
    public DirectedGraph addEdgeList(List<Pair<T, T>> edgeList) {
        this.originRelationshipList.addAll(edgeList);
        this.buildGraph();
        return this;
    }

    /**
     * 构建有向图
     */
    private void buildGraph() {
        Set<T> vertexElements = CollectionUtil.transToSetWithFlatMap(this.getOriginRelationshipList(), pair -> {
            List<T> elementList = new ArrayList<>();
            elementList.add(pair.getLeft());
            elementList.add(pair.getRight());
            return elementList;
        });
        this.vertexNum = vertexElements.size();
        List<T> vertexElementList = Lists.newArrayList(vertexElements);
        // 排序映射
        vertexElementList.sort(Comparator.comparing(Object::hashCode));
        this.vertexElementAsKeyMap = CollectionUtil.transToMap(vertexElementList, Function.identity(), vertexElementList::indexOf);
        this.vertexIndexAsKeyMap = CollectionUtil.transToMap(this.vertexElementAsKeyMap.entrySet(), Map.Entry::getValue, Map.Entry::getKey);
        this.buildVertexIndex();
    }

    /**
     * 构建有向图的索引
     */
    private void buildVertexIndex() {
        if (CollectionUtil.isEmpty(this.getOriginRelationshipList()) || CollectionUtil.isEmpty(this.getVertexElementAsKeyMap())) {
            return;
        }
        // 初始化有向图
        for (int i = 0; i < this.vertexNum; i++) {
            this.vertexIndexList.add(new ArrayList<>());
        }
        for (Pair<T, T> pair : this.originRelationshipList) {
            Integer outReferVertexIndex = this.vertexElementAsKeyMap.get(pair.getLeft());
            Integer inVertexIndex = this.vertexElementAsKeyMap.get(pair.getRight());
            this.vertexIndexList.get(outReferVertexIndex).add(inVertexIndex);
        }
    }
}
