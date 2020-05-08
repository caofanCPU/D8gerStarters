package com.xyz.caofancpu.multithreadutils.remote;

/**
 * 远程调用封装服务
 *
 * @author D8GER
 */
@FunctionalInterface
public interface RemoteInvoke<T> {
    T invoke();
}
