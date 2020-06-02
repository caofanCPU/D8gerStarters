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

package com.xyz.caofancpu.property;

import com.xyz.caofancpu.constant.D8gerConstants;
import com.xyz.caofancpu.constant.SymbolConstantUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * {@link ConfigurationProperties} for D8ger Web Util.
 *
 * @author D8GER
 */
@ConfigurationProperties(prefix = D8gerConstants.MAIL_PROPERTY_PREFIX)
@Validated
@Data
@Accessors(chain = true)
public class MailProperties {
    /**
     * 发件人邮箱地址, 默认空
     */
    private String fromEmailAddress = SymbolConstantUtil.EMPTY;

    /**
     * 发送邮件的主机, 默认smtp.exmail.qq.com
     */
    private String mailSendHost = "smtp.exmail.qq.com";

    /**
     * 开启SSL认证加密, 默认开启
     */
    private boolean enableSSL = true;

    /**
     * 授权码(授权码, 一般不是真实密码)
     */
    private String authPwd = SymbolConstantUtil.EMPTY;

}
