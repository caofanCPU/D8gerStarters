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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;

/**
 * 临时对象
 *
 * @author D8GER
 * @author guanxiaochen
 */
public class DataItem<T> {
    private final int total;
    private final Iterator<T> it;
    private final DataItem<?> parent;
    private T value;
    private int index;

    public DataItem(Collection<T> list) {
        this(list, null);
    }

    public DataItem(Collection<T> list, DataItem<?> parent) {
        this.index = -1;
        this.total = list.size();
        this.it = list.iterator();
        this.parent = parent;
    }

    public int getTotal() {
        return total;
    }

    public int getIndex() {
        return index;
    }

    boolean loadNext() {
        boolean hasNext = it.hasNext();
        if (hasNext) {
            index++;
            value = it.next();
        }
        return hasNext;
    }

    T nextValue() {
        if (loadNext()) {
            return value;
        }
        return null;
    }

    public T value() {
        return value;
    }

    public boolean isFirst() {
        return index == 0;
    }

    public boolean isLast() {
        return index == total - 1;
    }

    public DataItem<?> parent() {
        return parent;
    }

    <F> DataItem<F> child(Function<T, Collection<F>> childFunction) {
        T value = value();
        return new DataItem<>(value == null ? Collections.emptyList() : childFunction.apply(value), this);
    }

    <F> DataItem<F> child(ItemFunction<T, Collection<F>> valueFunction) {
        return new DataItem<>(valueFunction.apply(this), this);
    }

}
