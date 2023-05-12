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

import com.google.common.collect.Lists;
import com.xyz.caofancpu.constant.D8gerConstants;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * {@link ConfigurationProperties} for D8ger Web Util.
 *
 * @author D8GER
 */
@ConfigurationProperties(prefix = D8gerConstants.SWAGGER_PROPERTY_PREFIX)
@Validated
@Data
@Accessors(chain = true)
public class SwaggerProperties {
    /**
     * API文档展示开关
     */
    private boolean showApi = true;

    /**
     * SwaggerAPI文档项目配置信息
     */
    private D8Project project = new D8Project();

    /**
     * 请求头配置信息
     */
    private List<D8HeaderParameter> headerParameters = Lists.newArrayList();

    @Data
    public static class D8Project {
        /**
         * 标题信息
         */
        private String title = "Swagger自动API文档";

        /**
         * 描述
         */
        private String description = "Swagger-UI自动生成接口文档";

        /**
         * 版本号
         */
        private String version = "1.0.0-SNAPSHOT";

        /**
         * 联系人姓名
         */
        private String contactName = "D8ger";

        /**
         * 联系人访问链接
         */
        private String contactUrl = "https://github.com/caofanCPU";

        /**
         * 联系人邮箱
         */
        private String contactEMail = "xyb5to0ZCY@gmail.com";
    }

    @Data
    public static class D8HeaderParameter {
        /**
         * 展示顺序
         */
        private int order = 0;
        /**
         * 请求头名称
         */
        private String name;
        /**
         * 默认值
         */
        private String defaultValue;
        /**
         * 描述
         */
        private String description;
        /**
         * 类型简称, 例如'string', 'int'
         */
        private String typeShortName = "string";
        /**
         * 是否必传
         */
        private boolean required;

    }
}
