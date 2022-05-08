package com.nemesis.mathcore.expressionsolver.models;

import com.nemesis.mathcore.expressionsolver.components.Expression;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Equation implements Stringable {
    private Expression leftComponent;
    private RelationalOperator operator;
    private Expression rightComponent;

    @Override
    public String toLatex() {
        return leftComponent.toLatex() + "\\ " + operator.toLatex() + "\\ " + rightComponent.toLatex();
    }

    @Override
    public String toString() {
        return leftComponent.toString() + operator.toString() + rightComponent.toString();
    }
}
