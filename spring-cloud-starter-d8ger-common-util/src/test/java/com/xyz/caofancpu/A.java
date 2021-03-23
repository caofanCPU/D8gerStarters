/*
 * Copyright 2016-2021 the original author
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

import com.xyz.caofancpu.extra.QRCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 *
 */
@Slf4j
public class A extends JFrame {
    // 得到显示器屏幕的宽高
    public static int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    public static int height = Toolkit.getDefaultToolkit().getScreenSize().height;
    // 定义窗体的宽高
    public static int windowsWidth = 730;
    public static int windowsHeight = 450;

    public A(BufferedImage image, String url, String title) {
        super.setTitle(title);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        label.setIcon(new ImageIcon(image));
        panel.add(label);
        JTextArea originHttp = new JTextArea(url, 3, 40);
        originHttp.setForeground(Color.BLACK);
        originHttp.setLineWrap(true);
        originHttp.setFont(new Font(Font.DIALOG, Font.BOLD, 18));
        originHttp.setEditable(false);
        panel.add(originHttp);
        super.add(panel);
        super.setBounds((width - windowsWidth) / 2,
                (height - windowsHeight) / 2, windowsWidth, windowsHeight);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setVisible(true);
    }

    public static void main(String[] args) {
//        try {
//            String url = "https://mp.weixin.qq.com/s/YXH47C4P2Sc1OQblyZlZzg";
//            BufferedImage image = QRCodeUtil.createImage(url, null, false);
//            new A(image, url, "帝八哥-Kafka之旅");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        Pair<String, String> linkPair = Pair.of("https://mp.weixin.qq.com/s/YXH47C4P2Sc1OQblyZlZzg", "Kafka");
        DialogPanel dialogPanel = new DialogPanel(linkPair.getLeft(), linkPair.getRight());
        JFrame x = new JFrame();
        x.add(dialogPanel);
        x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        x.setSize(400, 400);
        x.setResizable(false);
        x.setLocationRelativeTo(null);
        x.setVisible(true);
    }

    public static class DialogPanel extends JPanel {
        public DialogPanel(String url, String title) {
            BufferedImage image;
            try {
                image = QRCodeUtil.createImage(url, null, true);
                JLabel label = new JLabel();
                // config image CENTER
                label.setIcon(new ImageIcon(image));
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setVerticalAlignment(JLabel.CENTER);
                label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
                label.setAlignmentY(JLabel.CENTER_ALIGNMENT);
                // config image area size
                label.setBounds((width - windowsWidth) / 2, (height - windowsHeight) / 2, windowsWidth, windowsHeight);
                // config image text CENTER & BOTTOM
                label.setText("<html><u>" + title + "</u></html>");
                label.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
                label.setForeground(Color.BLUE);
                label.setVerticalTextPosition(JLabel.BOTTOM);
                label.setHorizontalTextPosition(JLabel.CENTER);
                // config mouse click action
                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        System.out.println("鼠标单击啦");
                    }
                });
                add(label);
                setBorder(new EmptyBorder(30, 50, 30, 50));
            } catch (Exception e) {
                log.error("Init url QR code failed, try again!");
            }
        }
    }
}
