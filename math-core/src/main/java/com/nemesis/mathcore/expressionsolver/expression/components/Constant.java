package com.nemesis.mathcore.expressionsolver.expression.components;


/*

 */

import com.nemesis.mathcore.expressionsolver.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.utils.SyntaxUtils;

import java.math.BigDecimal;
import java.util.Objects;

public class Constant extends Base {

    public Constant(String number) {
        value = new BigDecimal(number);
    }

    public Constant(BigDecimal number) {
        value = number;
    }

    public Constant(Sign sign, BigDecimal value) {
        super.sign = sign;
        super.value = value;
    }

    @Override
    public Constant getDerivative() {
        return new Constant("0");
    }

    @Override
    public Component simplify() {
        // TODO: check mode (decimal/fraction)
        return this;
    }

    @Override
    public String toString() {
        String valueAsString = SyntaxUtils.removeNonSignificantZeros(value).toString();
        return ExpressionBuilder.addSign(sign.toString(), valueAsString);
    }

    @Override
    public boolean absEquals(Object obj) {
        return obj instanceof Constant && Objects.equals(this.value, ((Constant) obj).getValue());
    }
}
