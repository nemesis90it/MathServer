package com.nemesis.mathserver.mathserverboot.evaluator;

import com.nemesis.mathcore.expressionsolver.models.Domain;
import com.nemesis.mathcore.expressionsolver.utils.EquationUtils;
import com.nemesis.mathcore.expressionsolver.components.Variable;
import com.nemesis.mathcore.expressionsolver.intervals.model.Union;
import com.nemesis.mathcore.expressionsolver.models.Equation;
import com.nemesis.mathserver.mathserverboot.model.EvaluationResult;
import lombok.extern.slf4j.Slf4j;

/* TODO:  implements utility for Equation:
       simplify, derivative, domain (?), numericValue (?)

    TODO: test x^2-x-1≤0
 */

@Slf4j
public class EquationEvaluator {
    public static EvaluationResult evaluate(Equation equation, Variable variable) {
        EvaluationResult result = new EvaluationResult();

        log.info("Trying to resolve equation [{}] for variable [{}]", equation, variable);

        Union roots;
        Domain domain;
        Equation simplified;

        try {
            domain = equation.getLeftComponent().getDomain(variable);
            result.setDomain(domain.toLatex());
        } catch (UnsupportedOperationException e) {
            log.error(e.getMessage());
            result.setDomain("[not\\ supported\\ yet]");
        }

        try {
            roots = EquationUtils.resolve(equation, variable);
            result.setRoots(roots.toLatex());
        } catch (UnsupportedOperationException e) {
            log.error(e.getMessage());
            result.setDomain("[not\\ supported\\ yet]");
        }

        simplified = EquationUtils.simplify(equation, variable);

        result.setInput(equation.toLatex());
        result.setSimplifiedForm(simplified.toLatex());

        return result;
    }
}
