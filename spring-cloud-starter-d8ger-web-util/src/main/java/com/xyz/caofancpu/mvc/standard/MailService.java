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

import com.xyz.caofancpu.annotation.AttentionDoc;
import com.xyz.caofancpu.annotation.WarnDoc;
import com.xyz.caofancpu.core.CollectionUtil;
import com.xyz.caofancpu.logger.LoggerUtil;
import com.xyz.caofancpu.property.MailProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Collection;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

/**
 * Mail服务
 *
 * @author D8GER
 */
@Slf4j
public class MailService {

    public static final String SEND_HOST_KEY = "mail.smtp.host";

    public static final String SSL_AUTH_KEY = "mail.smtp.auth";
    /**
     * 邮箱服务配置
     */
    private final MailProperties mailProperties;

    /**
     * 构造函数, 初始化邮箱服务配置
     *
     * @param mailProperties 邮件基础配置
     */
    public MailService(MailProperties mailProperties) {
        this.mailProperties = mailProperties;
    }

    /**
     * 发送邮件
     *
     * @param multiToEmailAddress 接收者邮箱地址
     * @param title               邮件主题
     * @param content             邮件正文
     */
    @AttentionDoc("收件人邮箱地址、主题、正文均不允许为空")
    public void sendMail(Set<String> multiToEmailAddress, String title, String content) {
        if (CollectionUtil.isEmpty(multiToEmailAddress) || StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
            return;
        }
        Session session = getSession();
        try {
            MimeMessage message = buildMessage(multiToEmailAddress, title, content, session);
            // 发送消息
            Transport.send(message);
        } catch (MessagingException e) {
            log.error("邮件发送失败, 接收人[{}], 主题[{}], 正文[{}]", CollectionUtil.show(multiToEmailAddress), title, LoggerUtil.shortenLogContent(content));
            e.printStackTrace();
        }
    }

    /**
     * 构建邮件消息
     *
     * @param recipientEmails 接收者邮箱地址
     * @param title           邮件主题
     * @param content         邮件正文
     * @param session         服务session
     * @return
     * @throws MessagingException
     */
    private MimeMessage buildMessage(Set<String> recipientEmails, String title, String content, Session session)
            throws MessagingException {
        // 1.创建MimeMessage
        MimeMessage message = new MimeMessage(session);
        // 2.设置From: 头部头字段
        message.setFrom(new InternetAddress(mailProperties.getFromEmailAddress()));
        // 3.设置To: 头部头字段
        message.addRecipients(Message.RecipientType.TO, buildRecipients(recipientEmails));
        if (recipientEmails.size() != message.getAllRecipients().length) {
            log.warn("应发送收件人数量为[{}], 实际收件人数量为[{}]", recipientEmails.size(), message.getAllRecipients().length);
        }
        // 4.设置Subject: 头部头字段
        message.setSubject(title);
        // 5.设置消息体
        message.setText(content);
        return message;
    }

    /**
     * 构建收件人
     *
     * @param recipientEmails
     * @return
     */
    @WarnDoc("出现异常的收件人将被忽略")
    private Address[] buildRecipients(Collection<String> recipientEmails) {
        Set<InternetAddress> recipients = CollectionUtil.transToSet(recipientEmails, itemTo -> {
            try {
                return new InternetAddress(itemTo);
            } catch (Exception e) {
                log.error("收件人邮箱地址[{}]不合法, 将被忽略", itemTo);
                return null;
            }
        });
        return CollectionUtil.filterAndTransArray(recipients, Objects::nonNull, Function.identity(), Address[]::new);
    }

    /**
     * 获取Session
     *
     * @return
     */
    private Session getSession() {
        Properties properties = new Properties();
        // 1.设置邮件服务器
        properties.put(SEND_HOST_KEY, mailProperties.getMailSendHost());
        // 2.开启ssl加密
        properties.put(SSL_AUTH_KEY, mailProperties.getEnableSSL());
        // 3.获取默认session对象
        return Session.getDefaultInstance(properties, new Authenticator() {
            // 3.1发件人邮件用户名、密码
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailProperties.getFromEmailAddress(), mailProperties.getAuthPwd());
            }
        });
    }
}
