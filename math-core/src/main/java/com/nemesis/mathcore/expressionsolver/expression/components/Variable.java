package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Objects;

import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;

@Data
@EqualsAndHashCode(callSuper = false)
public class Variable extends Base {

    private char name;

    public Variable(char name) {
        this.name = name;
    }

    public Variable(Sign sign, char name) {
        super.sign = sign;
        this.name = name;
    }

    @Override
    public BigDecimal getValue() {
        throw new UnsupportedOperationException("Variables have no value");
    }

    public char getName() {
        return name;
    }

    @Override
    public Component getDerivative() {
        return new Constant("1");
    }

    @Override
    public Component simplify() {
        return this;
    }

    @Override
    public String toString() {
        if (sign.equals(PLUS)) {
            return String.valueOf(name);
        } else {
            return "(-" + name + ")";
        }
    }

    @Override
    public boolean absEquals(Object obj) {
        return obj instanceof Variable && Objects.equals(this.name, ((Variable) obj).getName());
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Variable) {
            return String.valueOf(this.name).compareTo(String.valueOf(o));
        } else {
            return -1;
        }
    }
}
