package com.nemesis.mathcore.expressionsolver.models;

import com.nemesis.mathcore.expressionsolver.parser.ExpressionParser;
import com.nemesis.mathcore.expressionsolver.components.Expression;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

public class PolynomialTest extends TestCase {

    public void testGetPolynomial() {

        Map<String, Boolean> tests = new HashMap<>();
        tests.put("x^2", true);
        tests.put("ln(x)", true);
        tests.put("x^4+y^2*x^5+2+z", true);
        tests.put("-x^4+y^2*x^5+2+z", true);
        tests.put("-x^4+y^2/x^5+2+z", false);
        tests.put("-x^4+x^5+2+z/2", true);

        for (Map.Entry<String, Boolean> test : tests.entrySet()) {
            final String expression = test.getKey();
            final Expression parsed = ExpressionParser.parse(expression);
            final Polynomial polynomial = Polynomial.getPolynomial(parsed);
            assertEquals("Testing [" + expression + "]", test.getValue(), (Boolean) (polynomial != null));
        }

    }
}