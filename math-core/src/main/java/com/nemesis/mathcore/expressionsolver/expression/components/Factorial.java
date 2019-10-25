package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.utils.SyntaxUtils;
import com.nemesis.mathcore.utils.MathUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Objects;

import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;

@Data
@EqualsAndHashCode(callSuper = false)
public class Factorial extends Base {

    private Factor argument;

    public Factorial(Factor argument) {
        this.argument = argument;
    }

    public Factorial(Sign sign, Factor argument) {
        super.sign = sign;
        this.argument = argument;
    }

    @Override
    public BigDecimal getValue() {
        if (value == null) {
            BigDecimal bodyValue = argument.getValue();
            bodyValue = SyntaxUtils.removeNonSignificantZeros(bodyValue);
            String bodyValueAsString = bodyValue.toPlainString();
            if (bodyValueAsString.contains(".") || bodyValueAsString.startsWith("-")) {
                throw new IllegalArgumentException("Factorial must be a positive integer");
            }
            BigDecimal absValue = MathUtils.factorial(bodyValue);
            this.value = sign.equals(PLUS) ? absValue : absValue.multiply(MINUS_ONE_DECIMAL);
        }
        return value;
    }

    @Override
    public Component getDerivative() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Component simplify() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        if (sign.equals(PLUS)) {
            return "(" + argument + ")!";
        } else {
            return sign + "(" + argument + ")!";
        }
    }

    @Override
    public boolean absEquals(Object obj) {
        return obj instanceof Factorial && Objects.equals(this.argument, ((Factorial) obj).getArgument());
    }

    @Override
    public int compareTo(Object o) {
        throw new UnsupportedOperationException();
    }

}