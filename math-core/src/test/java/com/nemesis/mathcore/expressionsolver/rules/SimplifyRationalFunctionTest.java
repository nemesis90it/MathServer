package com.nemesis.mathcore.expressionsolver.rules;


import com.nemesis.mathcore.expressionsolver.ExpressionParser;
import com.nemesis.mathcore.expressionsolver.expression.components.Component;
import com.nemesis.mathcore.expressionsolver.expression.components.Expression;
import com.nemesis.mathcore.expressionsolver.expression.components.Term;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.rewritting.rules.SimplifyRationalFunction;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class SimplifyRationalFunctionTest {

    @Test
    public void simplify() {
        Rule rule = new SimplifyRationalFunction();

        Map<String, String> tests = new LinkedHashMap<>();
        tests.put("(2*1/ln(10))/x", "(2*1/ln(10))/x");
        tests.put("(log(x)*x)/log(x)^2", "x/log(x)");
        tests.put("(log(x)*(x+1))/(x+1)*log(x)^2", "1/log(x)");

        tests.forEach((input, expectedOutput) -> {
            final Component actualOutput = rule.applyTo(ExpressionParser.parse(input));
            log.info("Testing [{}]", input);
            final Term expected = parseToTerm(expectedOutput);
            final Term actual = Term.getTerm(actualOutput);
            Assert.assertEquals(expected, actual);
        });

    }

    private static Term parseToTerm(String expression) {
        final Expression parsed = ExpressionParser.parse(expression);
        return Term.getTerm(parsed);
    }
}
