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

package com.xyz.caofancpu.logger;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.xyz.caofancpu.constant.SymbolConstantUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * IP工具
 *
 * @author D8GER
 */
@Slf4j
public class LogIpConfigUtil extends ClassicConverter {

    private static final String LOCALHOST = "127.0.0.1";

    private static final Pattern WWW_PUBLIC_IP_REGEX = Pattern.compile("您的本地上网IP是");

    /**
     * 获取请求来源IP, 当来源为自己时获取本机真实ip
     */
    public static String getRequestSourceIp() {
        String ipAddress = null;
        HttpServletRequest request;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(attributes)) {
            request = attributes.getRequest();
            ipAddress = request.getHeader("x-forwarded-for");
            if (Objects.isNull(ipAddress) || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (Objects.isNull(ipAddress) || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (Objects.isNull(ipAddress) || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
            }
        }
        if (Objects.isNull(ipAddress)
                || ipAddress.length() == 0
                || "unknown".equalsIgnoreCase(ipAddress)
                || LOCALHOST.equals(ipAddress)
                || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
            // 根据网卡取本机配置的IP
            InetAddress inet = null;
            try {
                inet = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                log.error("获取IP异常: " + e);
            }
            if (Objects.nonNull(inet)) {
                ipAddress = inet.getHostAddress();
            }
        }
        if (Objects.isNull(ipAddress)) {
            ipAddress = LOCALHOST;
            log.warn("获取(本机)IP地址失败, 使用LocalHost代替");
        }
        // 多个代理时, 第一个IP为客户端真实IP
        String[] ipAddressArr = ipAddress.split(SymbolConstantUtil.ENGLISH_COMMA);
        ipAddress = ipAddressArr[0];
        return ipAddress;
    }

    /**
     * 获取本机公网IP
     *
     * @return
     */
    public static String getSelfPublicIp() {
        log.warn("请自行获取本机公网IP, 默认使用LocalHost代替");
        return LOCALHOST;
    }

    @Override
    public String convert(ILoggingEvent event) {
        return isWindowsOS() ? getWindowsIp() : getLinuxLocalIp();
    }

    /**
     * 获取Linux下的IP地址
     *
     * @return IP地址
     */
    private String getLinuxLocalIp() {
        String ip = LOCALHOST;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface net = en.nextElement();
                String name = net.getName();
                if (!name.contains("docker") && !name.contains("lo")) {
                    for (Enumeration<InetAddress> enumIpAddr = net.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            String ipAddress = inetAddress.getHostAddress();
                            if (!ipAddress.contains("::") && !ipAddress.contains("0:0:") && !ipAddress.contains("fe80")) {
                                ip = ipAddress;
                            }
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            log.error("获取日志Ip异常", ex);
        }
        return ip;
    }

    /**
     * 判断操作系统是否是Windows
     *
     * @return
     */
    private boolean isWindowsOS() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    private String getWindowsIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("获取日志Ip异常", e);
        }
        return LOCALHOST;
    }

}