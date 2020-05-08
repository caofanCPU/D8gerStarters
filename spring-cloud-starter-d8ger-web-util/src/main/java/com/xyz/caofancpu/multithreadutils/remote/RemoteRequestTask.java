package com.xyz.caofancpu.multithreadutils.remote;

import java.util.concurrent.Callable;

/**
 * 远程调用任务请求封装对象
 *
 * @author D8GER
 */
public class RemoteRequestTask<K> implements Callable<K> {
    private RemoteInvoke<K> remoteInvoke;

    public RemoteRequestTask(RemoteInvoke<K> remoteInvoke) {
        this.remoteInvoke = remoteInvoke;
    }

    public RemoteInvoke<K> getRemoteInvoke() {
        return remoteInvoke;
    }

    public void setRemoteInvoke(RemoteInvokeHelper<K> remoteInvoke) {
        this.remoteInvoke = remoteInvoke;
    }

    @Override
    public K call() {
        return remoteInvoke.invoke();
    }
}
