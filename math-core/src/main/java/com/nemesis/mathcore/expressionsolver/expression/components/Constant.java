package com.nemesis.mathcore.expressionsolver.expression.components;


import com.nemesis.mathcore.expressionsolver.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.SyntaxUtils;

import java.math.BigDecimal;
import java.util.Objects;

import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;

public class Constant extends Base {

    private final BigDecimal value;

    public Constant() {
        this.value = BigDecimal.ZERO;
    }

    public Constant(String number) {
        value = new BigDecimal(number);
    }

    public Constant(BigDecimal number) {
        value = number;
    }

    public Constant(Sign sign, BigDecimal value) {
        if (sign == MINUS && value.compareTo(BigDecimal.ZERO) < 0) {
            sign = PLUS;
            value = value.abs();
        }
        super.sign = sign;
        this.value = value;
    }

    @Override
    public BigDecimal getValue() {
        return sign.equals(PLUS) ? value : value.multiply(MINUS_ONE_DECIMAL);
    }

    @Override
    public Constant getDerivative(char var) {
        return new Constant("0");
    }

    @Override
    public Component rewrite(Rule rule) {
        // TODO: check mode (decimal/fraction)
        if (sign == MINUS && this.value.compareTo(BigDecimal.ZERO) < 0) {
            return new Constant(value.abs());
        }
        return this;
    }

    @Override
    public Boolean isScalar() {
        return true;
    }

    @Override
    public String toString() {
        // TODO: check mode (decimal/fraction)
        String valueAsString = SyntaxUtils.removeNonSignificantZeros(value).toString();
        return ExpressionBuilder.addSign(sign.toString(), valueAsString);
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Constant) {
            return this.getValue().compareTo(((Constant) o).getValue());
        } else {
            return Base.compare(this, o);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Constant constant = (Constant) o;
        return Objects.equals(value, constant.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
