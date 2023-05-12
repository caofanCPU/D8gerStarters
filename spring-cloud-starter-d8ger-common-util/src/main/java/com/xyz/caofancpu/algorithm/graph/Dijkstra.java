/*
 * Copyright 2016-2021 the original author
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
package com.xyz.caofancpu.algorithm.graph;

import com.xyz.caofancpu.core.CollectionFunUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

/**
 * A.5.B.6.E.7.G
 * A.2.C.8.F.3.G
 * B.1.D
 * C.6.D
 * D.2.F
 * D.1.E
 * ====
 * .
 * .   +----------------------------+
 * .   |                            |
 * .   |                            |   6
 * .   |                 +----------+----------+
 * .   |                 |          |          v
 * .   |   +----+  5   +---+  1   +---+  1   +---+  7   +---+
 * .   |   | A  | ---> | B | ---> | D | ---> | E | ---> | G |
 * .   |   +----+      +---+      +---+      +---+      +---+
 * .   |     |                      ^                     ^
 * .   | 2   | 2                    |                     |
 * .   |     v                      |                     |
 * .   |   +----+  6                |                     |
 * .   |   | C  | ------------------+                     |
 * .   |   +----+                                         |
 * .   |     |                                            |
 * .   |     | 8                                          |
 * .   |     v                                            |
 * .   |   +----+  3                                      |
 * .   +-> | F  | ----------------------------------------+
 * .       +----+
 * .
 */
public class Dijkstra {

    public static void main(String[] args) {
        String[] vertexNames = new String[]{"A", "B", "C", "D", "E", "F", "G"};
        String[] edgeInfos = new String[]{
                "AB5", "AC2",
                "BD1", "BE6",
                "CD6", "CF8",
                "DE1", "DF2",
                "EG7",
                "FG3"
        };
        Graph graph = buildGraph(vertexNames, edgeInfos);
        Map<Integer, String> showVertexMap = graph.getVertexMap();
        showVertexMap.forEach((startVertexIndex, startPoint) -> {
            System.out.println("------------------------");
            Map<Integer, Integer> distanceMap = nbDijkstra(graph, startVertexIndex);
            distanceMap.forEach((k, v) -> System.out.println(startPoint + "->" + showVertexMap.get(k) + " with Distance: " + (Objects.equals(v, Integer.MAX_VALUE) ? "+∞" : v)));
        });

    }

    private static Map<Integer, Integer> nbDijkstra(Graph graph, int startVertexIndex) {
        int vertexSize = graph.getVertices().length;
        // save startVertexIndex to antherVertexIndex distance
        Map<Integer, Integer> distanceMap = new HashMap<>(vertexSize);
        Map<Integer, Integer> accessedVertexIndexMap = new HashMap<>(vertexSize);
        // init distanceMap, default ﹢∞
        for (int i = 0; i < vertexSize; i++) {
            distanceMap.put(i, Integer.MAX_VALUE);
        }
        // traverse startPoint and refresh
        accessedVertexIndexMap.put(startVertexIndex, startVertexIndex);
        LinkedList<Edge> edgesFromStart = graph.getAdjacencyMatrix()[startVertexIndex];
        edgesFromStart.forEach(edge -> distanceMap.put(edge.getIndex(), edge.getWeight()));
        // main circle, repeat traverse
        for (int i = 0; i < vertexSize; i++) {
            int minDistanceFromStart = Integer.MAX_VALUE;
            int minDistanceIndex = -1;
            for (int j = 0; j < vertexSize; j++) {
                if (!accessedVertexIndexMap.containsKey(j) && distanceMap.get(j) < minDistanceFromStart) {
                    minDistanceFromStart = distanceMap.get(j);
                    minDistanceIndex = j;
                }
            }
            if (minDistanceIndex == -1) {
                break;
            }

            accessedVertexIndexMap.put(minDistanceIndex, minDistanceIndex);
            for (Edge edge : graph.getAdjacencyMatrix()[minDistanceIndex]) {
                if (accessedVertexIndexMap.containsKey(edge.getIndex())) {
                    continue;
                }
                Integer weight = edge.getWeight();
                Integer preDistance = distanceMap.get(edge.getIndex());
                if (!Objects.equals(weight, Integer.MAX_VALUE) && minDistanceFromStart + weight < preDistance) {
                    distanceMap.put(edge.getIndex(), minDistanceFromStart + weight);
                }
            }
        }
        // self distance should be zero
        distanceMap.put(startVertexIndex, 0);
        return distanceMap;
    }

    /**
     * Refer vertices, the edge will be ignored(removed) which pointed the vertex not exist
     *
     * @param vertexNames
     * @param edgeInfos
     * @return
     */
    public static Graph buildGraph(String[] vertexNames, String[] edgeInfos) {
        // vertex and it's index relationship
        Map<String, Integer> vertexMap = new HashMap<>(vertexNames.length);
        for (int i = 0; i < vertexNames.length; i++) {
            vertexMap.put(vertexNames[i], i);
        }
        // edge relationship and distance between two vertices
        Map<Pair<String, String>, Integer> edgeInfoMap = new HashMap<>(edgeInfos.length);
        for (String edgeInfo : edgeInfos) {
            char[] itemChars = edgeInfo.toCharArray();
            String startVertex = String.valueOf(itemChars[0]);
            String endVertex = String.valueOf(itemChars[1]);
            if (vertexMap.containsKey(startVertex) && vertexMap.containsKey(endVertex)) {
                // only the vertex exist
                Integer distance = Integer.valueOf(String.valueOf(itemChars[2]));
                edgeInfoMap.put(Pair.of(startVertex, endVertex), distance);
                edgeInfoMap.put(Pair.of(endVertex, startVertex), distance);
            }
        }
        // build graph
        Graph graph = new Graph(vertexMap.size());
        // 1.build vertex
        vertexMap.forEach((name, index) -> graph.getVertices()[index] = new Vertex(name));
        graph.setVertexMap(CollectionFunUtil.transToMap(vertexMap.entrySet(), Map.Entry::getValue, Map.Entry::getKey));
        // 2.build edge
        edgeInfoMap.forEach((vertexPair, distance) -> {
            String leftVertex = vertexPair.getLeft();
            String rightVertex = vertexPair.getRight();
            graph.getAdjacencyMatrix()[vertexMap.get(leftVertex)].add(new Edge(vertexMap.get(rightVertex), distance));
        });
        return graph;
    }

    /**
     *
     */
    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Vertex {
        private String data;
    }

    /**
     *
     */
    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Edge {
        private Integer index;
        private Integer weight;
    }

    /**
     *
     */
    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Graph {
        /**
         * 顶点:数组
         * .o   o    o
         */
        private Vertex[] vertices;
        /**
         * 顶点的边:邻接矩阵
         * .A<--o-->B
         */
        private LinkedList<Edge>[] adjacencyMatrix;
        private Map<Integer, String> vertexMap;

        public Graph(int size) {
            // init vertex and matrix edge
            this.vertices = new Vertex[size];
            //noinspection unchecked
            this.adjacencyMatrix = new LinkedList[size];
            for (int i = 0; i < size; i++) {
                this.adjacencyMatrix[i] = new LinkedList<>();
            }
        }
    }

}
