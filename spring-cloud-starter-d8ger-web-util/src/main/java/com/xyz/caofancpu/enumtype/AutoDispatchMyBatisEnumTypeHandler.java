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
@SuppressWarnings("unchecked")
@Slf4j
public class AutoDispatchMyBatisEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

    private final BaseTypeHandler typeHandler;

    public AutoDispatchMyBatisEnumTypeHandler(Class<E> enumType) {
        if (Objects.isNull(enumType)) {
            throw new GlobalErrorInfoRuntimeException("参数非法, 类型不能为空");
        }
        if (IEnum.class.isAssignableFrom(enumType)) {
            typeHandler = new BaseMybatisEnumTypeHandler(enumType);
            log.info("创建枚举类型: [{}]的自定义DB转换器: [{}]", enumType.getSimpleName(), typeHandler.getClass().getSimpleName());
        } else {
            typeHandler = new EnumOrdinalTypeHandler(enumType);
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
        return (E) typeHandler.getNullableResult(rs, columnName);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        return (E) typeHandler.getNullableResult(rs, columnIndex);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        return (E) typeHandler.getNullableResult(cs, columnIndex);
    }
}
