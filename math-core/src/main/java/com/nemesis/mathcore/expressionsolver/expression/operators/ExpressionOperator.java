package com.nemesis.mathcore.expressionsolver.expression.operators;

public enum ExpressionOperator {

    SUM, SUBTRACT, NONE;

    @Override
    public String toString() {
        return this.equals(SUM) ? "+" : (this.equals(SUBTRACT) ? "-" : "");
    }
}
