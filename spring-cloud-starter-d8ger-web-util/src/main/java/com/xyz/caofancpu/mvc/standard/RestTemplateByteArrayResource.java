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

package com.xyz.caofancpu.mvc.standard;

import org.springframework.core.io.ByteArrayResource;

import javax.validation.constraints.NotBlank;

/**
 * 针对RestTemplate方式, 在应用服务内封装文件流, 上传至其他应用
 * 参考文档: https://www.cnblogs.com/paxing/p/11485049.html
 *
 * @author D8GER
 */
public class RestTemplateByteArrayResource extends ByteArrayResource {
    private final String originFileName;
    private int length;

    public RestTemplateByteArrayResource(byte[] byteArray, @NotBlank String originFileName) {
        super(byteArray);
        this.originFileName = originFileName;
    }

    public RestTemplateByteArrayResource(byte[] byteArray, int length, @NotBlank String originFileName) {
        super(byteArray);
        this.length = length;
        this.originFileName = originFileName;
    }

    /**
     * 覆写父类方法
     * 如果不重写这个方法，并且文件有一定大小，那么服务端会出现异常
     * {@code The multi-part request contained parameter data (excluding uploaded files) that exceeded}
     *
     * @return
     */
    @Override
    public String getFilename() {
        return originFileName;
    }

    /**
     * 覆写父类 contentLength 方法
     * 因为 {@link org.springframework.core.io.AbstractResource#contentLength()}方法会重新读取一遍文件，
     * 而上传文件时，restTemplate 会通过这个方法获取大小。然后当真正需要读取内容的时候，发现已经读完，会报如下错误。
     * <code>
     * java.lang.IllegalStateException: InputStream has already been read - do not use InputStreamResource if a stream needs to be read multiple times
     * at org.springframework.core.io.InputStreamResource.getInputStream(InputStreamResource.java:96)
     * </code>
     * <p>
     * ref:com.amazonaws.services.s3.model.S3ObjectInputStream#available()
     *
     * @return
     */
    @Override
    public long contentLength() {
        int estimate = length;
        return estimate == 0 ? 1 : estimate;
    }

}