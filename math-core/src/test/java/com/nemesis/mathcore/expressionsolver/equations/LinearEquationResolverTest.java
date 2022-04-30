package com.nemesis.mathcore.expressionsolver.equations;

import com.nemesis.mathcore.expressionsolver.ExpressionUtils;
import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.components.Variable;
import com.nemesis.mathcore.expressionsolver.intervals.model.GenericInterval;
import com.nemesis.mathcore.expressionsolver.intervals.model.Union;
import com.nemesis.mathcore.expressionsolver.models.Polynomial;
import com.nemesis.mathcore.expressionsolver.models.RelationalOperator;
import com.nemesis.mathcore.expressionsolver.utils.MathCoreContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.nemesis.mathcore.expressionsolver.models.RelationalOperator.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Slf4j
public class LinearEquationResolverTest {

    @Test
    public void resolve() {

        Map<ResolutionInput, ResolutionOutput> tests = new LinkedHashMap<>();

        tests.put(new ResolutionInput("x+1", EQ), new ResolutionOutput("x = -1", "x = -1"));
        tests.put(new ResolutionInput("x-1", EQ), new ResolutionOutput("x = 1", "x = 1"));
        tests.put(new ResolutionInput("x+2", EQ), new ResolutionOutput("x = -2", "x = -2"));
        tests.put(new ResolutionInput("x-2", EQ), new ResolutionOutput("x = 2", "x = 2"));
        tests.put(new ResolutionInput("x-2-3", EQ), new ResolutionOutput("x = 5", "x = 5"));
        tests.put(new ResolutionInput("3*x-2", EQ), new ResolutionOutput("x = 2/3", "x = \\frac{2}{3}"));
        tests.put(new ResolutionInput("3*x+2", EQ), new ResolutionOutput("x = -2/3", "x = \\frac{-2}{3}"));
        tests.put(new ResolutionInput("3*x*y+2", EQ), new ResolutionOutput("x = -2/(3y)", "x = \\frac{-2}{(3y)}"));
        tests.put(new ResolutionInput("3*y*x+2", EQ), new ResolutionOutput("x = -2/(3y)", "x = \\frac{-2}{(3y)}"));
        tests.put(new ResolutionInput("y*x*3+2", EQ), new ResolutionOutput("x = -2/(3y)", "x = \\frac{-2}{(3y)}"));
        tests.put(new ResolutionInput("y*x*3+2-1", EQ), new ResolutionOutput("x = -1/(3y)", "x = \\frac{-1}{(3y)}"));
        tests.put(new ResolutionInput("y*x*3+2*y-1", EQ), new ResolutionOutput("x = (-2y+1)/(3y)", "x = \\frac{(-2y+1)}{(3y)}"));

        tests.put(new ResolutionInput("x+1", GT), new ResolutionOutput("x > -1", "x > -1"));
        tests.put(new ResolutionInput("-x+1", GT), new ResolutionOutput("x < 1", "x < 1"));
        tests.put(new ResolutionInput("-x-1", GT), new ResolutionOutput("x < -1", "x < -1"));
        tests.put(new ResolutionInput("-x-3+5", GT), new ResolutionOutput("x < 2", "x < 2"));
        tests.put(new ResolutionInput("x-3+5", LT), new ResolutionOutput("x < -2", "x < -2"));
        tests.put(new ResolutionInput("x-3+5", LTE), new ResolutionOutput("x <= -2", "x \\leq -2"));
        tests.put(new ResolutionInput("x-3+5", GTE), new ResolutionOutput("x >= -2", "x \\geq -2"));
        tests.put(new ResolutionInput("3*x-2", GT), new ResolutionOutput("x > 2/3", "x > \\frac{2}{3}"));
        tests.put(new ResolutionInput("-3*x-2", GT), new ResolutionOutput("x < 2/-3", "x < \\frac{2}{-3}"));
        tests.put(new ResolutionInput("-3*x-2", LT), new ResolutionOutput("x > 2/-3", "x > \\frac{2}{-3}"));
        tests.put(new ResolutionInput("-3*x+2-5", LTE), new ResolutionOutput("x >= -1", "x \\geq -1"));


        MathCoreContext.setNumericMode(MathCoreContext.Mode.FRACTIONAL);

        for (ResolutionInput test : tests.keySet()) {
            System.out.println("\n");
            log.info("Testing [{} {} 0]", test.function(), test.operator().toString());
            final Component component = ExpressionUtils.simplify(test.function());
            Polynomial polynomial = Polynomial.getPolynomial(component);
            assertNotNull(polynomial);
            final Union intervals = LinearEquationResolver.resolve(polynomial, test.operator(), new Variable('x')); // TODO: test with all found variables
            assertNotNull(intervals);
            assertEquals(1, intervals.size());
            final ResolutionOutput expectedSolution = tests.get(test);
            assertEquals("Error resolving [" + test + "]", expectedSolution.plainString(), intervals.toArray(new GenericInterval[0])[0].toString());
            assertEquals("Error resolving [" + test + "]", expectedSolution.latex(), intervals.toArray(new GenericInterval[0])[0].toLatex());
        }

    }

    private static record ResolutionOutput(String plainString, String latex) {
    }

    private static record ResolutionInput(String function, RelationalOperator operator) {
    }
}