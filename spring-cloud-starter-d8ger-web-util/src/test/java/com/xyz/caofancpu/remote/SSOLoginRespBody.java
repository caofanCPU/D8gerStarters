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

package com.xyz.caofancpu.remote;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author ht-caofan
 */
@Data
@Accessors(chain = true)
public class SSOLoginRespBody implements Serializable {
    /**
     * 主账号令牌
     */
    private String refreshToken;
    /**
     * 访问子系统应用码
     */
    private int accessAppCode;
    /**
     * 子系统访问令牌
     */
    private String accessToken;
    /**
     * 是否更新子系统访问令牌
     */
    private boolean updateAccessToken;

}
