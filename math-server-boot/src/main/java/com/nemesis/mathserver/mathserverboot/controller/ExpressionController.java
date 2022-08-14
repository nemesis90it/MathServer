package com.nemesis.mathserver.mathserverboot.controller;


import com.nemesis.mathcore.expressionsolver.components.Expression;
import com.nemesis.mathcore.expressionsolver.components.Variable;
import com.nemesis.mathcore.expressionsolver.models.Equation;
import com.nemesis.mathcore.expressionsolver.utils.MathCoreContext;
import com.nemesis.mathserver.mathserverboot.InputParser;
import com.nemesis.mathserver.mathserverboot.evaluator.EquationEvaluator;
import com.nemesis.mathserver.mathserverboot.evaluator.ExpressionEvaluator;
import com.nemesis.mathserver.mathserverboot.model.EvaluationResult;
import com.nemesis.mathserver.mathserverboot.model.InputParsingResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping(value = "/expression", produces = "application/json")
public class ExpressionController {


    @GetMapping("/compute")
    public EvaluationResult evaluate(@RequestParam("expression") String input, @RequestParam(required = false) MathCoreContext.Mode mode) {

        if (mode == null) {
            mode = MathCoreContext.Mode.FRACTIONAL;
        }
        MathCoreContext.setNumericMode(mode);

        if (MathCoreContext.getNumericMode() == MathCoreContext.Mode.FRACTIONAL && input.contains(".")) {
            throw new IllegalArgumentException("Decimal numbers is not allowed in fractional mode");
        }

        input = input.replace(" ", "");


        InputParsingResult parsedInput = InputParser.parse(input);

        Set<Variable> variables = parsedInput.getVariables();
        if (variables.size() > 1) {
            throw new UnsupportedOperationException("Multi variable is not supported yet");
        }
        Variable variable = variables.stream().findFirst().orElse(null);

        return switch (parsedInput.getParsingResult()) {
            case Equation equation -> EquationEvaluator.evaluate(equation, variable);
            case Expression expression -> ExpressionEvaluator.evaluate(expression, variable);
            default -> throw new IllegalStateException("Unexpected value: " + parsedInput.getParsingResult().getClass());
        };

    }
}

