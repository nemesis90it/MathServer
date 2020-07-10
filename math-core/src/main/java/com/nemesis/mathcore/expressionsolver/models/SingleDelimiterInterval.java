package com.nemesis.mathcore.expressionsolver.models;


import com.nemesis.mathcore.expressionsolver.expression.components.Component;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SingleDelimiterInterval implements GenericInterval {

    private final String variable;
    private final Type intervalType;
    private final Component delimiter;


    @Override
    public String toString() {
        return String.format(intervalType.getStringPattern(), variable, delimiter.toString());
    }

    @Override
    public String toLatex() {
        return String.format(intervalType.getLatexPattern(), variable, delimiter.toLatex());
    }

    public enum Type implements GenericType {

        EQUALS(
                "%s " + RelationalOperator.EQUALS.getStringValue() + " %s",
                "%s " + RelationalOperator.EQUALS.getLatexValue() + " %s"
        ),
        NOT_EQUALS(
                "%s " + RelationalOperator.NOT_EQUALS.getStringValue() + " %s",
                "%s " + RelationalOperator.NOT_EQUALS.getLatexValue() + " %s"
        ),
        GREATER_THAN(
                "%s " + RelationalOperator.GREATER_THAN.getStringValue() + " %s",
                "%s " + RelationalOperator.GREATER_THAN.getLatexValue() + " %s"
        ),
        GREATER_THAN_OR_EQUALS(
                "%s " + RelationalOperator.GREATER_THAN_OR_EQUALS.getStringValue() + " %s",
                "%s " + RelationalOperator.GREATER_THAN_OR_EQUALS.getLatexValue() + " %s"
        ),
        LESS_THAN(
                "%s " + RelationalOperator.LESS_THAN.getStringValue() + " %s",
                "%s " + RelationalOperator.LESS_THAN.getLatexValue() + " %s"
        ),
        LESS_THAN_OR_EQUALS(
                "%s " + RelationalOperator.LESS_THAN_OR_EQUALS.getStringValue() + " %s",
                "%s " + RelationalOperator.LESS_THAN_OR_EQUALS.getLatexValue() + " %s"
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
