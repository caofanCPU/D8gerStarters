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
 * 三层表格
 *
 * @author D8GER
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ThreeLevelAnalysisResp implements Serializable {
    /**
     * 考试列表数据
     */
    private List<ExamResult> examResultList = Lists.newArrayList();

    /**
     * 考试分析数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class ExamResult implements Serializable {
        /**
         * 考试号
         */
        private String examNo;
        /**
         * 考试名称
         */
        private String examName;
        /**
         * 科目列表数据
         */
        private List<SubjectResult> subjectResultList = Lists.newArrayList();
    }

    /**
     * 科目分析数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class SubjectResult implements Serializable {
        /**
         * 科目号
         */
        private Integer subjectCode;
        /**
         * 科目名称
         */
        private String subjectName;
        /**
         * 班级列表数据
         */
        private List<GroupResult> groupResultList = Lists.newArrayList();
    }

    /**
     * 班级分析数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class GroupResult implements Serializable {
        /**
         * 班级编号
         */
        private Long groupId;
        /**
         * 班级名称
         */
        private String groupName;

        /**
         * 有效数据列表
         */
        private List<AnalysisResult> analysisResultList = Lists.newArrayList();
    }

    /**
     * 有效数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class AnalysisResult implements Serializable {
        /**
         * 分析结果序次号
         */
        private Integer reviewOrderId;

        /**
         * 应考人数
         */
        private Integer totalStudent;

        /**
         * 实考人数
         */
        private Integer actualStudent;

        /**
         * 最高分
         */
        private Float maxScore;

        /**
         * 最低分
         */
        private Float minScore;

        /**
         * 平均分
         */
        private Float avgScore;

        /**
         * 标准差
         */
        private Float sigma;

        /**
         * 优秀率(百分比数值)
         */
        private Float excellentRate;

        /**
         * 良好率(百分比数值)
         */
        private Float goodRate;

        /**
         * 及格率(百分比数值)
         */
        private Float passRate;

        /**
         * 不及格率(百分比数值)
         */
        private Float failedRate;

        /**
         * 难度系数
         */
        private Float difficulty;

        /**
         * 有效区分度
         */
        private Float distinction;

        /**
         * 复核人工号
         */
        private Long reviewerId;

        /**
         * 复核人姓名
         */
        private String reviewerName;
    }

}