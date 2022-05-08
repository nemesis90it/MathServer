package com.nemesis.mathcore.expressionsolver.models;

import java.util.Arrays;
import java.util.Objects;

public enum RelationalOperator implements Stringable {

    EQ("=", "="),
    NEQ("!=", "\\neq"),
    GT(">", ">"),
    GTE("≥", "\\geq"),
    LT("<", "<"),
    LTE("≤", "\\leq");

    private final String stringValue;
    private final String latexValue;

    RelationalOperator(String stringValue, String latexValue) {
        this.stringValue = stringValue;
        this.latexValue = latexValue;
    }

    public static RelationalOperator of(String operatorString) {
        return Arrays.stream(RelationalOperator.values())
                .filter(operator -> Objects.equals(operator.stringValue, operatorString))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public String toString() {
        return stringValue;
    }

    @Override
    public String toLatex() {
        return latexValue;
    }

    public RelationalOperator inverse() {
        return switch (this) {
            case GT -> LT;
            case GTE -> LTE;
            case LT -> GT;
            case LTE -> GTE;
            case EQ -> NEQ;
            case NEQ -> EQ;
        };
    }

    public boolean isInequality() {
        return this == GT || this == GTE ||
                this == LT || this == LTE;
    }

    public boolean isEquality() {
        return this == EQ || this == NEQ;
    }

}
