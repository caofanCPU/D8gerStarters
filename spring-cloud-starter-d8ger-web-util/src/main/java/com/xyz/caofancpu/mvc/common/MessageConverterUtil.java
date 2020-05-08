package com.xyz.caofancpu.mvc.common;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Created by caofanCPU on 2018/8/6.
 */
@Slf4j
public class MessageConverterUtil {
    /**
     * 将JSON字符串转为消息对象
     *
     * @param jsonString
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T convertJsonToMessage(String jsonString, Class<T> clazz) {
        if (StringUtils.isBlank(jsonString) || Objects.isNull(clazz)) {
            throw new IllegalArgumentException("非法的入参");
        }
        return JSONObject.parseObject(jsonString, clazz);
    }

}
