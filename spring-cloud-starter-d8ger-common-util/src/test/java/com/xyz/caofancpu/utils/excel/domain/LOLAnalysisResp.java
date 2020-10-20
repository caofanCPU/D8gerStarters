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
public class LOLAnalysisResp implements Serializable {
    /**
     * 赛区列表数据
     */
    private List<GameArea> gameAreaList = Lists.newArrayList();


    @Data
    @Accessors(chain = true)
    public static class GameArea implements Serializable {
        /**
         * 赛区ID
         */
        private Integer id;
        /**
         * 赛区名称
         */
        private String name;
        /**
         * 战队列表
         */
        private List<Team> teamList = Lists.newArrayList();
    }

    @Data
    @Accessors(chain = true)
    public static class Team implements Serializable {
        /**
         * 战队ID
         */
        private Integer id;
        /**
         * 战队名称
         */
        private String name;
        /**
         * 所属赛区ID
         */
        private Integer gameAreaId;
        /**
         * 战队人均KDA
         */
        private Float avgScore;
        /**
         * 选手列表
         */
        private List<Player> playerList = Lists.newArrayList();
    }

    @Data
    @Accessors(chain = true)
    public static class Player implements Serializable {
        /**
         * 选手ID
         */
        private Integer id;
        /**
         * 选手姓名
         */
        private String name;
        /**
         * 选手年龄
         */
        private Integer age;
        /**
         * 选手所属战队ID
         */
        private Integer teamId;
        /**
         * 参考上赛季, 场均击杀, 示例值, 10, 0, -3
         */
        private Integer killsPerGame;
        /**
         * 参考上赛季, KDA成长比例, 百分比小数表示, 示例值, 0.88, 0, -0.32
         */
        private Float percentScoreKDA;
        /**
         * 近三场战绩
         */
        private List<ResultScore> latest3List = Lists.newArrayList();
    }

    @Data
    @Accessors(chain = true)
    public static class ResultScore implements Serializable {
        /**
         * 场次索引
         */
        private Integer index;
        /**
         * KDA
         */
        private Float kda;
        /**
         * 是否赢得比赛
         */
        private Boolean hasWin;
    }
}