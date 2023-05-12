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

import com.xyz.caofancpu.core.CollectionFunUtil;
import com.xyz.caofancpu.core.FileUtil;
import com.xyz.caofancpu.logger.trace.ThreadTraceUtil;
import com.xyz.caofancpu.multithreadutils.batch.BatchGroupRunnable;
import com.xyz.caofancpu.multithreadutils.batch.DebuggerKingRunnable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 多线程批量任务测试
 *
 * @author D8GER
 */
public class BatchGroupRunnableTest {

    private static final String FILE_BASE_PATH = "/Users/htuser-085/Desktop/CAOFAN/IDEA-WORK/DebuggerBoot/DebuggerKingUtil/src/main/java/com/xyz/caofancpu/util/multithreadutils/textFile";

    public static void main(String[] args) {
        ThreadTraceUtil.beginTrace();
        ThreadPoolTaskExecutor pool = getStandardThreadPool();
        List<Integer> oddList = Arrays.asList(1, 3, 7);
        List<Integer> evenList = Arrays.asList(4, 8, 10);
        String oddFilePath = FILE_BASE_PATH + File.separator + "odd.txt";
        String evenFilePath = FILE_BASE_PATH + File.separator + "even.txt";
        BatchGroupRunnable batchGroupRunnable = new BatchGroupRunnable();
        batchGroupRunnable.onUpdateRate((total, currentValue) -> {
            if (total == 0) {
                System.out.println("XXXXXXXXXXXXXXXXXXX");
            }
            float rate = (currentValue + 0f) / total * 100;
            System.out.println("总体进度: " + rate);
        });
        batchGroupRunnable.onSuccess(() -> System.out.println("任务执行完成！"));
        createGroupTask(batchGroupRunnable, oddList, oddFilePath);
        createGroupTask(batchGroupRunnable, evenList, evenFilePath);
        batchGroupRunnable.execute(pool);
        pool.shutdown();
        ThreadTraceUtil.endTrace();
    }

    private static void createGroupTask(BatchGroupRunnable batchGroupRunnable, List<Integer> taskIdList, String filePath) {
        ConcurrentLinkedDeque<String> concurrentLinkedDeque = new ConcurrentLinkedDeque<>();
        BatchGroupRunnable currentBatchRunnable = batchGroupRunnable.createBatchRunnable();
        for (Integer taskId : taskIdList) {
            currentBatchRunnable.addRunnable(new DebuggerKingRunnable("执行任务[" + taskId + "]", concurrentLinkedDeque));
        }
        currentBatchRunnable.onSuccess(() -> {
            List<String> contentLines = new ArrayList<>(concurrentLinkedDeque);
            try {
                FileUtil.writeStringToFile(CollectionFunUtil.join(contentLines, "\n"), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static ThreadPoolTaskExecutor getStandardThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(16);
        executor.setMaxPoolSize(64);
        executor.setQueueCapacity(16);
        executor.setThreadNamePrefix("debugger_");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setKeepAliveSeconds(60);
        executor.initialize();
        return executor;
    }

}
