package com.nemesis.mathcore.expressionsolver.equations;

import com.nemesis.mathcore.expressionsolver.ExpressionUtils;
import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.components.Variable;
import com.nemesis.mathcore.expressionsolver.intervals.model.Union;
import com.nemesis.mathcore.expressionsolver.models.Polynomial;
import com.nemesis.mathcore.expressionsolver.models.RelationalOperator;
import com.nemesis.mathcore.expressionsolver.utils.MathCoreContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Slf4j
public class QuadraticEquationResolverTest {

    @Test
    public void resolve() {

        Map<ResolutionInput, ResolutionOutput> tests = new LinkedHashMap<>();

        tests.put(new ResolutionInput("x^2+x+1", RelationalOperator.EQ),
                new ResolutionOutput("x ∈ ∅", "x \\in \\emptyset"));

        tests.put(new ResolutionInput("x^2+x+2", RelationalOperator.GT),
                new ResolutionOutput("∀ x ∈ ℝ", "\\forall x \\in \\R"));

        tests.put(new ResolutionInput("x^2+x+2", RelationalOperator.GTE),
                new ResolutionOutput("∀ x ∈ ℝ", "\\forall x \\in \\R"));

        tests.put(new ResolutionInput("x^2+x+2", RelationalOperator.LT),
                new ResolutionOutput("x ∈ ∅", "x \\in \\emptyset"));

        tests.put(new ResolutionInput("x^2+x+2", RelationalOperator.LTE),
                new ResolutionOutput("x ∈ ∅", "x \\in \\emptyset"));

        tests.put(new ResolutionInput("x^2+x-5", RelationalOperator.EQ),
                new ResolutionOutput("x = (-1-√21)/2 ∪ x = (-1+√21)/2", "x = \\frac{(-1-\\sqrt{21})}{2} \\cup x = \\frac{(-1+\\sqrt{21})}{2}"));

        tests.put(new ResolutionInput("x^2-7x+10", RelationalOperator.GT),
                new ResolutionOutput("x < 2 , x ∈ ℝ ∪ x > 5 , x ∈ ℝ", "x < 2 , x \\in \\R \\cup x > 5 , x \\in \\R"));

        tests.put(new ResolutionInput("x^2-7x+10", RelationalOperator.LTE),
                new ResolutionOutput("2 ≤ x ≤ 5 , x ∈ ℝ", "2 \\leq x \\leq 5 , x \\in \\R"));

        tests.put(new ResolutionInput("x^2+5*x+6", RelationalOperator.EQ),
                new ResolutionOutput("x = -3 ∪ x = -2", "x = -3 \\cup x = -2"));

        tests.put(new ResolutionInput("9x^2-6x+1", RelationalOperator.EQ),
                new ResolutionOutput("x = 1/3", "x = \\frac{1}{3}"));

        tests.put(new ResolutionInput("9x^2-6x+1", RelationalOperator.LTE),
                new ResolutionOutput("x = 1/3", "x = \\frac{1}{3}"));

        tests.put(new ResolutionInput("9x^2-6x+1", RelationalOperator.GT),
                new ResolutionOutput("x ≠ 1/3", "x \\neq \\frac{1}{3}"));

        // TODO: transform to quadratic functions
//        tests.put(new ResolutionInput("x-1", RelationalOperator.EQUALS), new ResolutionOutput("x = 1", "x = 1"));
//        tests.put(new ResolutionInput("x+2", RelationalOperator.EQUALS), new ResolutionOutput("x = -2", "x = -2"));
//        tests.put(new ResolutionInput("x-2", RelationalOperator.EQUALS), new ResolutionOutput("x = 2", "x = 2"));
//        tests.put(new ResolutionInput("x-2-3", RelationalOperator.EQUALS), new ResolutionOutput("x = 5", "x = 5"));
//        tests.put(new ResolutionInput("3*x-2", RelationalOperator.EQUALS), new ResolutionOutput("x = 2/3", "x = \\frac{2}{3}"));
//        tests.put(new ResolutionInput("3*x+2", RelationalOperator.EQUALS), new ResolutionOutput("x = -2/3", "x = \\frac{-2}{3}"));
//        tests.put(new ResolutionInput("3*x*y+2", RelationalOperator.EQUALS), new ResolutionOutput("x = -2/(3y)", "x = \\frac{-2}{(3y)}"));
//        tests.put(new ResolutionInput("3*y*x+2", RelationalOperator.EQUALS), new ResolutionOutput("x = -2/(3y)", "x = \\frac{-2}{(3y)}"));
//        tests.put(new ResolutionInput("y*x*3+2", RelationalOperator.EQUALS), new ResolutionOutput("x = -2/(3y)", "x = \\frac{-2}{(3y)}"));
//        tests.put(new ResolutionInput("y*x*3+2-1", RelationalOperator.EQUALS), new ResolutionOutput("x = -1/(3y)", "x = \\frac{-1}{(3y)}"));
//        tests.put(new ResolutionInput("y*x*3+2*y-1", RelationalOperator.EQUALS), new ResolutionOutput("x = (-2y+1)/(3y)", "x = \\frac{(-2y+1)}{(3y)}"));
//
//        tests.put(new ResolutionInput("x+1", RelationalOperator.GREATER_THAN), new ResolutionOutput("x > -1", "x > -1"));
//        tests.put(new ResolutionInput("-x+1", RelationalOperator.GREATER_THAN), new ResolutionOutput("x < 1", "x < 1"));
//        tests.put(new ResolutionInput("-x-1", RelationalOperator.GREATER_THAN), new ResolutionOutput("x < -1", "x < -1"));
//        tests.put(new ResolutionInput("-x-3+5", RelationalOperator.GREATER_THAN), new ResolutionOutput("x < 2", "x < 2"));
//        tests.put(new ResolutionInput("x-3+5", RelationalOperator.LESS_THAN), new ResolutionOutput("x < -2", "x < -2"));
//        tests.put(new ResolutionInput("x-3+5", RelationalOperator.LESS_THAN_OR_EQUALS), new ResolutionOutput("x ≤ -2", "x \\leq -2"));
//        tests.put(new ResolutionInput("x-3+5", RelationalOperator.GREATER_THAN_OR_EQUALS), new ResolutionOutput("x ≥ -2", "x \\geq -2"));
//        tests.put(new ResolutionInput("3*x-2", RelationalOperator.GREATER_THAN), new ResolutionOutput("x > 2/3", "x > \\frac{2}{3}"));
//        tests.put(new ResolutionInput("-3*x-2", RelationalOperator.GREATER_THAN), new ResolutionOutput("x < 2/-3", "x < \\frac{2}{-3}"));
//        tests.put(new ResolutionInput("-3*x-2", RelationalOperator.LESS_THAN), new ResolutionOutput("x > 2/-3", "x > \\frac{2}{-3}"));
//        tests.put(new ResolutionInput("-3*x+2-5", RelationalOperator.LESS_THAN_OR_EQUALS), new ResolutionOutput("x ≥ -1", "x \\geq -1"));


        MathCoreContext.setNumericMode(MathCoreContext.Mode.FRACTIONAL);

        for (ResolutionInput test : tests.keySet()) {
            log.info("");
            log.info("Testing [{} {} 0]", test.getFunction(), test.getOperator().toString());
            final Component component = ExpressionUtils.simplify(test.getFunction());
            Polynomial polynomial = Polynomial.getPolynomial(component);
            assertNotNull(polynomial);
            final Union intervals = QuadraticEquationResolver.resolve(polynomial, test.getOperator(), new Variable('x')); // TODO: test with all found variables
            assertNotNull(intervals);
            final ResolutionOutput expectedSolution = tests.get(test);
            assertEquals("Error resolving [" + test + "]", expectedSolution.getPlainString(), intervals.toString());
            assertEquals("Error resolving [" + test + "]", expectedSolution.getLatexString(), intervals.toLatex());
        }

    }


    @Data
    @AllArgsConstructor
    private static class ResolutionOutput {
        private final String plainString;
        private final String latexString;

    }

    @Data
    @AllArgsConstructor
    private class ResolutionInput {
        private final String function;
        private final RelationalOperator operator;
    }

}
