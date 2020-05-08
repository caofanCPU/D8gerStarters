package com.xyz.caofancpu.extra;

import com.xyz.caofancpu.constant.SymbolConstantUtil;

import java.util.Objects;

/**
 * 测试常用工具类
 *
 * @author D8GER
 */
public class NormalUseForTestUtil {

    public static void outNextLine() {
        System.out.println(SymbolConstantUtil.EMPTY);
    }

    public static void out(Object text) {
        System.out.println(text);
    }

    public static void outWithoutLn(Object text) {
        System.out.print(text);
    }

    public static void outWithSpace(Object text) {
        System.out.print(text + SymbolConstantUtil.SPACE);
    }

    /**
     * 对象转String
     *
     * @param source
     * @return
     */
    public static String convertToString(Object source) {
        return Objects.isNull(source) ? null : source.toString();
    }

    /**
     * 对象转Integer
     *
     * @param source
     * @return
     */
    public static Integer convertToInteger(Object source) {
        if (Objects.isNull(source)) {
            return null;
        }
        return Integer.parseInt(source.toString());
    }

    /**
     * 对象转Long
     *
     * @param source
     * @return
     */
    public static Long convertToLong(Object source) {
        if (Objects.isNull(source)) {
            return null;
        }
        return Long.parseLong(source.toString());
    }
}
