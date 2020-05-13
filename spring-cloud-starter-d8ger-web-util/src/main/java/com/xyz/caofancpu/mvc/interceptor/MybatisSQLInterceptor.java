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

package com.xyz.caofancpu.mvc.interceptor;


import com.xyz.caofancpu.core.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.Properties;

/**
 * MybatisSQL拦截器切面
 * 首先通过注解定义该拦截器的切入点，
 * 对那个类的哪个方法进行拦截，
 * 防止方法重载需要声明参数类型以及个数
 *
 * @author D8GER
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
@Component
public class MybatisSQLInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation)
            throws Exception {
        // 通过拦截器得到被拦截的对象,就是上面配置的注解的对象
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        // 为了获取以及设置某些对象的属性值（某些对象的属性是没有getter/setter的），mybatis提供的快捷的通过反射设置获取属性值的工具类，当然也可以通过自己写反射完成
        MetaObject metaObject = MetaObject.forObject(
                statementHandler,
                SystemMetaObject.DEFAULT_OBJECT_FACTORY,
                SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY,
                new DefaultReflectorFactory()
        );
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        // 对映射语句进行选择过滤，如果是以ByPage结尾就拦截，否则不拦截
        String sqlId = mappedStatement.getId();
        // sql语句在对象BoundSql对象中，这个对象有get方法可以直接获取
        BoundSql boundSql = statementHandler.getBoundSql();
        // 获取原始sql，该sql是预处理的，有参数还没有被设置，被问号代替了
        String sql = boundSql.getSql();
        // 拿到我们给sql传入的参数对象
        Object param = boundSql.getParameterObject();
        Object result;
        String sqlLog = "执行SQL: [" +
                sql.replaceAll("\\s{2,}", StringUtils.SPACE).replaceAll(" ,", ",").replaceAll(" ;", ";") +
                "]\n传入参数: [" + JSONUtil.toJSONStringWithDateFormatAndEnumToString(param) + "]";
        result = invocation.proceed();
        return result;
    }

    @Override
    public Object plugin(Object target) {
        // 表示给一个目标对象织入一个拦截器，该代码织入的的拦截器对象就是本身this对象
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
