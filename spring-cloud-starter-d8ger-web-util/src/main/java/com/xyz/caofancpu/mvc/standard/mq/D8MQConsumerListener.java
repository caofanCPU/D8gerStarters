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

import org.apache.rocketmq.common.message.MessageExt;

/**
 * MQ消费者监听器基类
 */
public interface D8MQConsumerListener {
    /**
     * 处理消息
     *
     * @param messageExt
     */
    void consumeMessage(MessageExt messageExt)
            throws Exception;

    /**
     * 处理消息3次都失败, 根据实际情况可将消息丢弃或存盘
     *
     * @param messageExt
     */
    void consumeFailed(MessageExt messageExt)
            throws Exception;

}
