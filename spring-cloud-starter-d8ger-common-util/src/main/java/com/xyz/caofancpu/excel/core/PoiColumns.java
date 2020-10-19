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
import java.util.function.Function;

/**
 * Excel多列
 *
 * @author D8GER
 * @author guanxiaochen
 */
public class PoiColumns<T, V> extends PoiColumnItems<T, V> {

    public PoiColumns(ValueFunction<T, Collection<V>> valueFunction) {
        super(valueFunction);
    }

    public PoiColumns(ValueFunction<T, Collection<V>> valueFunction, String... title) {
        super(valueFunction, t1 -> title);
    }

    public PoiColumns(ValueFunction<T, Collection<V>> valueFunction, Function<V, String> title) {
        super(valueFunction, t1 -> new String[]{title.apply(t1.value())});
    }

    public <V2> PoiColumnItems<V, V2> addColumns(ValueFunction<V, Collection<V2>> valueFunction, Function<V2, String> title) {
        return addColumns(new PoiColumns<>(valueFunction, title));
    }

    public <V2> PoiColumn<V> addColumn(ValueFunction<V, V2> valueFunction, Function<V, String> titles) {
        return addColumn(new PoiColumn<>(valueFunction, item -> new String[]{titles.apply(item.value())}));
    }

    public <V2> PoiColumn<V> addColumn(ValueFunction<V, V2> valueFunction, String... titles) {
        return addColumn(new PoiColumn<>(valueFunction, item -> titles));
    }
}
