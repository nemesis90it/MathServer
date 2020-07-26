package com.nemesis.mathserver.mathserverboot.controller;


import com.nemesis.mathcore.expressionsolver.ExpressionParser;
import com.nemesis.mathcore.expressionsolver.ExpressionUtils;
import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.components.Expression;
import com.nemesis.mathcore.expressionsolver.components.Variable;
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
@RequestMapping(value = "/expression", produces = "application/json")
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
        final Component simplifiedExpression = ExpressionUtils.simplify(expression);
        result.setSimplifiedForm(simplifiedExpression.toLatex());

        Set<Variable> variables = simplifiedExpression.getVariables();

        if (variables.size() > 1) {
            throw new UnsupportedOperationException("Multi variable is not supported yet");
        }

        Variable variable = variables.stream().findFirst().orElse(null);

        if (variable != null) {
            log.info("Evaluating derivative of [{}] for variable [{}]", expression, variable);
            result.setDerivative(ExpressionUtils.getDerivative(expression, variable).toLatex());

            log.info("Calculating domain of [{}] for variable [{}]", expression, variable);
            try {
                result.setDomain(ExpressionUtils.getDomain(expression, new Variable('x')).toLatex());
            } catch (UnsupportedOperationException e) {
                log.error(e.getMessage());
                result.setDomain("[not\\ supported\\ yet]");
            }
        } else {
            log.info("No variable found, using 'x'");
            result.setDerivative(ExpressionUtils.getDerivative(expression, new Variable('x')).toLatex());
            result.setDomain(ExpressionUtils.getDomain(expression, new Variable('x')).toLatex());
        }


        if (simplifiedExpression.isScalar()) {
            log.info("Evaluating: [" + expression + "]");
            result.setNumericValue(String.valueOf(parsedExpression.getValue()));
        }

        log.info("Result [" + result + "]");
        return result;
    }
}

