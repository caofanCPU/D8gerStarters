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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Spring应用默认配置项
 *
 * @author D8GER
 */
@Component
public class SpringConfigProperties {
    /**
     * URL访问路径前缀
     */
    @Value("${server.servlet.context-path:}")
    public String contentPath;

    /**
     * 应用名称
     */
    @Value("${spring.application.name:}")
    public String applicationName;

    /**
     * 文件服务访问地址
     */
    @Value("${ms.file.url:NONE}")
    public String fileAccessUrl;

    /**
     * SSO访问地址
     */
    @Value("${ms.sso.url:NONE}")
    public String ssoAccessUrl;

    /**
     * LOCAL磁盘下载文件夹, 根据自身环境修改开发环境配置文件
     */
    @Value("${local.oss.download:NONE}")
    public String localOSSDownloadRoot;

    /**
     * LOCAL磁盘下载文件夹, 根据自身环境修改开发环境配置文件
     */
    @Value("${local.oss.upload:NONE}")
    public String localOSSUploadRoot;

}
