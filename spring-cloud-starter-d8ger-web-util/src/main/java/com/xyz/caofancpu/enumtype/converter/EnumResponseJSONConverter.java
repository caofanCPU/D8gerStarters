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

package com.xyz.caofancpu.enumtype.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.xyz.caofancpu.constant.IEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * 响应字段枚举类型序列化处理器
 * 如果发现是自定义IEnum的子类, 走自定义枚举转换: viewName -> name -> Enum.name()
 * 否则, 取Enum.name()
 *
 * @author D8GER
 */
@Slf4j
public class EnumResponseJSONConverter<E extends Enum<E>> extends JsonSerializer<E> {

    @Override
    public void serialize(E enumInstance, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) {
        if (Objects.isNull(enumInstance)) {
            return;
        }
        String name;
        if (enumInstance instanceof IEnum) {
            // 自定义
            IEnum temp = (IEnum) enumInstance;
            name = StringUtils.isNotBlank(temp.getViewName()) ? temp.getViewName() : temp.getName();
            if (StringUtils.isBlank(name)) {
                name = enumInstance.name();
            }
        } else {
            // 默认
            name = enumInstance.name();
        }

        try {
            jsonGenerator.writeString(name);
        } catch (IOException ex) {
            log.error("枚举响应转换异常, 枚举[{}]原因: {}", enumInstance.name(), ex);
        }
    }
}
