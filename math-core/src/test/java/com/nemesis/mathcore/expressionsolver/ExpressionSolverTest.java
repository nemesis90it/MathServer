package com.nemesis.mathcore.expressionsolver;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;


public class ExpressionSolverTest {

    @Test
    public void test() {


        Map<String, String> tests = new LinkedHashMap<>();
        tests.put("2*4+7", "15");
        tests.put("2*6/3+7", "11");
        tests.put("1", "1");
        tests.put("-1", "-1");
        tests.put("(-1)", "-1");
        tests.put("-(2+1)", "-3");
        tests.put("-(2+1)+5", "2");
        tests.put("-(2+1)-5", "-8");
        tests.put("-(2+1)-(5+1)", "-9");
        tests.put("-(2+6)/4", "-2");
        tests.put("1+2", "3");
        tests.put("-1+2", "1");
        tests.put("1/-2", "-0.5");
        tests.put("-1/2", "-0.5");
        tests.put("1/2", "0.5");
        tests.put("6/2", "3");
        tests.put("(1)", "1");
        tests.put("(1+2)", "3");
        tests.put("(1/2)", "0.5");
        tests.put("(6/2)", "3");
        tests.put("(3+2)!", "120");
        tests.put("120/3!", "20");
        tests.put("5!/5", "24");
        tests.put("2.3+5.1", "7.4");
        tests.put("(2*4+7)/((30+2*1)-29)-1", "4");
        tests.put("(2*4+7)/((30+3!*2)-70)-1", "-1.53571428571428571429");
        tests.put("3^2", "9");
        tests.put("3^2+1", "10");
        tests.put("3^2*5", "45");
        tests.put("3^2*(5+1)", "54");
        tests.put("3^2*4^5", "9216");
        tests.put("3^(2*4)", "6561");
        tests.put("3^(2*4)+1", "6562");
        tests.put("3^(2*4)/3", "2187");
        tests.put("2+(8^2)!", "126886932185884164100000000000000000000000000000000000000000000000000000000000000000000002");
//        tests.put("3^(2*4)/(3+1)", "1640.25"); // TODO: FIX!
//        tests.put("2^3^2", "512");


//        tests.put("((2*4+7)/((30+2*1)-29)-1)^2", "16");
//        tests.put("3^-2", "0.1111111111");

        for (String expression : tests.keySet()) {
            BigDecimal result = ExpressionSolver.evaluate(expression);
            String errorMessage = "ERROR ON EMPRESSION: " + expression;
            Assert.assertEquals(errorMessage, tests.get(expression), result.toString());
        }

    }
}
