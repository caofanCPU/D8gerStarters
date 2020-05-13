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

package com.xyz.caofancpu.extra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义集合函数式处理工具类
 *
 * @author D8GER
 */
public class CollectorGenerateUtil {

    public static <K, V> Map<K, V> initHashMap(int capacity, float loadFactor, HashMapGeneratorInterface<K, V> hashMapGenerator) {
        return hashMapGenerator.process(new HashMap(capacity, loadFactor));
    }

    public static <K, V> Map<K, V> initHashMap(float loadFactor, HashMapGeneratorInterface<K, V> hashMapGenerator) {
        return hashMapGenerator.process(new HashMap<>(1 << 4, loadFactor));
    }

    public static <K, V> Map<K, V> initHashMap(int capacity, HashMapGeneratorInterface<K, V> hashMapGenerator) {
        return hashMapGenerator.process(new HashMap<>(capacity, 0.75f));
    }

    public static <K, V> Map<K, V> initHashMap(HashMapGeneratorInterface<K, V> hashMapGenerator) {
        return hashMapGenerator.process(new HashMap<>(1 << 4, 0.75f));
    }

    /**
     * hashMap底层已对初始化容量做了 最近的2^n幂处理
     *
     * @param capacity
     * @param arrayListGeneratorInterface
     * @param <E>
     * @return
     */
//    public static <K, V> HashMap<K, V> initEmptyHashMap(int capacity, float loadFactor) {
//        if (capacity <= 1) {
//            capacity = 1 << 4;
//        }
//        if (loadFactor < 0f) {
//            loadFactor = 0.75f;
//        }
//        int newCapacity = 1;
//        while (newCapacity < capacity) {
//            newCapacity <<= 1;
//        }
//        return new HashMap<>(newCapacity, loadFactor);
//    }
    public static <E> List<E> initArrayList(int capacity, ArrayListGeneratorInterface<E> arrayListGeneratorInterface) {
        return arrayListGeneratorInterface.process(new ArrayList<>(capacity));
    }

    public static <E> List<E> initArrayList(ArrayListGeneratorInterface<E> arrayListGeneratorInterface) {
        return arrayListGeneratorInterface.process(new ArrayList<>());
    }

    @FunctionalInterface
    public interface HashMapGeneratorInterface<K, V> {

        /**
         * 该函数名称, 入参, 返回值不影响任意
         *
         * @param hashMap
         * @return
         */
        Map<K, V> process(Map<K, V> hashMap);

    }

    @FunctionalInterface
    public interface ArrayListGeneratorInterface<E> {

        /**
         * 该函数名称, 入参, 返回值不影响任意
         *
         * @param arrayList
         * @return
         */
        List<E> process(List<E> arrayList);
    }


}
