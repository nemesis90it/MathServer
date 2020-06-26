package com.nemesis.mathcore.expressionsolver.models;

import com.nemesis.mathcore.expressionsolver.ExpressionParser;
import com.nemesis.mathcore.expressionsolver.expression.components.Expression;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

public class PolynomialTest extends TestCase {

    public void testGetPolynomial() {

        Map<String, Boolean> tests = new HashMap<>();
//        tests.put("x^4+y^2*x^5+2+z", true);
//        tests.put("-x^4+y^2*x^5+2+z", true);
        tests.put("-x^4+x^5+2+z/2", true);
//        tests.put("-x^4+y^2/x^5+2+z", false);

        for (Map.Entry<String, Boolean> test : tests.entrySet()) {
            final String expression = test.getKey();
            final Expression parsed = ExpressionParser.parse(expression);
            final Polynomial polynomial = Polynomial.getPolynomial(parsed);
            assertEquals("Testing [" + expression + "]", (Boolean) !polynomial.getMonomials().isEmpty(), test.getValue());
        }

    }
}