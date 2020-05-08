package com.xyz.caofancpu.mvc.config;

import com.xyz.caofancpu.enumtype.AutoDispatchMyBatisEnumTypeHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;

/**
 * MyBatis配置
 * 推荐使用mybatis.configuration.default-enum-type-handler进行配置
 *
 * @author caofanCPU
 * @see <a href=http://www.mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/>
 */
//@Configuration
@Slf4j
@Deprecated
public class MyBatisConfig {

    /**
     * 方式三: 设置sqlSessionFactory属性
     * 依赖: @AutoConfigureAfter(MybatisAutoConfiguration.class)
     *
     * @deprecated 不推荐
     */
//    @Resource
    private SqlSessionFactory sqlSessionFactory;

    /**
     * 方式二: 个性化设置默认枚举转换类
     * <p>
     * mybatis-spring-boot-start支持配置自定义枚举转换器, 不推荐用此方式
     */
//    @Bean
    @Deprecated
    public ConfigurationCustomizer configurationCustomizer() {
        log.info("自定义枚举转换器注册成功!");
        return configuration -> configuration.setDefaultEnumTypeHandler(AutoDispatchMyBatisEnumTypeHandler.class);
    }

    /**
     * 注册自定义枚举转换器
     */
//    @PostConstruct
    @Deprecated
    public void customMybatisEnumTypeHandler() {
        TypeHandlerRegistry typeHandlerRegistry = sqlSessionFactory.getConfiguration().getTypeHandlerRegistry();
        typeHandlerRegistry.setDefaultEnumTypeHandler(AutoDispatchMyBatisEnumTypeHandler.class);
        log.info("自定义枚举转换器注册成功!");
    }
}
