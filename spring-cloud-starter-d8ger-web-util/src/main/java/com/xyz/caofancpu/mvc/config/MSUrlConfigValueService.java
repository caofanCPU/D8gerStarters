package com.xyz.caofancpu.mvc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

/**
 * 统一封装用到的[url]
 */
@Service("msUrlConfigValueService")
@DependsOn("initContextPropertyInitializer")
public class MSUrlConfigValueService {

    @Value("${ms.file.url}")
    public String fileAccessUrl;

    @Value("${ms.sso.url}")
    private String ssoAccessUrl;

}
