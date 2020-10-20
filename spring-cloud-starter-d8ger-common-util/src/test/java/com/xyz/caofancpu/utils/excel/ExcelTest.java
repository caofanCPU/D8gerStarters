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

package com.xyz.caofancpu.utils.excel;

import com.google.common.collect.Lists;
import com.xyz.caofancpu.core.CollectionUtil;
import com.xyz.caofancpu.core.FileUtil;
import com.xyz.caofancpu.core.JSONUtil;
import com.xyz.caofancpu.excel.core.PoiBook;
import com.xyz.caofancpu.excel.core.PoiColumns;
import com.xyz.caofancpu.excel.core.PoiSheet;
import com.xyz.caofancpu.excel.core.PoiStyle;
import com.xyz.caofancpu.excel.core.PoiTable1;
import com.xyz.caofancpu.excel.core.PoiTable3;
import com.xyz.caofancpu.excel.util.PoiUtil;
import com.xyz.caofancpu.extra.NormalUseForTestUtil;
import com.xyz.caofancpu.utils.excel.domain.ByteDanceAnalysisResp;
import com.xyz.caofancpu.utils.excel.domain.FakerAnalysisResp;
import com.xyz.caofancpu.utils.excel.domain.QPSAnalysisResp;
import com.xyz.caofancpu.utils.excel.domain.ThreeLevelAnalysisResp;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.BooleanUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Excel样例
 *
 * @author D8GER
 */
@RunWith(JUnit4.class)
public class ExcelTest {

    @Before
    public void before() {
        NormalUseForTestUtil.out("---------测试前---------");
    }

    @After
    public void after() {
        NormalUseForTestUtil.out("---------测试后---------");
    }

    @Test
    public void testExample1() {
        boolean onlyGroup = false;
        QPSAnalysisResp analysisResp = new QPSAnalysisResp();

        PoiBook poiBook = PoiBook.newSXSSFBook("小题分分析");
        //样式
        PoiStyle centerPoiStyle = PoiUtil.getCenterPoiStyle();
        PoiSheet poiSheet = poiBook.addSheet(new PoiSheet("Sheet0"));
        // 设置打印样式
        poiSheet.setPrintSetupScale((short) 93);
        poiSheet.addTable(new PoiTable1<QPSAnalysisResp.QPSAnalysisResult>(analysisResp.getAnalysisResultList()) {{
            addColumn1(QPSAnalysisResp.QPSAnalysisResult::getGroupName, "班级");
            if (BooleanUtils.isFalse(onlyGroup)) {
                addColumn1(QPSAnalysisResp.QPSAnalysisResult::getStudentName, "学生");
                addColumn1(QPSAnalysisResp.QPSAnalysisResult::getSchoolNo, "学校学号");
                addColumn1(QPSAnalysisResp.QPSAnalysisResult::getUuid, "会课学号");
            }
            addColumns1(new PoiColumns<QPSAnalysisResp.QPSAnalysisResult, QPSAnalysisResp.QPSInfo>(QPSAnalysisResp.QPSAnalysisResult::getQuestionResultList) {{
                addColumn(QPSAnalysisResp.QPSInfo::getScore, QPSAnalysisResp.QPSInfo::getQuestionName).setDefaultValue("--");
            }});
        }}).setTitleStyle(centerPoiStyle).setStyle(centerPoiStyle);
    }

    @Test
    public void testExample2() {
        boolean onlyGroup = false;
        FakerAnalysisResp analysisResp = new FakerAnalysisResp();

        PoiBook poiBook = PoiBook.newSXSSFBook("小题分分析");
        //样式
        PoiStyle centerPoiStyle = PoiUtil.getCenterPoiStyle();
        PoiSheet poiSheet = poiBook.addSheet(new PoiSheet("Sheet0"));
        // 设置打印样式
        poiSheet.setPrintSetupScale((short) 93);
        poiSheet.addTable(new PoiTable1<FakerAnalysisResp.FakerResult>(analysisResp.getAnalysisResultList()) {{
            addColumn1(FakerAnalysisResp.FakerResult::getGroupName, "班级");
            if (BooleanUtils.isFalse(onlyGroup)) {
                addColumn1(FakerAnalysisResp.FakerResult::getStudentName, "姓名");
                addColumn1(FakerAnalysisResp.FakerResult::getSchoolNo, "学校学号");
                addColumn1(FakerAnalysisResp.FakerResult::getUuid, "会课学号");
            }
            addColumns1(new PoiColumns<FakerAnalysisResp.FakerResult, FakerAnalysisResp.SubjectResult>(FakerAnalysisResp.FakerResult::getSubjectResultList, FakerAnalysisResp.SubjectResult::getSubjectName) {{
                if (BooleanUtils.isFalse(onlyGroup)) {
                    addColumn(FakerAnalysisResp.SubjectResult::getScore, "成绩").setDefaultValue("--");
                    addColumn(FakerAnalysisResp.SubjectResult::getGroupRank, "班级排名").setDefaultValue("--");
                    addColumn(FakerAnalysisResp.SubjectResult::getGradeRank, "年级排名").setDefaultValue("--");
                } else {
                    addColumn(FakerAnalysisResp.SubjectResult::getScore, "均分").setDefaultValue("--");
                    addColumn(FakerAnalysisResp.SubjectResult::getGradeRank, "排名").setDefaultValue("--");
                }
            }});
        }}).setTitleStyle(centerPoiStyle).setStyle(centerPoiStyle);
    }

    @Test
    public void testExample3()
            throws Exception {
        String baseDirFullPath = "/Users/D8GER/Desktop/CAOFAN/spring-cloud-d8ger-starters/spring-cloud-starter-d8ger-common-util/src/test/java/com/xyz/caofancpu/utils/excel/datasource";
        String dataSourceFullPath = baseDirFullPath + File.separator + "ByteDanceData.json";
        ByteDanceAnalysisResp analysisResp = JSONUtil.deserializeJSON(FileUtil.readFileToString(dataSourceFullPath), ByteDanceAnalysisResp.class);
        PoiBook poiBook = PoiBook.newSXSSFBook("再皮一下");
        //样式
        PoiStyle centerPoiStyle = PoiUtil.getCenterPoiStyle();
        PoiSheet poiSheet = poiBook.addSheet(new PoiSheet("Sheet0"));
        // 设置打印样式
        poiSheet.setPrintSetupScale((short) 93);
        poiSheet.addTable(new PoiTable1<ByteDanceAnalysisResp.ByteDanceResult>(analysisResp.getAnalysisResultList()) {{
            addColumn1(ByteDanceAnalysisResp.ByteDanceResult::getViewName, "项目");
            addColumn1(ByteDanceAnalysisResp.ByteDanceResult::getTotalStudent, "高三年级", "应考人数");
            addColumn1(ByteDanceAnalysisResp.ByteDanceResult::getActualStudent, "高三年级", "实考人数");
            addColumn1(ByteDanceAnalysisResp.ByteDanceResult::getMaxScore, "高三年级", "关键数据", "最高分");
            addColumn1(ByteDanceAnalysisResp.ByteDanceResult::getMinScore, "高三年级", "关键数据", "最低分");
            addColumn1(ByteDanceAnalysisResp.ByteDanceResult::getAvgScore, "高三年级", "关键数据", "平均分");
            addColumn1(ByteDanceAnalysisResp.ByteDanceResult::getSigma, "标准差");
            addColumn1(ByteDanceAnalysisResp.ByteDanceResult::getExcellentRate, "比率", "优秀");
            addColumn1(ByteDanceAnalysisResp.ByteDanceResult::getGoodRate, "比率", "良好");
            addColumn1(ByteDanceAnalysisResp.ByteDanceResult::getPassRate, "比率", "及格");
            addColumn1(ByteDanceAnalysisResp.ByteDanceResult::getFailedRate, "比率", "不及格");
            addColumn1(ByteDanceAnalysisResp.ByteDanceResult::getDifficulty, "难度");
            addColumn1(ByteDanceAnalysisResp.ByteDanceResult::getDistinction, "区分度");
            addColumn1(ByteDanceAnalysisResp.ByteDanceResult::getTeacherName, "带头大哥").setDefaultValue("--");
        }}).setTitleStyle(centerPoiStyle).setStyle(centerPoiStyle);
        String excelSaveFullPath = baseDirFullPath + File.separator + poiBook.getFileName();
        FileUtil.saveExcelFile(excelSaveFullPath, poiBook.buildWorkbook());
    }

    @Test
    public void testComplete()
            throws Exception {
        String baseDirFullPath = "/Users/D8GER/Desktop/CAOFAN/spring-cloud-d8ger-starters/spring-cloud-starter-d8ger-common-util/src/test/java/com/xyz/caofancpu/utils/excel/datasource";
        String dataSourceFullPath = baseDirFullPath + File.separator + "TestCompleteData.json";
        ThreeLevelAnalysisResp analysisResp = JSONUtil.deserializeJSON(FileUtil.readFileToString(dataSourceFullPath), ThreeLevelAnalysisResp.class);
        PoiBook poiBook = PoiBook.newSXSSFBook("就这");
        // 常规居中样式, 用户表格全局样式
        PoiStyle center = PoiUtil.getCenterPoiStyle();
        // 测试: 黄底红字
        PoiStyle yellowBgRedFt = PoiUtil.getRedFrontAndYellowBgCenterPoiStyle();
        // 测试: 绿字
        PoiStyle greenFont = PoiUtil.getGreenCenterPoiStyle();
        // 测试: 数字绿涨红跌, 平均分avgScore
        PoiStyle numberRefer65 = PoiUtil.getNumberArrowCenterPoiStyle();
        // 测试: 数字红涨绿跌, 难度系数difficulty
        PoiStyle reverseNumberRefer = PoiUtil.getNumberArrowCenterPoiStyle();
        // 测试: 数字百分比红涨绿跌, 优秀率excellentRate
        PoiStyle numberPercentRefer1 = PoiUtil.getNumberPercentArrowCenterPoiStyle(2);
        // 测试: 数字百分比红涨绿跌, 良好率goodRate
        PoiStyle reverseNumberPercentRefer20 = PoiUtil.getNumberPercentArrowCenterPoiStyle(2, true);
        PoiSheet poiSheet = poiBook.addSheet(new PoiSheet("帝八哥"));
        // 设置打印样式
        poiSheet.setPrintSetupScale((short) 93);
        poiSheet.addTable(new PoiTable3<ThreeLevelAnalysisResp.ExamResult, ThreeLevelAnalysisResp.SubjectResult, ThreeLevelAnalysisResp.GroupResult>(analysisResp.getExamResultList(), ThreeLevelAnalysisResp.ExamResult::getSubjectResultList, ThreeLevelAnalysisResp.SubjectResult::getGroupResultList) {{
            // 两列数据, 多级列标题形式:                考试信息
            //                            考试编号       |       考试名称
            // 取第一层数据, 1对应PoiTable3的第一个构造参数
            addColumn1(ThreeLevelAnalysisResp.ExamResult::getExamNo, "考试信息", "考试编号")
                    .setDefaultValue("--")
                    // 该列标题为黄底红字
                    .setTitleStyle(yellowBgRedFt)
                    // 该列除标题行的数据列为绿色字体样式
                    .setStyle(greenFont);
            addColumn1(ThreeLevelAnalysisResp.ExamResult::getExamName, "考试信息", "考试名称").setDefaultValue("--");

            // 两列数据, 一般形式:           科目编码       |       科目名称
            // 取第二层数据, 2对应PoiTable3的第二个构造参数
            addColumn2(ThreeLevelAnalysisResp.SubjectResult::getSubjectCode, "科目编码").setDefaultValue("--");
            addColumn2(ThreeLevelAnalysisResp.SubjectResult::getSubjectName, "科目名称").setDefaultValue("--");
            // 取第三层数据, 3对应PoiTable3的第三个构造参数
            addColumn3(ThreeLevelAnalysisResp.GroupResult::getGroupId, "班级信息", "ID").setDefaultValue("--");
            addColumn3(ThreeLevelAnalysisResp.GroupResult::getGroupName, "班级信息", "名称").setDefaultValue("--");
            // 取第三层数据, 并且添加多列, 前面1->2->3是在拓展excel的行, 此处相当于是拓展excel的列
            addColumns3(new PoiColumns<ThreeLevelAnalysisResp.GroupResult, ThreeLevelAnalysisResp.AnalysisResult>(ThreeLevelAnalysisResp.GroupResult::getAnalysisResultList, item -> "第[" + item.getReviewOrderId() + "]次审核结果") {{
                addColumn(ThreeLevelAnalysisResp.AnalysisResult::getTotalStudent, "应考人数").setDefaultValue("--");
                addColumn(ThreeLevelAnalysisResp.AnalysisResult::getActualStudent, "实考人数").setDefaultValue("--");
                addColumn(ThreeLevelAnalysisResp.AnalysisResult::getMaxScore, "关键数据", "最高分").setDefaultValue("--");
                addColumn(ThreeLevelAnalysisResp.AnalysisResult::getMinScore, "关键数据", "最低分").setDefaultValue("--");
                addColumn(ThreeLevelAnalysisResp.AnalysisResult::getAvgScore, "关键数据", "平均分").setDefaultValue("--").setStyle(numberRefer65);
                addColumn(ThreeLevelAnalysisResp.AnalysisResult::getSigma, "标准差").setDefaultValue("--");
                addColumn(ThreeLevelAnalysisResp.AnalysisResult::getExcellentRate, "比率", "优秀").setDefaultValue("--").setStyle(numberPercentRefer1);
                addColumn(ThreeLevelAnalysisResp.AnalysisResult::getGoodRate, "比率", "良好").setDefaultValue("--").setStyle(reverseNumberPercentRefer20);
                addColumn(ThreeLevelAnalysisResp.AnalysisResult::getPassRate, "比率", "及格").setDefaultValue("--");
                addColumn(ThreeLevelAnalysisResp.AnalysisResult::getFailedRate, "比率", "不及格").setDefaultValue("--");
                addColumn(ThreeLevelAnalysisResp.AnalysisResult::getDifficulty, "难度系数").setDefaultValue("--").setStyle(reverseNumberRefer);
                addColumn(ThreeLevelAnalysisResp.AnalysisResult::getDistinction, "区分度").setDefaultValue("--");
                addColumn(ThreeLevelAnalysisResp.AnalysisResult::getReviewerId, "审核人", "工号").setDefaultValue("-1");
                addColumn(ThreeLevelAnalysisResp.AnalysisResult::getReviewerName, "审核人", "姓名").setDefaultValue("系统");
            }});
        }}).setTitleStyle(center).setStyle(center);
        // 添加5个空行
        poiSheet.addWhiteRowSplit(5);
        //写一行数据, 该行区域指定 3个单元行*10个单元列
        poiSheet.addRow(3, 10, "帝八哥在这里").setStyle(yellowBgRedFt);
        // 合并指定区域的单元格
        PoiUtil.mergedRegion(poiBook, poiSheet, 17, 1, 20, 4, "合并后的单元格", greenFont);
        String excelSaveFullPath = baseDirFullPath + File.separator + poiBook.getFileName();
        FileUtil.saveExcelFile(excelSaveFullPath, poiBook.buildWorkbook());
    }


    /**
     * 1.支持标题自动合并
     * 2.只需要DataMo, 无任何侵入, 根据简易的lambda表达式指定数据填充行为, 即可完成数据渲染
     * 3.样式支持
     * 4.指定区域单元格合并
     */
    @Test
    public void lambdaExcel() {
        List<GameArea> sourceData = initData();
        NormalUseForTestUtil.out(JSONUtil.formatStandardJSON(sourceData));
    }

    public List<GameArea> initData() {
        return Lists.newArrayList(new GameArea().setId(1004321).setName("G2.VS.TES").setTeamList(initGroupA()),
                new GameArea().setId(2334567).setName("SN.VS.DWG").setTeamList(initGroupB()),
                new GameArea().setId(2334567).setName("SSSSSS").setTeamList(initGroupC())

        );
    }

    public List<Team> initGroupA() {
        return Lists.newArrayList(new Team().setId(998).setName("火箭班").setAvgScore(733.2F).setPlayerList(initStudent(2020, 2024)),
                new Team().setId(369).setName("平行班").setAvgScore(487.0F).setPlayerList(initStudent(455, 459))
        );
    }

    public List<Team> initGroupB() {
        return Lists.newArrayList(new Team().setId(108).setName("高三2班").setAvgScore(434.0F).setPlayerList(initStudent(777, 781)));
    }

    public List<Team> initGroupC() {
        return Lists.newArrayList(new Team().setId(202).setName("高三1班").setAvgScore(233.2F).setPlayerList(initStudent(1688, 1692)));
    }

    public List<Player> initStudent(int a, int b) {
        int mid = (a + b) / 2;
        return CollectionUtil.transToList(IntStream.rangeClosed(a, b).boxed().collect(Collectors.toList()),
                id -> new Player().setId(id).setName("学生" + id).setAge(id - a).setTeamId(id + a).setKillsPerGame(id - mid).setPercentScoreKDA((id - mid) / 1.0f)
        );
    }

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
    }

}