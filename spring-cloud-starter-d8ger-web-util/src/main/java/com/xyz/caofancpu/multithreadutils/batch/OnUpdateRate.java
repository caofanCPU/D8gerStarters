package com.xyz.caofancpu.multithreadutils.batch;

/**
 * 函数式接口，支持计算比率
 *
 * @author D8GER
 */
@FunctionalInterface
public interface OnUpdateRate {
    void updateRate(int total, int current);
}
