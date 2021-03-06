package com.nemesis.mathcore.expressionsolver;

import com.nemesis.mathcore.expressionsolver.components.Variable;
import com.nemesis.mathcore.expressionsolver.utils.Constants;
import com.nemesis.mathcore.expressionsolver.utils.MathCoreContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.nemesis.mathcore.expressionsolver.utils.Constants.E_CHAR;
import static java.lang.Math.E;

// TODO: test latex output

/*
    Example:  D[x^(3*x)^ln(x), x] = x^{(3x)^{ln(x)}}(((3x)^{ln(x)}((\frac{1}{x})ln(3x)+\frac{ln(x)}{x}))ln(x)+\frac{(3x)^{ln(x)}}{x})
 */


public class DerivativeCalculusTest {

    @Test
    public void testDerivative() {
        Map<DerivativeRequest, String> tests = new LinkedHashMap<>();

        tests.put(new DerivativeRequest("1", 'x'), "0");
        tests.put(new DerivativeRequest("x", 'x'), "1");
        tests.put(new DerivativeRequest("x+1", 'x'), "1");
        tests.put(new DerivativeRequest("2x", 'x'), "2");
        tests.put(new DerivativeRequest("(2x)(3x)", 'x'), "12x");
        tests.put(new DerivativeRequest("x/2", 'x'), "0.5");
        tests.put(new DerivativeRequest("(x+1)/2", 'x'), "0.5");
        tests.put(new DerivativeRequest("x+2y", 'x'), "1");
        tests.put(new DerivativeRequest("x+2y", 'y'), "2");
        tests.put(new DerivativeRequest("(x+1)/(2x)", 'x'), "-0.5/x^2");
        tests.put(new DerivativeRequest("x^2", 'x'), "2x");
        tests.put(new DerivativeRequest("x^x", 'x'), "x^x(ln(x)+1)");
        tests.put(new DerivativeRequest("x^" + E, 'x'), E + "x^1.718281828459045"); // ex^(e-1)
        tests.put(new DerivativeRequest("x^(3x)", 'x'), "x^(3x)(3ln(x)+3)");
////        tests.put(new DerivativeRequest("log(10,x)+2log(10,x)^2", 'x'), ""); // TODO
////        tests.put(new DerivativeRequest("(x+3)(5/x)", 'x'), "5/x+(x+3)(-5/(x)^2)"); // TODO: verify
////        tests.put("((x+3)+(5/x))2x", "((1)+(-5/x^2))2x+((x+3)+(5/x))2"); // 4x+6   TODO: verify

        this.doTestDerivative(tests);

        tests.remove(new DerivativeRequest("x^" + Constants.NEP_NUMBER, 'x'));

        MathCoreContext.setNumericMode(MathCoreContext.Mode.FRACTIONAL);

        tests.put(new DerivativeRequest("x/2", 'x'), "1/2");
        tests.put(new DerivativeRequest("(x+1)/2", 'x'), "1/2");
        tests.put(new DerivativeRequest("(x+1)/(2x)", 'x'), "-1/(2x^2)");
        tests.put(new DerivativeRequest("2log(10,x)", 'x'), "2/(ln(10)x)");
        tests.put(new DerivativeRequest("x^" + E_CHAR, 'x'), "(" + E_CHAR + "x^" + E_CHAR + ")/x"); // ex^(e-1)
        tests.put(new DerivativeRequest("log(10,x)^2", 'x'), "(1/ln(10))2log(10,x)/x");
        tests.put(new DerivativeRequest("x^2log(10,x)^2", 'x'), "2xlog(10,x)^2+(2/ln(10))xlog(10,x)");
//        tests.put(new DerivativeRequest("x^2ln(x)^2", 'x'), ""); // TODO

//        tests.put(new DerivativeRequest("ln(x)^2", 'x'), "(2ln(x))/x"); // Alternative
//        tests.put(new DerivativeRequest("ln(x)^2", 'x'), "(1/x)2ln(x)"); // TODO: verify

//        tests.put(new DerivativeRequest("log(10,x)^2", 'x'), "((2log(10,x))/x)/ln(10)"); // Alternative
//        tests.put(new DerivativeRequest("log(10,x)^2", 'x'), "(1/x)2log(10,x)1/ln(10)"); // Alternative

////        tests.put(new DerivativeRequest("2log(10,x)^2", 'x'), ""); // TODO
////        tests.put(new DerivativeRequest("(-y)^-671/628", 'y'), ""); // TODO

        this.doTestDerivative(tests);
    }

    private void doTestDerivative(Map<DerivativeRequest, String> tests) {
        tests.keySet().forEach(req -> {
            String errorMessage = "ERROR ON FUNCTION: " + req.getFunction();
            String result = null;
            try {
                System.out.println("\nTesting D[" + req.getFunction() + ", " + req.getVar() + "]");
                long start = System.nanoTime();
                result = ExpressionUtils.getDerivative(req.getFunction(), new Variable(req.getVar())).toString();
                long stop = System.nanoTime();
                System.out.println("Elapsed time: " + (stop - start) / 1000000d + " ms");
                System.out.println("D[" + req.getFunction() + ", " + req.getVar() + "] -> " + result);
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail(errorMessage);
            }
            Assert.assertEquals(errorMessage, tests.get(req), result);
        });
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class DerivativeRequest {
        private String function;
        private char var;
    }
}
