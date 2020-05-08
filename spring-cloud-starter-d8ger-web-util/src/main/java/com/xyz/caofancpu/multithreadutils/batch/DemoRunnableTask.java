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
