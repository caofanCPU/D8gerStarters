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

package com.xyz.caofancpu.logger.trace;

import com.xyz.caofancpu.logger.LoggerUtil;
import com.xyz.caofancpu.multithreadutils.batch.OnSuccessCallback;
import lombok.extern.slf4j.Slf4j;

/**
 * 线程相关
 *
 * @author D8GER
 */
@Slf4j
public class ThreadUtil {
    public static void run(String threadTraceId, Runnable runnable, OnSuccessCallback success) {
        try {
            ThreadTraceUtil.beginTrace();
            LoggerUtil.info(log, "线程执行开始", "父线程traceId", threadTraceId);
            runnable.run();
            LoggerUtil.info(log, "线程执行结束", "父线程traceId", threadTraceId);
        } catch (Throwable e) {
            LoggerUtil.error(log, "线程执行异常", e, "traceId", threadTraceId);
        } finally {
            if (success != null) {
                try {
                    success.callback();
                } catch (Exception e) {
                    LoggerUtil.error(log, "线程结束.回调异常", e, "traceId", threadTraceId);
                }
            }
            ThreadTraceUtil.endTrace();
        }
    }
}
