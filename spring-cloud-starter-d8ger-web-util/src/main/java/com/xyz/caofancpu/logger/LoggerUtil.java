package com.xyz.caofancpu.logger;

import com.xyz.caofancpu.core.CollectionUtil;
import com.xyz.caofancpu.logger.trace.ThreadTraceUtil;
import org.slf4j.Logger;

/**
 * 日志记录工具
 *
 * @author D8GER
 */
public class LoggerUtil {
    private static final String LOGGER_FORMAT = "[{}]#[{}]#{}";

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
            sb.append(key).append(":[").append(value).append("]$");
        }
        return sb;
    }
}
