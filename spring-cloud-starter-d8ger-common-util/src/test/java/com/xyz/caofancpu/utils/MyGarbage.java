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

package com.xyz.caofancpu.utils;

import com.alibaba.druid.sql.SQLUtils;

/**
 * FileName: MyGarbage
 */

@Deprecated
public class MyGarbage {

    public static void main(String[] args) {
        String just = SQLUtils.formatMySql(" select exam_no, task_id, subject_organ, school_id, group_id, subject_total_score, sum_total_score, sum_judge_score, max_score, min_score, avg_score, mid_score, student_score, absent_student_score, push_student_num, report_subject_type, exam_name, task_name, group_name, group_sort, report_group_type, major\n" +
                " from DWH_52.dws_group_subject\n" +
                " where exam_no = 'O15923032799452020060001010' and school_id = 10236754;");
        just = just.replaceAll("\t", "    ");
        just += ";";
        out(just);
    }

    public static void out(String text) {
        System.out.println(text);
    }

}
