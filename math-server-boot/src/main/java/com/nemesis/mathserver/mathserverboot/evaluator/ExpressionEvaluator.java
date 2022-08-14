package com.nemesis.mathserver.mathserverboot.evaluator;

import com.nemesis.mathcore.expressionsolver.utils.ExpressionUtils;
import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.components.Expression;
import com.nemesis.mathcore.expressionsolver.components.Variable;
import com.nemesis.mathserver.mathserverboot.model.EvaluationResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExpressionEvaluator {
    public static EvaluationResult evaluate(Expression expression, Variable variable) {

        final EvaluationResult result = new EvaluationResult();

        log.info("Simplifying expression [" + expression + "]");
        final Component simplifiedExpression = ExpressionUtils.simplify(expression);
        result.setSimplifiedForm(simplifiedExpression.toLatex());

        if (variable != null) {
            log.info("Evaluating derivative of [{}] for variable [{}]", expression, variable);
            result.setDerivative(ExpressionUtils.getDerivative(expression, variable).toLatex());

            log.info("Calculating domain of [{}] for variable [{}]", expression, variable);
            try {
                result.setDomain(ExpressionUtils.getDomain(expression, variable).toLatex());
            } catch (UnsupportedOperationException e) {
                log.error(e.getMessage(),e);
                result.setDomain("[not\\ supported\\ yet]");
            }
        } else {
            log.info("No variable found");
        }

        if (simplifiedExpression.isScalar()) {
            log.info("Evaluating: [" + expression + "]");
            result.setNumericValue(String.valueOf(simplifiedExpression.getValue()));
        }

        log.info("Result [" + result + "]");
        return result;

    }
}
