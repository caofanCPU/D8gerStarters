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

package com.xyz.caofancpu.enumtype.converter;

import com.xyz.caofancpu.constant.IEnum;
import com.xyz.caofancpu.result.GlobalErrorInfoRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * MyBatis枚举类型转换处理器
 *
 * @author D8GER
 */
@Slf4j
public class BaseMybatisEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

    private final Class<E> type;
    private final E[] enums;

    public BaseMybatisEnumTypeHandler(Class<E> type) {
        if (Objects.isNull(type)) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
        this.enums = type.getEnumConstants();
        if (Objects.isNull(this.enums)) {
            throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum type.");
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setInt(i, ((IEnum) parameter).getValue());
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : valueOf(value);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        int value = rs.getInt(columnIndex);
        return rs.wasNull() ? null : valueOf(value);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        int value = cs.getInt(columnIndex);
        return cs.wasNull() ? null : valueOf(value);
    }

    private E valueOf(int value) {
        E resultEnum = null;
        for (E enumConstant : enums) {
            if (!(enumConstant instanceof IEnum)) {
                continue;
            }
            IEnum temp = (IEnum) enumConstant;
            if (value == temp.getValue()) {
                resultEnum = enumConstant;
            }
        }
        if (Objects.isNull(resultEnum)) {
            log.error("枚举转换异常: 值[{}]无法转换为枚举类[{}]", value, type.getSimpleName());
            throw new GlobalErrorInfoRuntimeException("Cannot convert " + value + " to " + type.getSimpleName() + " by enum value.");
        }
        return resultEnum;
    }
}
