package com.nemesis.mathcore.expressionsolver.models;

public enum EquationOperator {

    EQUALS("=", "="),
    NOT_EQUALS("!=", "\\neq"),
    GREATER_THAN(">", ">"),
    LESS_THAN("<", "<"),
    GREATER_THAN_OR_EQUALS(">=", "\\geq"),
    LESS_THAN_OR_EQUALS("<=", "\\leq");

    private final String stringValue;
    private final String latexValue;

    EquationOperator(String stringValue, String latexValue) {
        this.stringValue = stringValue;
        this.latexValue = latexValue;
    }


    public String getStringValue() {
        return stringValue;
    }

    public String getLatexValue() {
        return latexValue;
    }
}
