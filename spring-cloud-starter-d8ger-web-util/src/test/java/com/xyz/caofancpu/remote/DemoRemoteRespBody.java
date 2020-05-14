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

package com.xyz.caofancpu.remote;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author D8GER
 */
public class DemoRemoteRespBody {
    /**
     * 视频转码信息
     */
    private final List<TranscodeInfo> transcodeInfoList = Lists.newArrayList();
    /**
     * 视频ID
     */
    private Long videoId;
    /**
     * 视频名称
     */
    private String name;
    /**
     * 转码状态
     */
    private Integer status;
    /**
     * 视频大小(转码完才会有大小, 单位: 字节)
     */
    private Long totalSize;
    /**
     * 总视频大小(源文件+所有转码后文件, 单位: 字节)
     */
    private Long totalTranscodeSize;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 视频封面缩略图
     */
    private String prefaceUrl;
    /**
     * 视频时长, 单位: 秒
     */
    private String videoDuration;
    /**
     * 视频审核状态 0:未审核 1:审核通过 2:审核拒绝
     * 注意: 只要不是审核拒绝的都可以正常播放
     */
    private Integer auditStatus;
    /**
     * 原始视频的md5值
     */
    private String fileMd5;
    /**
     * 原始视频清晰度
     * low/std/high/super/1080p
     */
    private String originDefinition;

    @Data
    @Accessors(chain = true)
    public static class TranscodeInfo implements Serializable {
        /**
         * 转码的文件清晰度(low/std/high/super/1080p)
         */
        private String definition;

        /**
         * 转码的文件格式(mp4/flv/m3u8)
         */
        private String type;

        /**
         * 转码的文件大小(转码完才会有大小, 单位: 字节)
         */
        private Long size;
    }

}
