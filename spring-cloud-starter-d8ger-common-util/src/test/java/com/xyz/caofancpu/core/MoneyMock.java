package com.xyz.caofancpu.core;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author D8GER<caofan.d8ger @ bytedance.com>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MoneyMock {
    private BigDecimal amount;
    private String amountValue;
}
