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

package com.xyz.caofancpu.excel.core;

/**
 * 节点根, 所有Excel都可看做是其子类
 *
 * @author D8GER
 * @author guanxiaochen
 */
public abstract class Node {
    /**
     * 父节点
     */
    private Node parent;

    public Node parent() {
        return parent;
    }

    public void parent(Node parent) {
        this.parent = parent;
    }
}
