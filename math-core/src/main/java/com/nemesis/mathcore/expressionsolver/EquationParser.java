package com.nemesis.mathcore.expressionsolver;

import com.nemesis.mathcore.expressionsolver.components.Expression;
import com.nemesis.mathcore.expressionsolver.models.Equation;
import com.nemesis.mathcore.expressionsolver.models.RelationalOperator;

import static com.nemesis.mathcore.expressionsolver.models.RelationalOperator.*;


public class EquationParser {
    public static Equation parse(String equation) {

        String[] components;
        RelationalOperator operator;

        if (equation.contains("!=")) {
            components = equation.split("!=");
            operator = NEQ;
        } else if (equation.contains("<=")) {
            components = equation.split("<=");
            operator = LTE;
        } else if (equation.contains(">=")) {
            components = equation.split(">=");
            operator = GTE;
        } else if (equation.contains("=")) {
            components = equation.split("=");
            operator = EQ;
        } else if (equation.contains("<")) {
            components = equation.split("<");
            operator = GT;
        } else if (equation.contains(">")) {
            components = equation.split(">");
            operator = LT;
        } else {
            throw new IllegalArgumentException("String [" + equation + "] is not recognized as equation");
        }

        Expression parsedLeftExpression = ExpressionParser.parse(components[0]);
        Expression parsedRightExpression = ExpressionParser.parse(components[1]);
        return new Equation(parsedLeftExpression, operator, parsedRightExpression);
    }
}
