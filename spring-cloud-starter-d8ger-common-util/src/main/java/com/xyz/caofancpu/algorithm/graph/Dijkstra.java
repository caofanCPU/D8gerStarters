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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 */
public class Dijkstra {

    public static void main(String[] args) {
        Graph graph = initGraph();
        Map<Integer, Integer> distanceMap = nbDijkstra(graph, 0);
        distanceMap.forEach((k, v) -> System.out.println("距离顶点下标[" + k + "]的距离为: " + v));
    }

    private static Map<Integer, Integer> nbDijkstra(Graph graph, int startVertexIndex) {
        int vertexSize = graph.getVertices().length;
        // save startVertexIndex to antherVertexIndex distance
        Map<Integer, Integer> distanceMap = new HashMap<>(vertexSize);
        Set<Integer> alreadyAccessedVertexIndexSet = new HashSet<>(vertexSize);
        // init distanceMap, default ﹢∞
        for (int i = 0; i < vertexSize; i++) {
            distanceMap.put(i, Integer.MAX_VALUE);
        }
        // traverse startPoint and refresh
        alreadyAccessedVertexIndexSet.add(0);
        LinkedList<Edge> edgesFromStart = graph.getAdjacencyMatrix()[startVertexIndex];
        edgesFromStart.forEach(edge -> distanceMap.put(edge.index, edge.weight));
        // main circle, repeat traverse
        for (int i = 1; i < vertexSize; i++) {
            int minDistanceFromStart = Integer.MAX_VALUE;
            int minDistanceIndex = -1;
            for (int j = 1; j < vertexSize; j++) {
                if (!alreadyAccessedVertexIndexSet.contains(j) && distanceMap.get(j) < minDistanceIndex) {
                    minDistanceFromStart = distanceMap.get(j);
                    minDistanceIndex = j;
                }
            }
            if (minDistanceIndex == -1) {
                break;
            }

            //
            alreadyAccessedVertexIndexSet.add(minDistanceIndex);
            for (Edge edge : graph.getAdjacencyMatrix()[minDistanceIndex]) {
                if (alreadyAccessedVertexIndexSet.contains(edge.getIndex())) {
                    continue;
                }
                Integer weight = edge.getWeight();
                Integer preDistance = distanceMap.get(edge.getIndex());
                if (!Objects.equals(weight, Integer.MAX_VALUE) && minDistanceFromStart + weight < preDistance) {
                    distanceMap.put(edge.getIndex(), minDistanceFromStart + weight);
                }
            }
        }
        return distanceMap;
    }

    private static Graph initGraph() {
        Graph graph = new Graph(7);
        graph.getVertices()[0] = new Vertex("A");
        graph.getVertices()[1] = new Vertex("B");
        graph.getVertices()[2] = new Vertex("C");
        graph.getVertices()[3] = new Vertex("D");
        graph.getVertices()[4] = new Vertex("E");
        graph.getVertices()[5] = new Vertex("F");
        graph.getVertices()[6] = new Vertex("G");
        // A
        graph.getAdjacencyMatrix()[0].add(new Edge(1, 5));
        graph.getAdjacencyMatrix()[0].add(new Edge(2, 2));
        // B
        graph.getAdjacencyMatrix()[1].add(new Edge(0, 5));
        graph.getAdjacencyMatrix()[1].add(new Edge(3, 1));
        graph.getAdjacencyMatrix()[1].add(new Edge(4, 6));
        // C
        graph.getAdjacencyMatrix()[2].add(new Edge(0, 2));
        graph.getAdjacencyMatrix()[2].add(new Edge(3, 6));
        graph.getAdjacencyMatrix()[2].add(new Edge(5, 8));
        // D
        graph.getAdjacencyMatrix()[3].add(new Edge(1, 1));
        graph.getAdjacencyMatrix()[3].add(new Edge(2, 6));
        graph.getAdjacencyMatrix()[3].add(new Edge(4, 1));
        graph.getAdjacencyMatrix()[3].add(new Edge(5, 2));
        // E
        graph.getAdjacencyMatrix()[4].add(new Edge(1, 6));
        graph.getAdjacencyMatrix()[4].add(new Edge(3, 1));
        graph.getAdjacencyMatrix()[4].add(new Edge(6, 7));
        // F
        graph.getAdjacencyMatrix()[5].add(new Edge(2, 8));
        graph.getAdjacencyMatrix()[5].add(new Edge(3, 2));
        graph.getAdjacencyMatrix()[5].add(new Edge(6, 3));
        // G
        graph.getAdjacencyMatrix()[6].add(new Edge(4, 7));
        graph.getAdjacencyMatrix()[6].add(new Edge(5, 3));

        return graph;
    }

    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Vertex {
        private String data;
    }

    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Edge {
        private Integer index;
        private Integer weight;
    }

    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Graph {
        private Vertex[] vertices;
        private LinkedList<Edge>[] adjacencyMatrix;

        public Graph(int size) {
            // init vertex and matrix edge
            this.vertices = new Vertex[size];
            //noinspection unchecked
            this.adjacencyMatrix = new LinkedList[size];
            for (int i = 0; i < this.adjacencyMatrix.length; i++) {
                this.adjacencyMatrix[i] = new LinkedList<>();
            }
        }
    }

}
