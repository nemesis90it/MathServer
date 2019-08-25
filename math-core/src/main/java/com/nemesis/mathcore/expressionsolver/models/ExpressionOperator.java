package com.nemesis.mathcore.expressionsolver.models;

public enum ExpressionOperator {

    SUM, SUBSTRACT, NONE;

    @Override
    public String toString() {
        return this.equals(SUM) ? "+" : (this.equals(SUBSTRACT) ? "-" : "");
    }
}
