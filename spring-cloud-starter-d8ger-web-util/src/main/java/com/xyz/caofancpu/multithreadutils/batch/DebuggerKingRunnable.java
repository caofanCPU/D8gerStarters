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

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 任务对象
 *
 * @author D8GER
 */
@Slf4j
@Deprecated
public class DebuggerKingRunnable implements Runnable {

    private Runnable task;
    private String taskDescription;
    private ConcurrentLinkedDeque<String> concurrentLinkedDeque;

    public DebuggerKingRunnable(Runnable task) {
        this.task = task;
    }

    public DebuggerKingRunnable(String taskDescription, ConcurrentLinkedDeque<String> concurrentLinkedDeque) {
        this.taskDescription = taskDescription;
        this.concurrentLinkedDeque = concurrentLinkedDeque;
    }

    @Override
    public void run() {
        begin();
        try {
            handle();
        } catch (Exception e) {
            log.error("error...", e);
        } finally {
            after();
        }
    }

    private void begin() {
        System.out.println(taskDescription);
    }

    private void handle() {
        concurrentLinkedDeque.add(taskDescription);
    }

    private void after() {

    }

}
