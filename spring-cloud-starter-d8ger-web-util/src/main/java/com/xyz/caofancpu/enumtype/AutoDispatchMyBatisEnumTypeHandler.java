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

package com.xyz.caofancpu.enumtype;

import com.xyz.caofancpu.constant.IEnum;
import com.xyz.caofancpu.enumtype.converter.BaseMybatisEnumTypeHandler;
import com.xyz.caofancpu.result.GlobalErrorInfoRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * 枚举类型自动转换器
 *
 * @author D8GER
 */
@Slf4j
public class AutoDispatchMyBatisEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

    private final BaseTypeHandler<E> typeHandler;

    public AutoDispatchMyBatisEnumTypeHandler(Class<E> enumType) {
        if (Objects.isNull(enumType)) {
            throw new GlobalErrorInfoRuntimeException("参数非法, 类型不能为空");
        }
        if (IEnum.class.isAssignableFrom(enumType)) {
            typeHandler = new BaseMybatisEnumTypeHandler<>(enumType);
            log.info("创建枚举类型: [{}]的自定义DB转换器: [{}]", enumType.getSimpleName(), typeHandler.getClass().getSimpleName());
        } else {
            typeHandler = new EnumOrdinalTypeHandler<>(enumType);
            log.info("创建枚举类型: [{}]的默认DB转换器: [{}]", enumType.getSimpleName(), typeHandler.getClass().getSimpleName());
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType)
            throws SQLException {
        typeHandler.setNonNullParameter(ps, i, parameter, jdbcType);
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        return typeHandler.getNullableResult(rs, columnName);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        return typeHandler.getNullableResult(rs, columnIndex);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        return typeHandler.getNullableResult(cs, columnIndex);
    }
}
