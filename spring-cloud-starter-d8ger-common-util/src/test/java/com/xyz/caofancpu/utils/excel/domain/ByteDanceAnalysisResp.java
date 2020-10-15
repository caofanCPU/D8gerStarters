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

package com.xyz.caofancpu.utils.excel.domain;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 分析响应
 *
 * @author D8GER
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ByteDanceAnalysisResp implements Serializable {
    private List<ByteDanceResult> analysisResultList = Lists.newArrayList();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class ByteDanceResult implements Serializable {
        private String viewName;

        private Integer totalStudent;

        private Integer actualStudent;

        private Float maxScore;

        private Float minScore;

        private Float avgScore;

        private Float sigma;

        private String excellentRate;

        private String goodRate;

        private String passRate;

        private String failedRate;

        private Float difficulty;

        private Float distinction;

        private String teacherName;
    }

}