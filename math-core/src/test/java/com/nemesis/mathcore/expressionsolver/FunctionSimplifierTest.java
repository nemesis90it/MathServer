package com.nemesis.mathcore.expressionsolver;

import com.nemesis.mathcore.expressionsolver.utils.Constants;
import com.nemesis.mathcore.expressionsolver.utils.MathCoreContext;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;


public class FunctionSimplifierTest {

    @Test
    public void testSimplify() {

        Map<String, String> tests = new LinkedHashMap<>();

//        tests.put("1", "1");
//        tests.put("1/2", "0.5");
//        tests.put("x", "x");
//        tests.put("-(-x)", "x");
//        tests.put("-2*(-x)", "2x");
//        tests.put("x+1", "x+1");
//        tests.put("1+x", "x+1");
//        tests.put("2*x", "2x");
//        tests.put("x*2", "2x");
//        tests.put("(2*x)*(3*x)", "6x^2");
//        tests.put("(2*x)+(3*x)", "5x");
//        tests.put("(2*x)-(3*x)", "-x");
//        tests.put("2*x*(2*x+3*x)", "10x^2");
//        tests.put("(2*x)*((2*x)+(3*x))", "10x^2");
//        tests.put("(8*x)+(2*x)+(3*x)", "13x");
//        tests.put("(8*x)*(2*x)+1+(3*x)", "16x^2+3x+1");
        tests.put("(8*x)*(2*x+1)+1+(3*x)", "16x^2+11x+1");
        tests.put("(8*y)+(2*x)+(3*x)", "5x+8y");
        tests.put("2*(8*y-x)", "-2x+16y");
        tests.put("2*(8*y+3*x)", "6x+16y");
        tests.put("2*(8*y-3*x)", "-6x+16y");
        tests.put("-2*(8*y+3*x)", "-6x-16y");
        tests.put("2*(-(8*y+3*x))", "-6x-16y");
        tests.put("(30*x)/(15*x)", "2");
        tests.put("-(-log(x))", "log(x)");
        tests.put("-(1-log(x))", "log(x)-1");
        tests.put("-log(x)+2*log(x)", "log(x)");
        tests.put("-log(x)^2+2*log(x)", "-log(x)^2+2log(x)");
        tests.put("log(x)+2*log(x)^2", "2log(x)^2+log(x)");
        tests.put("(30*log(x))/(15*log(x))", "2");
        tests.put("(30*log(x)^2)/(15*log(x))", "2log(x)");
        tests.put("24/(2*x)", "12/x");
        tests.put("24/(2*x+6*x)", "3/x");
        tests.put("(30*x^4)/(15*x)", "2x^3");
        tests.put("24/(2*y+3*x)", "24/(3x+2y)");
        tests.put("24/(2/y+3*x)", "24/(3x+2/y)");
        tests.put("-24/(2/y+3*x)", "-24/(3x+2/y)");
        tests.put("-24/-(2/y+3*x)", "24/(3x+2/y)");
        tests.put("4*x/x^2", "4/x");
        tests.put("4*(2/y)", "8/y");
        tests.put("-4*-(2/y+3*x)", "12x+8/y");
        tests.put("7*x+4*y-2*x+2", "5x+4y+2");
        tests.put("7*x+4*y-2*x+2-4*y", "5x+2");
        tests.put("-7*x+4*y-2*x+2-(4*y)", "-9x+2");
        tests.put("-7*log(x)+4*y-2*log(x)+2-4*y", "-9log(x)+2");
        tests.put("-7*log(x)+4*y-2*log(x)+2-5*y+3*x", "-9log(x)+3x-y+2");
        tests.put("-7*log(x)+4*y-2*log(x)+2-4*y+3*x", "-9log(x)+3x+2");
        tests.put("(2*x)+(3*x)+(8*y)", "5x+8y");
        tests.put("-2*x^4+3*x^7+8*y", "3x^7-2x^4+8y");
        tests.put("-(2*x^4)+(3*x^7)", "3x^7-2x^4");
        tests.put("-(2*x^4)-(3*x^7)", "-3x^7-2x^4");
        tests.put("(x^2)^4", "x^8");
        tests.put("x^1", "x");
        tests.put("x^0", "1");
        tests.put("x+3!-log(x)", "-log(x)+x+6");
        tests.put("x+(2+1)!-log(x)", "-log(x)+x+6");
        tests.put("x+(2+1)!-log(100)", "x+4");
        tests.put("x+(2+1)!-log(100)^3", "x-2");
        tests.put("log(x^y)", "ylog(x)");
        tests.put("ln(" + Constants.E_CHAR + ")", "1");
        tests.put("log(10)", "1");
        tests.put("log(1)", "0");
        tests.put("log(x/x)", "0");
        tests.put("8*log(y)/(2*log(y))", "4");
        tests.put("8*x+8*log(y)/(2*log(y))", "8x+4");

        MathCoreContext.setNumericMode(MathCoreContext.Mode.DECIMAL);
        this.doTestSimplify(tests);

        MathCoreContext.setNumericMode(MathCoreContext.Mode.FRACTIONAL);
        tests.put("1/2", "1/2");
        tests.put("2/4", "1/2");
        tests.put("4/2", "2");
        tests.put("4*x/2*x", "2");
////        tests.put("4*x/(2*y)", "???");
        this.doTestSimplify(tests);

    }

    private void doTestSimplify(Map<String, String> tests) {
        for (String function : tests.keySet()) {
            String errorMessage = "ERROR ON FUNCTION: " + function;
            String result = null;
            try {
                System.out.println("\nTesting [" + function + "]");
                long start = System.nanoTime();
                result = ExpressionUtils.simplify(function).toString();
                long stop = System.nanoTime();
                System.out.println("Elapsed time: " + (stop - start) / 1000000d + " ms");
                System.out.println(function + " -> " + result);
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail(errorMessage);
            }
            Assert.assertEquals(errorMessage, tests.get(function), result.toString());
        }
    }

}
