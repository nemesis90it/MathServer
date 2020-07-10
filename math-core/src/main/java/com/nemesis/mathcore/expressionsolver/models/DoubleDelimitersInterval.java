package com.nemesis.mathcore.expressionsolver.models;

import com.nemesis.mathcore.expressionsolver.expression.components.Component;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.nemesis.mathcore.expressionsolver.models.RelationalOperator.LESS_THAN;
import static com.nemesis.mathcore.expressionsolver.models.RelationalOperator.LESS_THAN_OR_EQUALS;

@Data
@AllArgsConstructor
public class DoubleDelimitersInterval implements GenericInterval {

    private final String variable;
    private final Type type;
    private final Component leftDelimiter;
    private final Component rightDelimiter;


    @Override
    public String toString() {
        return String.format(type.getStringPattern(), leftDelimiter.toString(), variable, rightDelimiter.toString());
    }

    @Override
    public String toLatex() {
        return String.format(type.getLatexPattern(), leftDelimiter.toLatex(), variable, rightDelimiter.toLatex());
    }

    public enum Type implements GenericType {

        STRICTLY_BETWEEN(
                "%s " + LESS_THAN.getStringValue() + " %s " + LESS_THAN.getStringValue() + " %s",
                "%s " + LESS_THAN.getLatexValue() + " %s " + LESS_THAN.getLatexValue() + " %s"
        ),
        LEFT_STRICTLY_BETWEEN(
                "%s " + LESS_THAN.getStringValue() + " %s " + LESS_THAN_OR_EQUALS.getStringValue() + " %s",
                "%s " + LESS_THAN.getLatexValue() + " %s " + LESS_THAN_OR_EQUALS.getLatexValue() + " %s"
        ),
        RIGHT_STRICTLY_BETWEEN(
                "%s " + LESS_THAN_OR_EQUALS.getStringValue() + " %s " + LESS_THAN.getStringValue() + " %s",
                "%s " + LESS_THAN_OR_EQUALS.getLatexValue() + " %s " + LESS_THAN.getLatexValue() + " %s"
        ),
        BETWEEN(
                "%s " + LESS_THAN_OR_EQUALS.getStringValue() + " %s " + LESS_THAN_OR_EQUALS.getStringValue() + " %s",
                "%s " + LESS_THAN_OR_EQUALS.getLatexValue() + " %s " + LESS_THAN_OR_EQUALS.getLatexValue() + " %s"
        );

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

        @Override
        public String getLatexPattern() {
            return latexPattern;
        }
    }

}
