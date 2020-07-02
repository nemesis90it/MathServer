package com.nemesis.mathcore.expressionsolver.models.interval;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DoubleDelimitersInterval implements GenericInterval {

    private final String variable;
    private final Type type;
    private final String leftDelimiter;
    private final String rightDelimiter;


    @Override
    public String toString() {
        return String.format(type.getStringPattern(), leftDelimiter, variable, rightDelimiter);
    }

    @Override
    public String toLatex() {
        return String.format(type.getLatexPattern(), leftDelimiter, variable, rightDelimiter);
    }

    enum Type implements GenericType {

        STRICTLY_BETWEEN("%s < %s < %s", "%s < %s < %s"),
        LEFT_STRICTLY_BETWEEN("%s < %s <= %s", "%s < %s \\leq %s"),
        RIGHT_STRICTLY_BETWEEN("%s <= %s < %s", "%s \\leq %s < %s"),
        BETWEEN("%s <= %s <= %s", "%s \\leq %s \\leq %s");

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
