package com.nemesis.mathcore.expressionsolver.equations;

import com.nemesis.mathcore.expressionsolver.ExpressionUtils;
import com.nemesis.mathcore.expressionsolver.expression.components.Component;
import com.nemesis.mathcore.expressionsolver.expression.components.Variable;
import com.nemesis.mathcore.expressionsolver.models.GenericInterval;
import com.nemesis.mathcore.expressionsolver.models.Polynomial;
import com.nemesis.mathcore.expressionsolver.models.RelationalOperator;
import com.nemesis.mathcore.expressionsolver.utils.MathCoreContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Slf4j
public class LinearEquationResolverTest {

    @Test
    public void resolve() {

        Map<String, ResolutionOutput> tests = new LinkedHashMap<>();

        tests.put("x+1", new ResolutionOutput("x = -1", "x = -1"));
        tests.put("x-1", new ResolutionOutput("x = 1", "x = 1"));
        tests.put("x+2", new ResolutionOutput("x = -2", "x = -2"));
        tests.put("x-2", new ResolutionOutput("x = 2", "x = 2"));
        tests.put("3*x-2", new ResolutionOutput("x = 2/3", "x = \\frac{2}{3}"));
        tests.put("3*x+2", new ResolutionOutput("x = -2/3", "x = \\frac{-2}{3}"));
        tests.put("3*x*y+2", new ResolutionOutput("x = -2/(3y)", "x = \\frac{-2}{(3y)}"));
        tests.put("3*y*x+2", new ResolutionOutput("x = -2/(3y)", "x = \\frac{-2}{(3y)}"));
        tests.put("y*x*3+2", new ResolutionOutput("x = -2/(3y)", "x = \\frac{-2}{(3y)}"));


        MathCoreContext.setNumericMode(MathCoreContext.Mode.FRACTIONAL);

        for (String test : tests.keySet()) {
            log.info("Testing [{}]", test);
            final Component component = ExpressionUtils.simplify(test);
            Polynomial polynomial = Polynomial.getPolynomial(component);
            assertNotNull(polynomial);
            final Set<GenericInterval> intervals = LinearEquationResolver.resolve(polynomial, RelationalOperator.EQUALS, new Variable('x')); // TODO: test with all found variables
            assertNotNull(intervals);
            assertEquals(1, intervals.size());
            final ResolutionOutput expectedSolution = tests.get(test);
            assertEquals("Error resolving [" + test + "]", expectedSolution.getPlainString(), intervals.toArray(new GenericInterval[0])[0].toString());
            assertEquals("Error resolving [" + test + "]", expectedSolution.getLatex(), intervals.toArray(new GenericInterval[0])[0].toLatex());
        }

    }

    @Data
    @AllArgsConstructor
    private static class ResolutionOutput {
        private final String plainString;
        private final String latex;

    }
}