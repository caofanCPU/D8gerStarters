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

package com.xyz.caofancpu.middleware.mq;

import com.xyz.caofancpu.annotation.AttentionDoc;
import com.xyz.caofancpu.mvc.standard.mq.D8BaseMessage;
import com.xyz.caofancpu.mvc.standard.mq.D8BaseSendCallback;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * MQ默认发送回调函数
 *
 * @author D8GER
 */
@Component
@AllArgsConstructor
@Slf4j
@AttentionDoc("若想覆盖, 只需实现D8BaseSendCallback, 注册到容器中即可")
public class D8gerSendCallback implements D8BaseSendCallback {

    @Override
    public void onSuccess(D8BaseMessage d8BaseMessage)
            throws Exception {
        log.info("MQ消息发送成功, 回调处理仅记录日志");
    }

    @Override
    public void onFailed(D8BaseMessage d8BaseMessage)
            throws Exception {
        log.info("MQ消息发送失败, 回调处理将进行重复发送");
    }
}
