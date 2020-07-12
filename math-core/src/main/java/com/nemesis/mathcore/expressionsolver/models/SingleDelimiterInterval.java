package com.nemesis.mathcore.expressionsolver.models;


import com.nemesis.mathcore.expressionsolver.components.Component;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SingleDelimiterInterval that = (SingleDelimiterInterval) o;
        return Objects.equals(variable, that.variable) &&
                intervalType == that.intervalType &&
                Objects.equals(delimiter, that.delimiter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, intervalType, delimiter);
    }

    @Override
    public int compareTo(GenericInterval other) {
        if (other instanceof SingleDelimiterInterval otherInterval) {
            return this.delimiter.compareTo(otherInterval.getDelimiter());
        } else {
            return GenericInterval.super.compareTo(other);
        }
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
