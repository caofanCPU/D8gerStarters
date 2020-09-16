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

package com.xyz.caofancpu.logger;

import com.xyz.caofancpu.constant.SymbolConstantUtil;
import com.xyz.caofancpu.core.CollectionUtil;
import com.xyz.caofancpu.logger.trace.ThreadTraceUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

/**
 * 日志记录工具
 *
 * @author D8GER
 */
public class LoggerUtil {
    private static final String LOGGER_FORMAT = "[{}]#[{}]#{}";

    /**
     * 字符串内容摘要长度阈值
     */
    public static final int SHORT_CONTENT_THRESHOLD_LENGTH = 500;

    /**
     * 字符串内容摘要
     *
     * @param content
     * @return
     */
    public static String shortenLogContent(String content) {
        return StringUtils.isNotBlank(content) || content.length() <= SHORT_CONTENT_THRESHOLD_LENGTH ? content.substring(0, SHORT_CONTENT_THRESHOLD_LENGTH) + SymbolConstantUtil.ELLIPSES : content;
    }

    /**
     * ERROR日志记录
     *
     * @param logger
     * @param title
     * @param params
     */
    public static void error(final Logger logger, String title, Object... params) {
        if (logger.isErrorEnabled()) {
            logger.error(LOGGER_FORMAT, ThreadTraceUtil.getTraceId(), title, formatLogMessage(params));
        }
    }

    /**
     * WARN日志记录
     *
     * @param logger
     * @param title
     * @param params
     */
    public static void warn(final Logger logger, String title, Object... params) {
        if (logger.isWarnEnabled()) {
            logger.warn(LOGGER_FORMAT, ThreadTraceUtil.getTraceId(), title, formatLogMessage(params));
        }
    }

    /**
     * INFO日志记录
     *
     * @param logger
     * @param title
     * @param params
     */
    public static void info(final Logger logger, String title, Object... params) {
        if (logger.isInfoEnabled()) {
            logger.info(LOGGER_FORMAT, ThreadTraceUtil.getTraceId(), title, formatLogMessage(params));
        }
    }

    /**
     * DEBUGGER日志记录
     *
     * @param logger
     * @param title
     * @param params
     */
    public static void debugger(final Logger logger, String title, Object... params) {
        if (logger.isDebugEnabled()) {
            logger.debug(LOGGER_FORMAT, ThreadTraceUtil.getTraceId(), title, formatLogMessage(params));
        }
    }

    /**
     * 格式化日志记录信息，奇位参数作为KEY，偶位作为VALUE
     * 当传入参数不是偶数个时，最后一个参数组的VALUE默认""
     *
     * @param params
     * @return
     */
    private static StringBuilder formatLogMessage(Object... params) {
        StringBuilder sb = new StringBuilder();
        if (CollectionUtil.isEmpty(params)) {
            return sb;
        }
        for (int index = 0; index < params.length; index += 2) {
            Object key = params[index];
            Object value;
            if (index == params.length - 1) {
                value = "NULL-日志调用方未传入参数值";
            } else {
                value = params[index + 1];
            }
            sb.append(key).append(":[").append(value).append("]#");
        }
        return sb;
    }
}
