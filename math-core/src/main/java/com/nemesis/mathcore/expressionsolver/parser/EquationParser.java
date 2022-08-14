package com.nemesis.mathcore.expressionsolver.parser;

import com.nemesis.mathcore.expressionsolver.components.Expression;
import com.nemesis.mathcore.expressionsolver.models.Equation;
import com.nemesis.mathcore.expressionsolver.models.RelationalOperator;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

import static com.nemesis.mathcore.expressionsolver.models.RelationalOperator.*;

@Slf4j
public class EquationParser {
    public static Equation parse(String equation) {

        String[] components = null;
        RelationalOperator operator = null;

        for (RelationalOperator relationalOperator : values()) {
            String symbol = relationalOperator.toString();
            if ((equation.contains(symbol))) {
                operator = relationalOperator;
                components = equation.split(symbol);
                break;
            }
        }

        if (operator == null) {
            log.debug("String [{}] is not recognized as equation", equation);
            return null;
        }

        Expression parsedLeftExpression = ExpressionParser.parse(components[0]);
        Expression parsedRightExpression = ExpressionParser.parse(components[1]);
        return new Equation(parsedLeftExpression, operator, parsedRightExpression);
    }
}
