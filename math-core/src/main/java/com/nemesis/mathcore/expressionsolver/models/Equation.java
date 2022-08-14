package com.nemesis.mathcore.expressionsolver.models;

import com.nemesis.mathcore.expressionsolver.components.Component;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Equation extends Input implements Stringable {
    private Component leftComponent;
    private RelationalOperator operator;
    private Component rightComponent;


    @Override
    public String toLatex() {
        return leftComponent.toLatex() + "\\ " + operator.toLatex() + "\\ " + rightComponent.toLatex();
    }

    @Override
    public String toString() {
        return leftComponent.toString() + operator.toString() + rightComponent.toString();
    }
}
