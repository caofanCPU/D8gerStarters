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
