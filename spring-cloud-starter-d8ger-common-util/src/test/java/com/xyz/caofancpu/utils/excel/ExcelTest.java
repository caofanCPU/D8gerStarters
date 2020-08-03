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

import com.xyz.caofancpu.excel.core.PoiBook;
import com.xyz.caofancpu.excel.core.PoiColumns;
import com.xyz.caofancpu.excel.core.PoiSheet;
import com.xyz.caofancpu.excel.core.PoiStyle;
import com.xyz.caofancpu.excel.core.PoiTable1;
import com.xyz.caofancpu.excel.util.PoiUtil;
import com.xyz.caofancpu.extra.NormalUseForTestUtil;
import com.xyz.caofancpu.utils.excel.domain.ExcellentRateAnalysisResp;
import com.xyz.caofancpu.utils.excel.domain.QuestionAnalysisResp;
import com.xyz.caofancpu.utils.excel.domain.RankAnalysisResp;
import org.apache.commons.lang3.BooleanUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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
        QuestionAnalysisResp analysisResp = new QuestionAnalysisResp();

        PoiBook poiBook = PoiBook.newSXSSFBook("小题分分析");
        //样式
        PoiStyle centerPoiStyle = PoiUtil.getCenterPoiStyle();
        PoiSheet poiSheet = poiBook.addSheet(new PoiSheet("Sheet0"));
        // 设置打印样式
        poiSheet.setPrintSetupScale((short) 93);
        poiSheet.addTable(new PoiTable1<QuestionAnalysisResp.QuestionAnalysisResult>(analysisResp.getAnalysisResultList()) {{
            addColumn1(QuestionAnalysisResp.QuestionAnalysisResult::getGroupName, "班级");
            if (BooleanUtils.isFalse(onlyGroup)) {
                addColumn1(QuestionAnalysisResp.QuestionAnalysisResult::getStudentName, "学生");
                addColumn1(QuestionAnalysisResp.QuestionAnalysisResult::getSchoolNo, "学校学号");
                addColumn1(QuestionAnalysisResp.QuestionAnalysisResult::getHtSchoolNo, "会课学号");
            }
            addColumns1(new PoiColumns<QuestionAnalysisResp.QuestionAnalysisResult, QuestionAnalysisResp.QuestionInfo>(QuestionAnalysisResp.QuestionAnalysisResult::getQuestionResultList) {{
                addColumn(QuestionAnalysisResp.QuestionInfo::getScore, QuestionAnalysisResp.QuestionInfo::getQuestionName).setDefaultValue("--");
            }});
        }}).setTitleStyle(centerPoiStyle).setStyle(centerPoiStyle);
    }

    @Test
    public void testExample2() {
        boolean onlyGroup = false;
        RankAnalysisResp analysisResp = new RankAnalysisResp();

        PoiBook poiBook = PoiBook.newSXSSFBook("小题分分析");
        //样式
        PoiStyle centerPoiStyle = PoiUtil.getCenterPoiStyle();
        PoiSheet poiSheet = poiBook.addSheet(new PoiSheet("Sheet0"));
        // 设置打印样式
        poiSheet.setPrintSetupScale((short) 93);
        poiSheet.addTable(new PoiTable1<RankAnalysisResp.RankResult>(analysisResp.getAnalysisResultList()) {{
            addColumn1(RankAnalysisResp.RankResult::getGroupName, "班级");
            if (BooleanUtils.isFalse(onlyGroup)) {
                addColumn1(RankAnalysisResp.RankResult::getStudentName, "姓名");
                addColumn1(RankAnalysisResp.RankResult::getSchoolNo, "学校学号");
                addColumn1(RankAnalysisResp.RankResult::getHtSchoolNo, "会课学号");
            }
            addColumns1(new PoiColumns<RankAnalysisResp.RankResult, RankAnalysisResp.SubjectResult>(RankAnalysisResp.RankResult::getSubjectResultList, RankAnalysisResp.SubjectResult::getSubjectName) {{
                if (BooleanUtils.isFalse(onlyGroup)) {
                    addColumn(RankAnalysisResp.SubjectResult::getScore, "成绩").setDefaultValue("--");
                    addColumn(RankAnalysisResp.SubjectResult::getGroupRank, "班级排名").setDefaultValue("--");
                    addColumn(RankAnalysisResp.SubjectResult::getGradeRank, "年级排名").setDefaultValue("--");
                } else {
                    addColumn(RankAnalysisResp.SubjectResult::getScore, "均分").setDefaultValue("--");
                    addColumn(RankAnalysisResp.SubjectResult::getGradeRank, "排名").setDefaultValue("--");
                }
            }});
        }}).setTitleStyle(centerPoiStyle).setStyle(centerPoiStyle);
    }

    @Test
    public void testExample3() {
        ExcellentRateAnalysisResp analysisResp = new ExcellentRateAnalysisResp();
        PoiBook poiBook = PoiBook.newSXSSFBook("三率一分分析");
        //样式
        PoiStyle centerPoiStyle = PoiUtil.getCenterPoiStyle();
        PoiSheet poiSheet = poiBook.addSheet(new PoiSheet("Sheet0"));
        // 设置打印样式
        poiSheet.setPrintSetupScale((short) 93);
        poiSheet.addTable(new PoiTable1<ExcellentRateAnalysisResp.ExcellentRateResult>(analysisResp.getAnalysisResultList()) {{
            addColumn1(ExcellentRateAnalysisResp.ExcellentRateResult::getViewName, "科目");
            addColumn1(ExcellentRateAnalysisResp.ExcellentRateResult::getTotalStudent, "应考人数");
            addColumn1(ExcellentRateAnalysisResp.ExcellentRateResult::getActualStudent, "实考人数");
            addColumn1(ExcellentRateAnalysisResp.ExcellentRateResult::getMaxScore, "最高分");
            addColumn1(ExcellentRateAnalysisResp.ExcellentRateResult::getMinScore, "最低分");
            addColumn1(ExcellentRateAnalysisResp.ExcellentRateResult::getAvgScore, "平均分");
            addColumn1(ExcellentRateAnalysisResp.ExcellentRateResult::getSigma, "标准差");
            addColumn1(ExcellentRateAnalysisResp.ExcellentRateResult::getExcellentRate, "比率", "优秀");
            addColumn1(ExcellentRateAnalysisResp.ExcellentRateResult::getGoodRate, "比率", "良好");
            addColumn1(ExcellentRateAnalysisResp.ExcellentRateResult::getPassRate, "比率", "及格");
            addColumn1(ExcellentRateAnalysisResp.ExcellentRateResult::getFailedRate, "比率", "不及格");
            addColumn1(ExcellentRateAnalysisResp.ExcellentRateResult::getDifficulty, "难度");
            addColumn1(ExcellentRateAnalysisResp.ExcellentRateResult::getDistinction, "区分度");
            addColumn1(ExcellentRateAnalysisResp.ExcellentRateResult::getTeacherName, "教师");
        }}).setTitleStyle(centerPoiStyle).setStyle(centerPoiStyle);
    }

}