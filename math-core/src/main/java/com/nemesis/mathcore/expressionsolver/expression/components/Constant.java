package com.nemesis.mathcore.expressionsolver.expression.components;


/*

 */

import com.nemesis.mathcore.expressionsolver.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.utils.SyntaxUtils;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Objects;

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
        super.sign = sign;
        this.value = value;
    }

    @Override
    public BigDecimal getValue() {
        return sign.equals(PLUS) ? value : value.multiply(MINUS_ONE_DECIMAL);
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

    @Override
    public int compareTo(Object o) {
        if (o instanceof Constant) {
            return this.getValue().compareTo(((Constant) o).getValue());
        } else {
            return 1;
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
