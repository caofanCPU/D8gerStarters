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

package com.xyz.caofancpu.mvc.standard.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * MQ消息基类
 *
 * @author D8GER
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class D8BaseMessage<T extends Serializable> implements Serializable {
    /**
     * 消息主题
     */
    private String topic;

    /**
     * 消息主题下子分类
     */
    private String tag;

    /**
     * 消息唯一标识码
     */
    private String key;

    /**
     * 是否需要有序消费, 默认否
     */
    private Boolean consumeOrdered = false;

    /**
     * 消息数据
     */
    private T data;
}
