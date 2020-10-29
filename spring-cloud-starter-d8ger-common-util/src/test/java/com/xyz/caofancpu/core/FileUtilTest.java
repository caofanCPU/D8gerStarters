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

package com.xyz.caofancpu.core;

import com.xyz.caofancpu.extra.NormalUseForTestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@RunWith(JUnit4.class)
public class FileUtilTest {

    /**
     * 把原始字符串分割成指定长度的字符串列表
     *
     * @param inputString 原始字符串
     * @param length      指定长度
     * @return
     */
    public static List<String> getStrList(String inputString, int length) {
        int size = inputString.length() / length;
        if (inputString.length() % length != 0) {
            size += 1;
        }
        return getStrList(inputString, length, size);
    }

    /**
     * 把原始字符串分割成指定长度的字符串列表
     *
     * @param inputString 原始字符串
     * @param length      指定长度
     * @param size        指定列表大小
     * @return
     */
    public static List<String> getStrList(
            String inputString, int length,
            int size) {
        List<String> list = new ArrayList<String>();
        for (int index = 0; index < size; index++) {
            String childStr = substring(inputString, index * length,
                    (index + 1) * length);
            list.add(childStr);
        }
        return list;
    }

    /**
     * 分割字符串，如果开始位置大于字符串长度，返回空
     *
     * @param str 原始字符串
     * @param f   开始位置
     * @param t   结束位置
     * @return
     */
    public static String substring(String str, int f, int t) {
        if (f > str.length())
            return null;
        if (t > str.length()) {
            return str.substring(f);
        } else {
            return str.substring(f, t);
        }
    }

    @Test
    public void encodeBase64()
            throws Exception {
//        String tina = "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";
        String path = "/Users/D8GER/Downloads/BiteApple.png";
        String text = FileUtil.encodeBase64(path);
        NormalUseForTestUtil.out(text);
        NormalUseForTestUtil.out(FileUtil.svgContext(path));
    }
}