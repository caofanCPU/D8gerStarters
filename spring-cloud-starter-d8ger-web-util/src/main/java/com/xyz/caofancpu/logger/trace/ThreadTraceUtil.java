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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.security.SecureRandom;

/**
 * 线程追踪工具
 *
 * @author D8GER
 */
@Slf4j
public class ThreadTraceUtil {
    private static final String TRACE_ID_KEY = "ThreadTraceId";
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom random = new SecureRandom();

    /**
     * 开始追踪线程
     */
    public static void beginTrace() {
        long epoch = System.currentTimeMillis() / 1000;
        String traceId = encodeBase62(random.nextLong());
        MDC.put(TRACE_ID_KEY, epoch + "_" + traceId);
    }

    /**
     * 结束一次追踪线程
     */
    public static void endTrace() {
        MDC.remove(TRACE_ID_KEY);
    }

    /**
     * Base62编码
     */
    private static String encodeBase62(long num) {
        num = Math.abs(num);
        StringBuilder sb = new StringBuilder();
        for (; num > 0; num /= ALPHABET.length()) {
            sb.append(ALPHABET.charAt((int) (num % ALPHABET.length())));
        }
        return sb.toString();
    }

    /**
     * 获取线程追踪ID
     *
     * @return
     */
    public static String getTraceId() {
        String traceId = MDC.get(TRACE_ID_KEY);
        return StringUtils.isBlank(traceId) ? StringUtils.EMPTY : traceId;
    }

}
