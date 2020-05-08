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
