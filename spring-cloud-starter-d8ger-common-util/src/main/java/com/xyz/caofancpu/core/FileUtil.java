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

package com.xyz.caofancpu.core;

import com.alibaba.fastjson.JSONObject;
import com.xyz.caofancpu.constant.SymbolConstantUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

/**
 * 文件处理工具类
 *
 * @author D8GER
 */
@Slf4j
public class FileUtil {

    /**
     * 生成唯一序列标识
     *
     * @return
     */
    public static String getUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 将文件转成base64 字符串
     *
     * @param path 文件路径
     * @return
     * @throws Exception
     */
    public static String encodeBase64(String path)
            throws IOException {
        byte[] buffer = FileUtils.readFileToByteArray(new File(path));
        return Base64.encodeBase64String(buffer);
    }

    public static String svgContext(String fileFullPath)
            throws IOException {
        String content = FileUtil.encodeBase64(fileFullPath);
        String[] tmp = fileFullPath.split(File.separator);
        String[] fileNameTmp = tmp[tmp.length - 1].split("\\.");
        String name = fileNameTmp[0];
        String suffix = fileNameTmp[1];
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(SymbolConstantUtil.NEXT_LINE)
                .append("<svg xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"36px\" height=\"36px\" viewBox=\"0 0 36 36\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\">").append(SymbolConstantUtil.NEXT_LINE)
                .append(SymbolConstantUtil.TAB).append("<!-- Generator: Sketch 57.1 (83088) - https://sketch.com -->").append(SymbolConstantUtil.NEXT_LINE)
                .append(SymbolConstantUtil.TAB).append("<title>").append(name).append("</title>").append(SymbolConstantUtil.NEXT_LINE)
                .append(SymbolConstantUtil.TAB).append("<desc>Created By Tina.</desc>").append(SymbolConstantUtil.NEXT_LINE)
                .append(SymbolConstantUtil.TAB).append("<g id=\"Tina-1\" stroke=\"none\" stroke-width=\"1\" fill=\"none\" fill-rule=\"evenodd\">").append(SymbolConstantUtil.NEXT_LINE)
                .append(SymbolConstantUtil.DOUBLE_TAB).append("<image id=\"").append(name).append("\" x=\"0\" y=\"0\" width=\"36\" height=\"36\" xlink:href=\"data:image/").append(suffix).append(";base64,").append(content).append("\"></image>").append(SymbolConstantUtil.NEXT_LINE)
                .append(SymbolConstantUtil.TAB).append("</g>").append(SymbolConstantUtil.NEXT_LINE)
                .append("</svg>").append(SymbolConstantUtil.NEXT_LINE);
        return sb.toString();
    }

    /**
     * 保存字符串到指定文件
     *
     * @param content
     * @param fileFullPath
     * @throws FileNotFoundException
     */
    public static void writeStringToFile(String content, String fileFullPath)
            throws IOException {
        File file = new File(fileFullPath);
        FileUtils.writeStringToFile(file, content);
    }

    /**
     * 复制文件
     *
     * @param sourceFileFullPath
     * @param destFileFullPath
     * @throws IOException
     */
    public static void copyFile(String sourceFileFullPath, String destFileFullPath)
            throws IOException {
        File sourceFile = new File(sourceFileFullPath);
        File destFile = new File(destFileFullPath);
        FileUtils.copyFile(sourceFile, destFile);
    }

    /**
     * 本地保存excel文件
     *
     * @param targetPath
     * @param wb
     * @throws IOException
     */
    public static String saveExcelFile(String targetPath, Workbook wb)
            throws IOException {
        File targetFile = new File(targetPath);
        try (OutputStream out = new FileOutputStream(targetFile)) {
            wb.write(out);
        } finally {
            wb.close();
        }
        return targetPath;
    }

    public static String readFileToString(String fileFullPath)
            throws IOException {
        File file = new File(fileFullPath);
        return FileUtils.readFileToString(file);
    }

    /**
     * 将base64字符解码保存文件
     *
     * @param base64Code
     * @param fileFullPath
     * @throws Exception
     */

    public static void decodeBase64WithSave(String base64Code, String fileFullPath)
            throws IOException {
        byte[] buffer = Base64.decodeBase64(base64Code);
        FileUtils.writeByteArrayToFile(new File(fileFullPath), buffer);
    }

    /**
     * 文件反序列化到对象
     *
     * @param fileFullPath
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     * @deprecated 应从Redis中获取对象, 反序列化
     */
    @Deprecated
    public static <T> T deserializeFromFile(String fileFullPath, Class<T> clazz)
            throws IOException {
        FileInputStream fis = new FileInputStream(fileFullPath);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object sourceObj = null;
        try {
            sourceObj = ois.readObject();
        } catch (IOException e) {
            // ignore
        } catch (ClassNotFoundException e) {
            log.error("反序列化文件出错, 原因: ", e);
            throw new IOException("反序列化的目的类不存在!");
        } finally {
            IOUtils.closeQuietly(ois);
            IOUtils.closeQuietly(fis);
        }
        return JSONObject.parseObject(JSONObject.toJSONString(sourceObj), clazz);
    }

    /**
     * 对象序列化到文件
     *
     * @param obj
     * @param fileFullPath
     * @throws IOException
     * @deprecated 应使用fast-json序列化, 保存在Redis中
     */
    @Deprecated
    public static void serializeToFile(Object obj, String fileFullPath)
            throws IOException {
        FileOutputStream fos = new FileOutputStream(fileFullPath);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        try {
            oos.writeObject(obj);
        } catch (IOException e) {
            // ignore
        } finally {
            IOUtils.closeQuietly(oos);
            IOUtils.closeQuietly(fos);
        }
    }

}
