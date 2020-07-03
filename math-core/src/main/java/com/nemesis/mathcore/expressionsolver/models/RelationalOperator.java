package com.nemesis.mathcore.expressionsolver.models;

public enum RelationalOperator implements GenericInterval.GenericType {

    EQUALS("%s = %s", "%s = %s"),
    NOT_EQUALS("%s != %s", "%s \\neq %s"),
    GREATER_THAN("%s > %s", "%s > %s"),
    GREATER_THAN_OR_EQUALS("%s >= %s", "%s \\geq %s"),
    LESS_THAN("%s < %s", "%s < %s"),
    LESS_THAN_OR_EQUALS("%s <= %s", "%s \\leq %s");

    private final String stringPattern;
    private final String latexPattern;

    RelationalOperator(String stringPattern, String latexPattern) {
        this.stringPattern = stringPattern;
        this.latexPattern = latexPattern;
    }

    @Override
    public String getStringPattern() {
        return stringPattern;
    }

    public String getLatexPattern() {
        return latexPattern;
    }
}
