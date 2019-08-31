package com.nemesis.mathcore.expressionsolver;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;


public class ExpressionParserTest {

    @Test
    public void test() {


        Map<String, String> tests = new LinkedHashMap<>();

        tests.put("1", "1");
        tests.put("-1", "-1");
        tests.put("1+2", "3");
        tests.put("-1+2", "1");
        tests.put("2*4+7", "15");
        tests.put("2*6/3+7", "11");
        tests.put("(-1)", "-1");
        tests.put("-(2+1)", "-3");
        tests.put("-(2+1)+5", "2");
        tests.put("-(2+1)-5", "-8");
        tests.put("-(2+1)-(5+1)", "-9");
        tests.put("-(2+6)/4", "-2");
        tests.put("1/-2", "-0.5");
        tests.put("-1/2", "-0.5");
        tests.put("1/2", "0.5");
        tests.put("6/2", "3");
        tests.put("(1)", "1");
        tests.put("(1+2)", "3");
        tests.put("(1/2)", "0.5");
        tests.put("(6/2)", "3");
        tests.put("3!", "6");
        tests.put("-3!", "-6");
        tests.put("3!+(2+2)!", "30");
        tests.put("(3+1)!+(2+2)!", "48");
        tests.put("(3+2)!", "120");
        tests.put("-(3+2)!", "-120");
        tests.put("(3+2)!+1", "121");
        tests.put("(1+2)!!", "720");
        tests.put("(1+2)!!+1", "721");
        tests.put("3!!", "720");
        tests.put("-3!!", "-720");
        tests.put("3!!+1", "721");
        tests.put("120/3!", "20");
        tests.put("5!/5", "24");
        tests.put("2.3+5.1", "7.4");
        tests.put("(2*4+7)/((30+2*1)-29)-1", "4");
        tests.put("(2*4+7)/((30+3!*2)-70)-1", "-1.53571428571428571429");
        tests.put("3^2", "9");
        tests.put("-3^2", "-9");
        tests.put("3^2+1", "10");
        tests.put("3^2*5", "45");
        tests.put("3^2*(5+1)", "54");
        tests.put("3^2*4^5", "9216");
        tests.put("2^-3", "0.125");
        tests.put("2^(-3)", "0.125");
        tests.put("(2+4^3)^5", "1252332576");
        tests.put("(2+4^3)^5+1", "1252332577");
        tests.put("(2+4^3)^5/2", "626166288");
        tests.put("(2+4^3)+(3+5)^5/2", "16450");
        tests.put("3^(2*4)", "6561");
        tests.put("3^(2*4)+1", "6562");
        tests.put("3^(2*4)/3", "2187");
        tests.put("3^(2*4)/(3+1)", "1640.25");
        tests.put("(1+2)^(8/4+1)", "27");
        tests.put("(1+2)^(8/4+1)+1", "28");
        tests.put("(1+2)^(8/4+1)/3", "9");
        tests.put("(1+(6/3)+2)^(8/4+1)", "125");
        tests.put("((6/3)^(1+2))^(2+1)", "512");
        tests.put("4^3^2", "262144");
        tests.put("4^-3^2", "0.000003814697265625");
        tests.put("-2^3^2", "-512");
        tests.put("-2^-3^2", "-0.001953125");
        tests.put("2^3^(2+1)", "134217728");
        tests.put("2^3^(2+1)+2^3", "134217736");
        tests.put("2^3^(2+1)/(2^3)", "16777216");
        tests.put("2^3^(2!+1)/(2^3)!", "3328.81269841269841269841");
        tests.put("2^((3*2)-(1+2))", "8");
        tests.put("((1+2)+(3+4))^3", "1000");
        tests.put("((1+2)+(3+4))^3+1", "1001");
        tests.put("((4+2)/2+(3+4))^3+1", "1001");
        tests.put("((2+2)+(8+4)/2)^3", "1000");
        tests.put("((2+2)+(8+4)/2)^3+1", "1001");
        tests.put("log(100)", "2");
        tests.put("log(1000)!", "6");
        tests.put("-log(1000)!", "-6");
        tests.put("-log(100)", "-2");
        tests.put("ln(10)", "2.302585092994046");
        tests.put("1+log(100)", "3");
        tests.put("1+ln(10)", "3.302585092994046");
        tests.put("log(100)+1", "3");
        tests.put("ln(10)+1", "3.302585092994046");
        tests.put("2*log(100)+1", "5");
        tests.put("-2*log(100)+1", "-3");
        tests.put("(log(100))^3", "8");
        tests.put("-(log(100))^3", "-8");
        tests.put("ⅇ", "2.718281828459045090795598298427648842334747314453125");
        tests.put("π", "3.141592653589793115997963468544185161590576171875");
        tests.put("ln(ⅇ)", "1");
        tests.put("log(100)^3", "8");
        tests.put("log(100)^(1+2)", "8");
        tests.put("log(100)^log(1000)", "8");
        tests.put("log(100)^log(1000)^2", "512");
        tests.put("3^log(1000)", "27");
        tests.put("3^4!", "282429536481");
        tests.put("(1+2)^log(1000)", "27");
        tests.put("(1+2)^3!", "729");
        tests.put("3!^(1+2)!", "46656");
        tests.put("4!^3!", "191102976");
        tests.put("(1+2)^2^3", "6561");
        // TODO: test complex logarithms
        // TODO: test all operations with decimal numbers

        for (String expression : tests.keySet()) {
            String errorMessage = "ERROR ON EXPRESSION: " + expression;
            BigDecimal result = null;
            try {
                System.out.println("Testing [" + expression + "]");
                result = ExpressionParser.evaluate(expression);
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail(errorMessage);
            }
            Assert.assertEquals(errorMessage, tests.get(expression), result.toString());
        }

    }
}
