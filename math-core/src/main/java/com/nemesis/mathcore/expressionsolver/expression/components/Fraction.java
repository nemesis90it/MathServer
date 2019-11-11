package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.ExpressionBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class Fraction extends Constant {

    private BigInteger numerator;
    private BigInteger denominator;

    @Override
    public String toString() {
        return ExpressionBuilder.division(numerator.toString(), denominator.toString());
    }
}
