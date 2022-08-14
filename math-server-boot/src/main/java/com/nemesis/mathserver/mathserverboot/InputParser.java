package com.nemesis.mathserver.mathserverboot;

import com.nemesis.mathcore.expressionsolver.parser.EquationParser;
import com.nemesis.mathcore.expressionsolver.parser.ExpressionParser;
import com.nemesis.mathcore.expressionsolver.components.Expression;
import com.nemesis.mathcore.expressionsolver.components.Variable;
import com.nemesis.mathcore.expressionsolver.models.Equation;
import com.nemesis.mathcore.expressionsolver.utils.MathCoreContext;
import com.nemesis.mathserver.mathserverboot.model.InputParsingResult;

import java.util.HashSet;
import java.util.Set;

/*
Input ::= Expression [RelationalOperator Expression]
 */
public class InputParser {
    public static InputParsingResult parse(String inputString) {

        InputParsingResult parsingResult;
        Set<Variable> variables = new HashSet<>();

        Equation parsedEquation = EquationParser.parse(inputString);
        if (parsedEquation != null) {
            variables.addAll(parsedEquation.getLeftComponent().getVariables());
            variables.addAll(parsedEquation.getRightComponent().getVariables());
            parsingResult = new InputParsingResult(parsedEquation, variables);
        } else {
            Expression parsedExpression = ExpressionParser.parse(inputString);
            if (parsedExpression != null) {
                variables.addAll(parsedExpression.getVariables());
                parsingResult = new InputParsingResult(parsedExpression, variables);
            } else {
                throw new IllegalArgumentException("Input string [" + inputString + "] is not recognized");
            }
        }

        return parsingResult;


    }
}
