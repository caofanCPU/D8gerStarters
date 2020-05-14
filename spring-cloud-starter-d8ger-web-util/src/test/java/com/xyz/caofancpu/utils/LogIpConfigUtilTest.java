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

package com.xyz.caofancpu.utils;

import com.xyz.caofancpu.extra.NormalUseForTestUtil;
import com.xyz.caofancpu.logger.LogIpConfigUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 获取真实IP测试
 *
 * @author D8GER
 */
public class LogIpConfigUtilTest {

    @Before
    public void setUp()
            throws Exception {
    }

    @After
    public void tearDown()
            throws Exception {
    }

    @Test
    public void getIpAddress()
            throws Exception {
        NormalUseForTestUtil.out(LogIpConfigUtil.getIpAddress());
    }
}