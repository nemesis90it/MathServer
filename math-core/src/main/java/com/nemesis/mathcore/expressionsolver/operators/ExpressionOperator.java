package com.nemesis.mathcore.expressionsolver.operators;

public enum ExpressionOperator implements Comparable<ExpressionOperator> {

    SUM, SUBTRACT, NONE;

    @Override
    public String toString() {
        return this.equals(SUM) ? "+" : (this.equals(SUBTRACT) ? "-" : "");
    }
}
