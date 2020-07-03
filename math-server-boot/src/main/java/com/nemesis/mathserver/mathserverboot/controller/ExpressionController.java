package com.nemesis.mathserver.mathserverboot.controller;


import com.nemesis.mathcore.expressionsolver.ExpressionParser;
import com.nemesis.mathcore.expressionsolver.ExpressionUtils;
import com.nemesis.mathcore.expressionsolver.expression.components.Component;
import com.nemesis.mathcore.expressionsolver.expression.components.Expression;
import com.nemesis.mathcore.expressionsolver.expression.components.Variable;
import com.nemesis.mathcore.expressionsolver.utils.MathCoreContext;
import com.nemesis.mathserver.mathserverboot.model.EvaluationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping(value = "/expression", produces = "text/plain")
public class ExpressionController {

//    private static final Pattern genericNumPattern = Pattern.compile(Constants.IS_GENERIC_NUM_REGEX);
//    private static final Pattern derivativePattern = Pattern.compile(Constants.DERIVATIVE_INPUT_REGEX);


    @GetMapping("/compute")
    public EvaluationResult evaluate(@RequestParam String expression, @RequestParam(required = false) MathCoreContext.Mode mode) {

        if (mode == null) {
            mode = MathCoreContext.Mode.FRACTIONAL;
        }
        MathCoreContext.setNumericMode(mode);

        expression = expression.replace(" ", "");

        final Expression parsedExpression = ExpressionParser.parse(expression);

        EvaluationResult result = new EvaluationResult();

        log.info("Simplifying function [" + expression + "]");
        final Component simplyfiedExpression = ExpressionUtils.simplify(expression);
        result.setSimplyfiedForm(simplyfiedExpression.toLatex());

        Set<Variable> variables = simplyfiedExpression.getVariables();

        for (Variable variable : variables) {
            log.info("Evaluating derivative of [{}] for variable [{}]", expression, variable);
            result.addDerivative(variable, ExpressionUtils.getDerivative(expression, variable).toLatex());

            log.info("Calculating domain of [{}] for variable [{}]", expression, variable);
            result.addDomain(variable, ExpressionUtils.getDomain(expression, variable));
        }

        if (simplyfiedExpression.isScalar()) {
            log.info("Evaluating: [" + expression + "]");
            result.setNumericValue(String.valueOf(parsedExpression.getValue()));
        }

        log.info("Result [" + result + "]");
        return result;
    }
}

