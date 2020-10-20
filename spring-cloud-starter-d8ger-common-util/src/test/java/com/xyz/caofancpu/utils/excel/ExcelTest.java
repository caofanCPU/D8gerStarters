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

import com.xyz.caofancpu.core.FileUtil;
import com.xyz.caofancpu.core.JSONUtil;
import com.xyz.caofancpu.excel.core.PoiBook;
import com.xyz.caofancpu.excel.core.PoiColumns;
import com.xyz.caofancpu.excel.core.PoiSheet;
import com.xyz.caofancpu.excel.core.PoiStyle;
import com.xyz.caofancpu.excel.core.PoiTable3;
import com.xyz.caofancpu.excel.util.PoiUtil;
import com.xyz.caofancpu.extra.NormalUseForTestUtil;
import com.xyz.caofancpu.utils.excel.domain.LOLAnalysisResp;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;

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

    /**
     * 1.支持标题自动合并
     * 2.只需要DataMo, 无任何侵入, 根据简易的lambda表达式指定数据填充行为, 即可完成数据渲染
     * 3.样式支持
     * 4.指定区域单元格合并
     */
    @Test
    public void lambdaExcel()
            throws Exception {
        String baseDirFullPath = "/Users/D8GER/Desktop/CAOFAN/spring-cloud-d8ger-starters/spring-cloud-starter-d8ger-common-util/src/test/java/com/xyz/caofancpu/utils/excel/datasource";
        String dataSourceFullPath = baseDirFullPath + File.separator + "LOLTeamData.json";
        LOLAnalysisResp analysisResp = JSONUtil.deserializeJSON(FileUtil.readFileToString(dataSourceFullPath), LOLAnalysisResp.class);
        PoiBook poiBook = PoiBook.newSXSSFBook("LOL三层嵌套表格");
        // 常规居中样式, 用户表格全局样式
        PoiStyle center = PoiUtil.getCenterPoiStyle();
        // 测试: 黄底红字
        PoiStyle yellowBgRedFt = PoiUtil.getRedFrontAndYellowBgCenterPoiStyle();
        // 测试: 绿字
        PoiStyle greenFont = PoiUtil.getGreenCenterPoiStyle();
        PoiStyle redFont = PoiUtil.getRedCenterPoiStyle();
        // 测试: 数字绿涨红跌
        PoiStyle numberRefer = PoiUtil.getNumberArrowCenterPoiStyle();
        // 测试: 数字百分比红涨绿跌
        PoiStyle numberPercent = PoiUtil.getNumberPercentArrowCenterPoiStyle(2);
        PoiSheet poiSheet = poiBook.addSheet(new PoiSheet("帝八哥"));
        // 设置打印样式
        poiSheet.setPrintSetupScale((short) 93);
        poiSheet.addTable(new PoiTable3<LOLAnalysisResp.GameArea, LOLAnalysisResp.Team, LOLAnalysisResp.Player>(analysisResp.getGameAreaList(), LOLAnalysisResp.GameArea::getTeamList, LOLAnalysisResp.Team::getPlayerList) {{
            // 两列数据, 多级列标题形式
            // 取第一层数据, 1对应PoiTable3的第一个构造参数
            addColumn1(LOLAnalysisResp.GameArea::getId, "赛区信息", "编码")
                    .setDefaultValue("--")
                    // 该列标题为黄底红字
                    .setTitleStyle(yellowBgRedFt)
                    // 该列除标题行的数据列为绿色字体样式
                    .setStyle(greenFont);
            addColumn1(LOLAnalysisResp.GameArea::getName, "赛区信息", "名称").setDefaultValue("--");

            // 两列数据, 一般形式
            // 取第二层数据, 2对应PoiTable3的第二个构造参数
            addColumn2(LOLAnalysisResp.Team::getId, "战队信息", "ID").setDefaultValue("--");
            addColumn2(LOLAnalysisResp.Team::getName, "战队信息", "名称").setDefaultValue("--");
            addColumn2(LOLAnalysisResp.Team::getAvgScore, "战队信息", "平均KDA").setDefaultValue("--");
            // 取第三层数据, 3对应PoiTable3的第三个构造参数
            addColumn3(LOLAnalysisResp.Player::getId, "选手信息", "基础信息", "ID").setDefaultValue("--");
            addColumn3(LOLAnalysisResp.Player::getName, "选手信息", "基础信息", "姓名").setDefaultValue("--");
            addColumn3(LOLAnalysisResp.Player::getAge, "选手信息", "基础信息", "年龄").setDefaultValue("--").setTitleStyle(greenFont);
            addColumn3(LOLAnalysisResp.Player::getKillsPerGame, "选手信息", "关键数据", "场均击杀").setStyle(numberRefer).setDefaultValue("--");
            addColumn3(item -> item.getPercentScoreKDA() / 100.0F, "选手信息", "关键数据", "进退百分比").setStyle(numberPercent).setDefaultValue("--");
            // 取第三层数据, 并且添加多列, 前面1->2->3是在拓展excel的行, 此处相当于是拓展excel的列
            addColumns3(new PoiColumns<LOLAnalysisResp.Player, LOLAnalysisResp.ResultScore>(LOLAnalysisResp.Player::getLatest3List, item -> "最近第[" + item.getIndex() + "]场战况") {{
                addColumn(LOLAnalysisResp.ResultScore::getKda, "KDA").setDefaultValue("--");
                addColumn(item -> item.getHasWin() ? "✔" : "✘", "结果").setStyle(redFont).setDefaultValue("--");
            }});
        }}).setTitleStyle(center).setStyle(center);
        // 添加5个空行
        poiSheet.addWhiteRowSplit(2);
        //写一行数据, 该行区域指定 3个单元行*10个单元列
        poiSheet.addRow(3, 10, "LOL.S10冠军赛数据分析").setStyle(yellowBgRedFt);
        // 合并指定区域的单元格
        PoiUtil.mergedRegion(poiBook, poiSheet, 24, 2, 25, 6, "LOL.S10冠军", greenFont);
        String excelSaveFullPath = baseDirFullPath + File.separator + poiBook.getFileName();
        FileUtil.saveExcelFile(excelSaveFullPath, poiBook.buildWorkbook());
    }


}