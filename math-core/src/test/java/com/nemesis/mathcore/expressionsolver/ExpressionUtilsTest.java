package com.nemesis.mathcore.expressionsolver;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ExpressionUtilsTest {

    @Test
    public void testEvaluate() {


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
        tests.put("3!^-(1+2)!", "0.00002143347050754458");
        tests.put("4!^3!", "191102976");
        tests.put("-4!^3!", "-191102976");
        tests.put("(1+2)^2^3", "6561");
        tests.put("-(1+2)^2^3", "-6561");
//        tests.put("3^(1/2)", "1.73205080757"); TODO
        tests.put("√3", "1.7320508075688772936");
        tests.put("∛3", "1.4422495703074083823");
        tests.put("∜3", "1.3160740129524924608");

        // TODO: test complex logarithms
        // TODO: test all operations with decimal numbers

        for (String expression : tests.keySet()) {
            String errorMessage = "ERROR ON EXPRESSION: " + expression;
            BigDecimal result = null;
            try {
                System.out.println("Testing [" + expression + "]");
                long start = System.nanoTime();
                result = ExpressionUtils.evaluate(expression);
                long stop = System.nanoTime();
                System.out.println("Elapsed time: " + (stop - start) / 1000 + " µs\n");

            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail(errorMessage);
            }
            Assert.assertEquals(errorMessage, tests.get(expression), result.toString());
        }

    }

    @Test
    public void testDerivative() {
        Map<String, String> tests = new LinkedHashMap<>();

        tests.put("1", "0");
        tests.put("x", "1");
        tests.put("x+1", "1");
        tests.put("2*x", "2");
        tests.put("(2*x)*(3*x)", "12x");
        tests.put("x/2", "2/2^2"); // 1/2
        tests.put("(x+1)/2", "2/2^2"); // 1/2
//        tests.put("(x+1)/(2*x)", "(2x)-(x+1)(2)/(2x)^2"); // -1/2x^2  TODO: verify
//        tests.put("(x+3)*(5/x)", "(5/x)+(x+3)(-5/x^2)"); // -1/2x^2  TODO: verify
//        tests.put("((x+3)+(5/x))*2*x", "((1)+(-5/x^2))*2x+((x+3)+(5/x))*2"); // 4x+6   TODO: verify
//        tests.put("x^2", "x^2(2/x)"); // 2x
//        tests.put("x^" + Constants.NEP_NUMBER, " x^e(e/x)"); // ex^(e-1)
//        tests.put("x^(3*x)", "x^(3x)((3)*ln(x)+(3x)/x)"); // x^(3x)(3ln(x)+3)

        for (String function : tests.keySet()) {
            String errorMessage = "ERROR ON FUNCTION: " + function;
            String result = null;
            try {
                System.out.println("\nTesting [" + function + "]");
                long start = System.nanoTime();
                result = ExpressionUtils.getDerivative(function);
                long stop = System.nanoTime();
                System.out.println("Elapsed time: " + (stop - start) / 1000 + " µs");
                System.out.println("D[" + function + "] -> " + result);
            } catch (Exception e) {
                e.printStackTrace();
//                Assert.fail(errorMessage);
            }
//            Assert.assertEquals(errorMessage, tests.get(function), result.toString());
        }
    }

    @Test
    public void testSimplify() {
        Map<String, String> tests = new LinkedHashMap<>();

        tests.put("1", "1");
        tests.put("x", "x");
//        tests.put("-(-x)", "x"); // TODO
        tests.put("-2*(-x)", "2x");
        tests.put("x+1", "x+1");
        tests.put("1+x", "x+1");
        tests.put("2*x", "2x");
        tests.put("x*2", "2x");
        tests.put("(2*x)*(3*x)", "6x^2");
        tests.put("(2*x)+(3*x)", "5x");
        tests.put("(2*x)*((2*x)+(3*x))", "10x^2");
        tests.put("(8*x)+(2*x)+(3*x)", "13x");
        tests.put("(8*y)+(2*x)+(3*x)", "5x+8y");
        tests.put("2*(8*y-x)", "-2x+16y");
        tests.put("2*(8*y+3*x)", "6x+16y");
        tests.put("-2*(8*y+3*x)", "-6x-16y");
        tests.put("2*(-(8*y+3*x))", "-6x-16y");
        tests.put("(30*x)/(15*x)", "2");
//        tests.put("(30*x^4)/(15*x)", "2x^3"); // TODO
//        tests.put("24/(2*x+6*x)", "3/x");
//        tests.put("24/(2*y+3*x)", "24/(2y+3x)");
//        tests.put("24/(2/y+3*x)", "24/(2/y+3x)");
//        tests.put("7*x+4*y-2*x+2", Arrays.asList("5x+4y+2"));
//        tests.put("7*x+4*y-2*x+2-4*y", Arrays.asList("5x+2"));
//        tests.put("-7*x+4*y-2*x+2-4*y", Arrays.asList("-9x+2"));
//        tests.put("-7*log(x)+4*y-2*log(x)+2-4*y", Arrays.asList("-9log(x)+2")); // TODO

//        tests.put("-(-log(x))", "log(x)");
//        tests.put("(2*x)+(3*x)+(8*y)", "5x+8y");


        for (String function : tests.keySet()) {
            String errorMessage = "ERROR ON FUNCTION: " + function;
            String result = null;
            try {
                System.out.println("\nTesting [" + function + "]");
                long start = System.nanoTime();
                result = ExpressionUtils.simplify(function);
                long stop = System.nanoTime();
                System.out.println("Elapsed time: " + (stop - start) / 1000 + " µs");
                System.out.println(function + " -> " + result);
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail(errorMessage);
            }
            Assert.assertEquals(errorMessage, tests.get(function), result.toString());
        }
    }
}
