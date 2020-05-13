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

package com.xyz.caofancpu.multithreadutils.batch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;


/**
 * demo
 *
 * @author D8GER
 */
@Data
@AllArgsConstructor
@Accessors(chain = true)
@Deprecated
public class DemoRunnableTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(DemoRunnableTask.class);

    private final Object a;

    private final CountDownLatch countDownLatch;

    @Override
    public void run() {
        try {
            synchronized (a) {
                // do something might cause parallel problem
            }
            // do other thing
        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            countDownLatch.countDown();
        }
    }

}
