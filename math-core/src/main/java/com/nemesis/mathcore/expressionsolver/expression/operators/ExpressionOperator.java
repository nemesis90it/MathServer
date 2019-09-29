package com.nemesis.mathcore.expressionsolver.expression.operators;

public enum ExpressionOperator {

    SUM, SUBSTRACT, NONE;

    @Override
    public String toString() {
        return this.equals(SUM) ? "+" : (this.equals(SUBSTRACT) ? "-" : "");
    }
}
