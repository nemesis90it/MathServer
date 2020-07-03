package com.nemesis.mathcore.expressionsolver.models;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NoDelimiterInterval implements GenericInterval {

    private final char variable;
    private final Type type;

    @Override
    public String toString() {
        return String.format(type.getStringPattern(), variable);
    }

    @Override
    public String toLatex() {
        return String.format(type.getLatexPattern(), variable);
    }

    public enum Type implements GenericType {

        FOR_EACH("for each %s", "\\forall %s"),
        VOID("for no value of %s", "\\nexists %s"),
        UNDEFINED("undefined domain", "undefined domain");
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
