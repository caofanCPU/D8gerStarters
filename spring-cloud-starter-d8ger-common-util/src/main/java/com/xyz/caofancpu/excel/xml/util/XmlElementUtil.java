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

package com.xyz.caofancpu.excel.xml.util;

import com.xyz.caofancpu.excel.util.PoiAssert;
import com.xyz.caofancpu.excel.xml.config.PoiStyleConfig;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.dom4j.Attribute;
import org.dom4j.Element;

import java.io.InputStream;

/**
 * xml读取工具
 */
public class XmlElementUtil {

    public static String getAttribute(Element element, String name, String defaultValue) {
        String attribute = getAttribute(element, name);
        if (attribute != null) {
            return attribute;
        }
        return defaultValue;
    }

    public static String getAttributeRequired(Element element, String name) {
        String attribute = getAttribute(element, name);
        PoiAssert.isEmpty(attribute, "参数[" + name + "]不能为空");
        return attribute;
    }

    public static String getAttribute(Element element, String name) {
        Attribute attribute = element.attribute(name);
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    public static Integer getIntAttribute(Element element, String name, Integer defaultValue) {
        Integer attribute = getIntAttribute(element, name);
        if (attribute != null) {
            return attribute;
        }
        return defaultValue;
    }

    public static Integer getIntAttributeRequired(Element element, String name) {
        Integer attribute = getIntAttribute(element, name);
        PoiAssert.isEmpty(attribute, "参数[" + name + "]不能为空");
        return attribute;
    }

    public static Integer getIntAttribute(Element element, String name) {
        Attribute attribute = element.attribute(name);
        if (attribute == null) {
            return null;
        }
        return Integer.valueOf(attribute.getValue());
    }

    public static Boolean getBooleanAttribute(Element element, String name, Boolean defaultValue) {
        String attribute = getAttribute(element, name);
        if (attribute != null) {
            return Boolean.valueOf(attribute);
        }
        return defaultValue;
    }

    public static Integer getColumnWidth(Element element, String name) {
        Attribute attribute = element.attribute(name);
        if (attribute == null) {
            return null;
        }
        Float floatAttribute = Float.valueOf(attribute.getValue());
        return floatAttribute > 256 ? floatAttribute.intValue() : (int) (floatAttribute * 256 + 182);
    }


    public static InputStream getResourceAsStream(String resource) {
        return getResourceAsStream(resource, new ClassLoader[]{Thread.currentThread().getContextClassLoader(), XmlElementUtil.class.getClassLoader(), ClassLoader.getSystemClassLoader()});
    }


    public static InputStream getResourceAsStream(String resource, ClassLoader[] classLoader) {
        for (ClassLoader cl : classLoader) {
            if (null != cl) {
                InputStream returnValue = cl.getResourceAsStream(resource);
                if (null == returnValue) {
                    returnValue = cl.getResourceAsStream("/" + resource);
                }

                if (null != returnValue) {
                    return returnValue;
                }
            }
        }
        return null;
    }

    public static PoiStyleConfig getStyle(Element element, PoiStyleConfig parent) {
        return getStyle(element, "", parent);
    }

    public static PoiStyleConfig getTitleStyle(Element element, PoiStyleConfig parent) {
        return getStyle(element, "title_", parent);
    }

    private static PoiStyleConfig getStyle(Element element, String prev, PoiStyleConfig parent) {
        PoiStyleConfig style = new PoiStyleConfig();

        String dDontNameAttribute = XmlElementUtil.getAttribute(element, prev + "dFontName");
        if (dDontNameAttribute != null) {
            style.setDFontName(dDontNameAttribute);
        }
        String dFontSizeAttribute = XmlElementUtil.getAttribute(element, prev + "dFontSize");
        if (dFontSizeAttribute != null) {
            style.setDFontSize(dFontSizeAttribute);
        }
        String dFontBoldAttribute = XmlElementUtil.getAttribute(element, prev + "dFontBold");
        if (dFontBoldAttribute != null) {
            style.setDFontBold(dFontBoldAttribute);
        }
        String dFontColorAttribute = XmlElementUtil.getAttribute(element, prev + "dFontColor");
        if (dFontColorAttribute != null) {
            style.setDFontColor(dFontColorAttribute);
        }
        String dBgColorAttribute = XmlElementUtil.getAttribute(element, prev + "dBgColor");
        if (dBgColorAttribute != null) {
            style.setDBgColor(dBgColorAttribute);
        }


        String fontNameAttribute = XmlElementUtil.getAttribute(element, prev + "fontName");
        if (fontNameAttribute != null) {
            style.setFontName(fontNameAttribute);
        } else {
            style.setFontName(parent.getFontName());
        }
        String fontSizeAttribute = XmlElementUtil.getAttribute(element, prev + "fontSize");
        if (fontSizeAttribute != null) {
            style.setFontSize(Short.valueOf(fontSizeAttribute));
        } else {
            style.setFontSize(parent.getFontSize());
        }
        String fontBoldAttribute = XmlElementUtil.getAttribute(element, prev + "fontBold");
        if (fontBoldAttribute != null) {
            style.setFontBold(Boolean.valueOf(fontBoldAttribute));
        } else {
            style.setFontBold(parent.getFontBold());
        }

        String fountColorAttribute = XmlElementUtil.getAttribute(element, prev + "fontColor");
        if (fountColorAttribute != null) {
            style.setFontColor(IndexedColors.valueOf(fountColorAttribute.toUpperCase()));
        } else {
            style.setFontColor(parent.getFontColor());
        }
        String bgColorAttribute = XmlElementUtil.getAttribute(element, prev + "bgColor");
        if (bgColorAttribute != null) {
            style.setBgColor(IndexedColors.valueOf(bgColorAttribute.toUpperCase()));
        } else {
            style.setBgColor(parent.getBgColor());
        }
        String wrapTextAttribute = XmlElementUtil.getAttribute(element, prev + "wrapText");
        if (wrapTextAttribute != null) {
            style.setWrapText(Boolean.valueOf(wrapTextAttribute));
        } else {
            style.setWrapText(parent.getWrapText());
        }
        String alignAttribute = XmlElementUtil.getAttribute(element, prev + "align");
        if (alignAttribute != null) {
            style.setAlign(HorizontalAlignment.valueOf(alignAttribute.toUpperCase()));
        } else {
            style.setAlign(parent.getAlign());
        }
        String vAlignAttribute = XmlElementUtil.getAttribute(element, prev + "vAlign");
        if (vAlignAttribute != null) {
            style.setVAlign(VerticalAlignment.valueOf(vAlignAttribute.toUpperCase()));
        } else {
            style.setVAlign(parent.getVAlign());
        }
        String borderBottomAttribute = XmlElementUtil.getAttribute(element, prev + "borderBottom");
        if (borderBottomAttribute != null) {
            style.setBorderBottom(BorderStyle.valueOf(borderBottomAttribute.toUpperCase()));
        } else {
            style.setBorderBottom(parent.getBorderBottom());
        }
        String borderLeftAttribute = XmlElementUtil.getAttribute(element, prev + "borderLeft");
        if (borderLeftAttribute != null) {
            style.setBorderLeft(BorderStyle.valueOf(borderLeftAttribute.toUpperCase()));
        } else {
            style.setBorderLeft(parent.getBorderLeft());
        }
        String borderTopAttribute = XmlElementUtil.getAttribute(element, prev + "borderTop");
        if (borderTopAttribute != null) {
            style.setBorderTop(BorderStyle.valueOf(borderTopAttribute.toUpperCase()));
        } else {
            style.setBorderTop(parent.getBorderTop());
        }
        String borderRightAttribute = XmlElementUtil.getAttribute(element, prev + "borderRight");
        if (borderRightAttribute != null) {
            style.setBorderRight(BorderStyle.valueOf(borderRightAttribute.toUpperCase()));
        } else {
            style.setBorderRight(parent.getBorderRight());
        }
        String styleFormatAttribute = XmlElementUtil.getAttribute(element, prev + "styleFormat");
        if (styleFormatAttribute != null) {
            style.setStyleFormat(styleFormatAttribute);
        } else {
            style.setStyleFormat(parent.getStyleFormat());
        }
        return style;
    }
}
