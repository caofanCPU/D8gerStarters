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

package com.xyz.caofancpu.excel.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * 对象管理
 *
 * @author D8GER
 * @author guanxiaochen
 */
public class PoiXmlContext extends ExpressionParser implements Map<String, Object> {

    private final Stack<Map<String, Object>> stack = new Stack<>();

    public PoiXmlContext() {
        context = this;
        stack.push(new HashMap<>());
    }

    public void beginStack(Map<String, Object> values) {
        stack.push(new HashMap<>(values));
    }

    public void beginStack() {
        stack.push(new HashMap<>());
    }

    public void endStack() {
        if (stack.size() > 0) {
            stack.pop();
        }
    }

    @Override
    public int size() {
        int size = 0;
        for (Map<String, Object> map : stack) {
            size += map.size();
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        for (Map<String, Object> map : stack) {
            if (!map.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsKey(Object key) {
        ListIterator<Map<String, Object>> listIterator = stack.listIterator(stack.size());
        while (listIterator.hasPrevious()) {
            if (listIterator.previous().containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        ListIterator<Map<String, Object>> listIterator = stack.listIterator(stack.size());
        while (listIterator.hasPrevious()) {
            if (listIterator.previous().containsValue(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object get(Object key) {
        ListIterator<Map<String, Object>> listIterator = stack.listIterator(stack.size());
        while (listIterator.hasPrevious()) {
            Map<String, Object> previousMap = listIterator.previous();
            //noinspection SuspiciousMethodCalls
            if (previousMap.containsKey(key)) {
                return previousMap.get(key);
            }
        }
        return null;
    }

    @Override
    public Object put(String key, Object value) {
        return stack.lastElement().put(key, value);
    }

    /**
     * 不经过eval处理过程，直接取栈顶map的键值
     *
     * @param key
     * @return
     */
    public Object getObjectFromPeekMap(String key) {
        return stack.lastElement().get(key);
    }

    @Override
    public Object remove(Object key) {
        Object result = null;
        ListIterator<Map<String, Object>> listIterator = stack.listIterator(stack.size());
        while (listIterator.hasPrevious()) {
            if (result == null) {
                result = listIterator.previous().remove(key);
            } else {
                listIterator.previous().remove(key);
            }
        }
        return result;
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        stack.lastElement().putAll(m);
    }

    @Override
    public void clear() {
        stack.clear();
    }

    @Deprecated
    @Override
    public Set<String> keySet() {
        Set<String> result = new HashSet<>();
        for (Map<String, Object> map : stack) {
            result.addAll(map.keySet());
        }
        return result;
    }

    @Deprecated
    @Override
    public Collection<Object> values() {
        List<Object> result = new ArrayList<>();
        for (Map<String, Object> map : stack) {
            result.addAll(map.values());
        }
        return result;
    }

    @Deprecated
    @Override
    public Set<Entry<String, Object>> entrySet() {
        Set<Entry<String, Object>> result = new HashSet<>();
        for (Map<String, Object> map : stack) {
            result.addAll(map.entrySet());
        }
        return result;
    }


}
