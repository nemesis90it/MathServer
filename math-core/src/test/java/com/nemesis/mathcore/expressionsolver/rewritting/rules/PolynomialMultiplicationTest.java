package com.nemesis.mathcore.expressionsolver.rewritting.rules;

import com.nemesis.mathcore.expressionsolver.parser.ExpressionParser;
import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.components.Term;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PolynomialMultiplicationTest extends TestCase {

    public void test() {

        PolynomialMultiplication multiplier = new PolynomialMultiplication();
        Component result;
        Term term;
        String expression;

        expression = "8x(2x+1)";
        log.info("Testing [{}]", expression);
        term = Term.getTerm(ExpressionParser.parse(expression));
        result = multiplier.transformer().apply(term);
        assertEquals("16x^2+8x", result.toString());

        expression = "(8x-3)(x+1)";
        log.info("Testing [{}]", expression);
        term = Term.getTerm(ExpressionParser.parse(expression));
        result = multiplier.transformer().apply(term);
        assertEquals("8x^2+5x-3", result.toString());

        expression = "2(8x-3)(x+1)";
        log.info("Testing [{}]", expression);
        term = Term.getTerm(ExpressionParser.parse(expression));
        result = multiplier.transformer().apply(term);
        assertEquals("16x^2+10x-6", result.toString());

    }

}