package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.SyntaxUtils;
import com.nemesis.mathcore.utils.MathUtils;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;

import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;

@Data
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
    public Component getDerivative(char var) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Component rewrite(Rule rule) {

        Component simplifiedArg = argument.rewrite(rule);

        if (simplifiedArg instanceof Constant && simplifiedArg.getValue().equals(BigDecimal.ZERO)) {
            return new Constant("1");
        }

        return new Factorial(Factor.getFactor(simplifiedArg));
    }

    @Override
    public Boolean isScalar() {
        return this.argument.isScalar();
    }

    @Override
    public Constant getValueAsConstant() {
        return new Constant(this.getValue());
    }

    @Override
    public String toString() {
        return this.argument + "!";
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Factorial) {
            Comparator<Factorial> argComparator = Comparator.comparing(Factorial::getArgument);
            return argComparator.compare(this, (Factorial) o);
        } else {
            return Base.compare(this, o);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Factorial factorial = (Factorial) o;
        return Objects.equals(argument, factorial.argument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(argument);
    }
}
