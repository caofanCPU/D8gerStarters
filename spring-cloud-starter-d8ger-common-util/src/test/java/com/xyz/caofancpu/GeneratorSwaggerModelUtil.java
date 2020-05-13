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

package com.xyz.caofancpu;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自动生成SwaggerModel对象工具类
 *
 * @author D8GER
 */
public class GeneratorSwaggerModelUtil {

    @SuppressWarnings("unchecked")
    public static void wrapExistSwaggerModelFieldPositionOrder(Class<?> aClass, String... moduleNames)
            throws Exception {
        String rootPath = System.getProperty("user.dir");
        String modulePrefix = "";
        if (moduleNames != null && moduleNames.length > 0) {
            modulePrefix = moduleNames[0];
        }
        String defaultCodeDirPrefix = "/src/main/java";
        String moduleFilePrefix = rootPath + File.separator + modulePrefix + defaultCodeDirPrefix;
        File sourceCode = new File(moduleFilePrefix + File.separator + aClass.getName().replaceAll("\\.", "\\/") + ".java");
        List<String> codeLineList = FileUtils.readLines(sourceCode);
        List<String> wrapLineList = new ArrayList<>(codeLineList.size());
        AtomicInteger counter = new AtomicInteger(1);
        System.out.println("\n\n");
        for (String item : codeLineList) {
            if (item.contains("@ApiModelProperty(")) {
                if (!item.contains("position")) {
                    item = item.replace(")", ", position = " + counter.getAndIncrement() + ")");
                } else {
                    item = regexHandlePositionProperty(item, counter.getAndIncrement());
                }
            }
            wrapLineList.add(item);
            System.out.println(item);
        }
        System.out.println("\n\n");
        FileUtils.writeLines(sourceCode, null, wrapLineList, StringUtils.LF);
    }

    private static String regexHandlePositionProperty(String originString, int positionOrder) {
        Pattern pattern = Pattern.compile("((?:position)(?:\\s)*(?:\\=)(?:\\s)*(?:\\d)*)");
        Matcher matcher = pattern.matcher(originString);
        return matcher.replaceAll("position = " + positionOrder);
    }

    public static void generateByClass(Class<?> aClass)
            throws Exception {
        Field[] declaredFields = aClass.getDeclaredFields();
        if (declaredFields.length == 0) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(wrapModelClassHeader(aClass));
        for (int i = 0; i < declaredFields.length; i++) {
            Field declaredField = declaredFields[i];
            sb.append(wrapSwaggerApiModelProperty(i + 1));
            sb.append(wrapField(declaredField));
            sb.append("\n");
        }
        sb.append(wrapModelClassFooter());
        System.out.println(aClass.getCanonicalName() + "\n\n\n" + sb.toString());
    }

    private static String wrapField(Field field) {
        String typeName = field.getType().getSimpleName();
        if (List.class.isAssignableFrom(field.getType())) {
            String listTypeName = field.getGenericType().getTypeName();
            String[] splitArray = listTypeName.replaceAll(">", "").split("\\.");
            typeName = "List<" + splitArray[splitArray.length - 1] + ">";
        }
        return "\tprivate " + typeName + " " + field.getName() + ";\n";
    }

    private static String wrapSwaggerApiModelProperty(int fieldPositionOrder) {
        return "\t@ApiModelProperty(value = \"\", example = \"\", position = " + fieldPositionOrder + ")\n";
    }

    private static String wrapModelClassHeader(Class<?> aClass) {
        return "/**\n" +
                " * XXXXXXXX\n" +
                " *\n" +
                " * @author XXXXX\n" +
                " */\n" +
                "@Data\n" +
                "@NoArgsConstructor\n" +
                "@AllArgsConstructor\n" +
                "@Accessors(chain = true)\n" +
                "@ApiModel\n" +
                "public class " + aClass.getSimpleName() +
                " implements Serializable {\n";
    }

    private static String wrapModelClassFooter() {
        return "}";
    }

    public static void main(String[] args)
            throws Exception {
//        generateByClass(XXX.class);
//        wrapExistSwaggerModelFieldPositionOrder(XXXX.class, "XXX-moudle");
    }

}
