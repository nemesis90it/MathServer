package com.nemesis.mathcore.expressionsolver.models.interval;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SingleDelimiterInterval implements GenericInterval {

    private final char variable;
    private final Type type;
    private final String point;

    @Override
    public String toString() {
        return String.format(type.getStringPattern(), variable, point);
    }

    @Override
    public String toLatex() {
        return String.format(type.getLatexPattern(), variable, point);
    }

    public enum Type implements GenericType {

        EQUALS("%s = %s", "%s = %s"),
        NOT_EQUALS("%s != %s", "%s \\neq %s"),
        GREATER_THAN("%s > %s", "%s > %s"),
        GREATER_THAN_OR_EQUALS("%s >= %s", "%s \\geq %s"),
        LESS_THAN("%s < %s", "%s < %s"),
        LESS_THAN_OR_EQUALS("%s <= %s", "%s \\leq %s");

        private final String stringPattern;
        private final String latexPattern;

        Type(String stringPattern, String latexPattern) {
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
}
