package com.nemesis.mathcore.expressionsolver.models;


import com.nemesis.mathcore.expressionsolver.expression.components.Component;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SingleDelimiterInterval implements GenericInterval {

    private final char variable;
    private final RelationalOperator relationalOperator;
    private final Component point;

    @Override
    public String toString() {
        return String.format(relationalOperator.getStringPattern(), variable, point.toString());
    }

    @Override
    public String toLatex() {
        return String.format(relationalOperator.getLatexPattern(), variable, point.toLatex());
    }

}
