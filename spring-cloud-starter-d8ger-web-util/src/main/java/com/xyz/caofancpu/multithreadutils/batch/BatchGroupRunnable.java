package com.xyz.caofancpu.multithreadutils.batch;


import com.xyz.caofancpu.logger.LoggerUtil;
import com.xyz.caofancpu.logger.trace.ThreadTraceUtil;
import com.xyz.caofancpu.logger.trace.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 批量任务封装对象
 *
 * @author D8GER
 */
public class BatchGroupRunnable implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(BatchGroupRunnable.class);
    private final AtomicInteger count;
    private final List<Runnable> runnableList;
    private final String threadTraceId;
    private boolean state = false;
    private Executor pool;
    private OnSuccessCallback onSuccessCallback;
    private OnUpdateRate onUpdateRate;

    public BatchGroupRunnable() {
        this.runnableList = new ArrayList<>();
        this.count = new AtomicInteger();
        this.threadTraceId = ThreadTraceUtil.getTraceId();
    }

    public BatchGroupRunnable(List<Runnable> runnableList) {
        this.runnableList = runnableList;
        this.count = new AtomicInteger(runnableList.size());
        this.threadTraceId = ThreadTraceUtil.getTraceId();
    }

    public BatchGroupRunnable(OnSuccessCallback onSuccessCallback) {
        this();
        this.onSuccessCallback = onSuccessCallback;
    }

    public BatchGroupRunnable(List<Runnable> runnableList, OnSuccessCallback onSuccessCallback) {
        this(runnableList);
        this.onSuccessCallback = onSuccessCallback;
    }

    @Override
    public void run() {
        execute(this);
    }

    public BatchGroupRunnable createBatchRunnable() {
        BatchGroupRunnable batchGroupRunnable = new BatchGroupRunnable();
        addRunnable(batchGroupRunnable);
        return batchGroupRunnable;
    }

    public BatchGroupRunnable addRunnable(Runnable runnable) {
        this.runnableList.add(runnable);
        this.count.getAndIncrement();
        return this;
    }

    public BatchGroupRunnable onSuccess(OnSuccessCallback onSuccessCallback) {
        this.onSuccessCallback = onSuccessCallback;
        return this;
    }

    public BatchGroupRunnable onUpdateRate(OnUpdateRate onUpdateRate) {
        this.onUpdateRate = onUpdateRate;
        return this;
    }

    public void execute(Executor executor, OnSuccessCallback onSuccessCallback) {
        this.onSuccessCallback = onSuccessCallback;
        execute(executor);
    }

    public void execute(Executor pool) {
        this.pool = pool;
        run();
    }

    private void execute(BatchGroupRunnable batchGroupRunnable) {
        batchGroupRunnable.state = true;
        if (batchGroupRunnable.onUpdateRate != null) {
            batchGroupRunnable.onUpdateRate.updateRate(batchGroupRunnable.runnableList.size(), 0);
        }
        for (Runnable runnable : batchGroupRunnable.runnableList) {
            if (runnable instanceof BatchGroupRunnable) {
                BatchGroupRunnable childRunnable = (BatchGroupRunnable) runnable;
                if (childRunnable.onSuccessCallback != null) {
                    OnSuccessCallback callback = childRunnable.onSuccessCallback;
                    childRunnable.onSuccess(() -> {
                        callback.callback();
                        batchGroupRunnable.countDown();
                    });
                } else {
                    childRunnable.onSuccess(batchGroupRunnable::countDown);
                }
                execute(childRunnable);
            } else {
                ItemRunnable itemRunnable = batchGroupRunnable.new ItemRunnable(runnable);
                if (this.pool == null) {
                    itemRunnable.run();
                } else {
                    this.pool.execute(itemRunnable);
                }
            }
        }
    }

    public synchronized void countDown() {
        int activeNum = count.decrementAndGet();
        if (onUpdateRate != null) {
            onUpdateRate.updateRate(runnableList.size(), runnableList.size() - activeNum);
        }
        if (activeNum == 0 && onSuccessCallback != null) {
            try {
                onSuccessCallback.callback();
            } catch (Exception e) {
                LoggerUtil.error(LOG, "线程回调异常", e, "threadTraceId", threadTraceId);
            }
        }
    }

    private class ItemRunnable implements Runnable {
        private final Runnable runnable;

        ItemRunnable(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            ThreadUtil.run(threadTraceId, runnable, BatchGroupRunnable.this::countDown);
        }
    }
}
