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
import com.xyz.caofancpu.core.VerbalExpressionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
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
     * 获取本机真实ip
     */
    public static String getIpAddress() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(attributes)) {
            return LOCALHOST;
        }
        HttpServletRequest request = attributes.getRequest();
        String ipAddress = request.getHeader("x-forwarded-for");
        if (Objects.isNull(ipAddress) || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (Objects.isNull(ipAddress) || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (Objects.isNull(ipAddress) || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (LOCALHOST.equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
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
    public static String getPublicIp() {
        RestTemplate restTemplate = new RestTemplate();
        String wwwUrl = "http://www.net.cn/static/customercare/yourip.asp";

        String htmlText = restTemplate.postForObject(wwwUrl, null, String.class);
        if (Objects.isNull(htmlText)) {
            htmlText = SymbolConstantUtil.EMPTY;
        }
        String[] sourceLines = htmlText.split(SymbolConstantUtil.NEXT_LINE);
        List<String> matchIpItemList = new ArrayList<>(4);
        for (int i = 0; i < sourceLines.length; i++) {
            if (!WWW_PUBLIC_IP_REGEX.matcher(sourceLines[i]).find()) {
                continue;
            }
            Matcher matcher = VerbalExpressionUtil.IP_PATTERN.matcher(sourceLines[i]);
            while (matcher.find()) {
                matchIpItemList.add(matcher.group());
            }
            return matchIpItemList.get(0);
        }
        log.warn("未能找到本机公网IP, 使用LocalHost代替");
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