package com.nemesis.mathcore.expressionsolver.models;

public enum RelationalOperator {

    EQUALS("=", "="),
    NOT_EQUALS("!=", "\\neq"),
    GREATER_THAN(">", ">"),
    GREATER_THAN_OR_EQUALS(">=", "\\geq"),
    LESS_THAN("<", "<"),
    LESS_THAN_OR_EQUALS("<=", "\\leq");

    private final String stringValue;
    private final String latexValue;

    RelationalOperator(String stringValue, String latexValue) {
        this.stringValue = stringValue;
        this.latexValue = latexValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public String getLatexValue() {
        return latexValue;
    }

    public RelationalOperator inverse() {
        return switch (this) {
            case GREATER_THAN -> LESS_THAN;
            case GREATER_THAN_OR_EQUALS -> LESS_THAN_OR_EQUALS;
            case LESS_THAN -> GREATER_THAN;
            case LESS_THAN_OR_EQUALS -> GREATER_THAN_OR_EQUALS;
            case EQUALS -> NOT_EQUALS;
            case NOT_EQUALS -> EQUALS;
        };
    }

    public boolean isInequality() {
        return this == GREATER_THAN || this == GREATER_THAN_OR_EQUALS ||
                this == LESS_THAN || this == LESS_THAN_OR_EQUALS;
    }

    public boolean isEquality() {
        return this == EQUALS || this == NOT_EQUALS;
    }

}
