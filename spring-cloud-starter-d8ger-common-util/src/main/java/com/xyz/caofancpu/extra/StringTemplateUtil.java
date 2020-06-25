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

package com.xyz.caofancpu.extra;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLParseException;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.fastjson.JSONObject;
import com.xyz.caofancpu.constant.SymbolConstantUtil;
import com.xyz.caofancpu.core.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 字符串模板替换工具类
 *
 * @author D8GER
 */
@Slf4j
public class StringTemplateUtil {
    public static final String TEMPLATE_KEY_PREFIX = "\\$\\{";
    public static final String TEMPLATE_KEY_SUFFIX = "\\}";

    public static String processTemplate(String templateContent, Object source) {
        if (Objects.isNull(templateContent)
                || Objects.isNull(source)) {
            return templateContent;
        }
        Map<String, Object> paramMap = JSONObject.parseObject(JSONObject.toJSONString(source));
        return processTemplate(templateContent, paramMap);
    }

    public static String processTemplate(String templateContent, Map<String, Object> paramMap) {
        if (Objects.isNull(templateContent) || CollectionUtil.isEmpty(paramMap)) {
            return templateContent;
        }
        Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
        for (Map.Entry<String, Object> entry : entrySet) {
            String regex = TEMPLATE_KEY_PREFIX + entry.getKey() + TEMPLATE_KEY_SUFFIX;
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(templateContent);
            templateContent = matcher.replaceAll(NormalUseForTestUtil.convertToString(entry.getValue()));
        }
        return templateContent;
    }

    public static String formatMySQL(String sql) {
        if (StringUtils.isBlank(sql)) {
            return sql;
        }
        try {
            String dbType = JdbcUtils.MYSQL;
            List<SQLStatement> statementList = SQLUtils.toStatementList(sql, dbType);

            StringBuilder out = new StringBuilder();
            SQLASTOutputVisitor visitor = SQLUtils.createFormatOutputVisitor(out, statementList, dbType);
            for (SQLStatement stmt : statementList) {
                stmt.accept(visitor);
            }

            return out.toString().replaceAll(SymbolConstantUtil.ORIGIN_TAB, SymbolConstantUtil.TAB) + SymbolConstantUtil.ENGLISH_SEMICOLON;
        } catch (SQLParseException | ParserException ex) {
            return sql;
        }
    }

}
