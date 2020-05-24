package com.nemesis.mathcore.expressionsolver;

import com.nemesis.mathcore.expressionsolver.utils.MathCoreContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;


public class DerivativeCalculusTest {

    @Test
    public void testDerivative() {
        Map<DerivativeRequest, String> tests = new LinkedHashMap<>();

        tests.put(new DerivativeRequest("1", 'x'), "0");
        tests.put(new DerivativeRequest("x", 'x'), "1");
        tests.put(new DerivativeRequest("x+1", 'x'), "1");
        tests.put(new DerivativeRequest("2*x", 'x'), "2");
        tests.put(new DerivativeRequest("(2*x)*(3*x)", 'x'), "12x");
        tests.put(new DerivativeRequest("x/2", 'x'), "0.5");
        tests.put(new DerivativeRequest("(x+1)/2", 'x'), "0.5");
        tests.put(new DerivativeRequest("x+2*y", 'x'), "1");
        tests.put(new DerivativeRequest("x+2*y", 'y'), "2");
        tests.put(new DerivativeRequest("(x+1)/(2*x)", 'x'), "-0.5/x^2");
//        tests.put(new DerivativeRequest("log(x)+2*log(x)^2", 'x'), ""); // TODO
//        tests.put(new DerivativeRequest("(x+3)*(5/x)", 'x'), "5/x+(x+3)(-5/(x)^2)"); // TODO: verify
//        tests.put("((x+3)+(5/x))*2*x", "((1)+(-5/x^2))*2x+((x+3)+(5/x))*2"); // 4x+6   TODO: verify
//        tests.put("x^2", "x^2(2/x)"); // 2x
//        tests.put("x^" + Constants.NEP_NUMBER, " x^e(e/x)"); // ex^(e-1)
//        tests.put("x^(3*x)", "x^(3x)((3)*ln(x)+(3x)/x)"); // x^(3x)(3ln(x)+3)

//        this.doTestDerivative(tests);

        MathCoreContext.setNumericMode(MathCoreContext.Mode.FRACTIONAL);
        tests.put(new DerivativeRequest("x/2", 'x'), "1/2");
        tests.put(new DerivativeRequest("(x+1)/2", 'x'), "1/2");
        tests.put(new DerivativeRequest("(x+1)/(2*x)", 'x'), "-1/(2x^2)");
        tests.put(new DerivativeRequest("2*log(x)", 'x'), "2/(ln(10)x)");

//        tests.put(new DerivativeRequest("ln(x)^2", 'x'), "(2ln(x))/x");
        tests.put(new DerivativeRequest("ln(x)^2", 'x'), "(1/x)*2ln(x)"); // TODO: verify

//        tests.put(new DerivativeRequest("log(x)^2", 'x'), "((2log(x))/x)/ln(10)");
        tests.put(new DerivativeRequest("log(x)^2", 'x'), "(1/x)*1/ln(10)*2log(x)"); // TODO: verify

//        tests.put(new DerivativeRequest("2*log(x)^2", 'x'), ""); // TODO

        this.doTestDerivative(tests);
    }

    private void doTestDerivative(Map<DerivativeRequest, String> tests) {
        for (DerivativeRequest req : tests.keySet()) {
            String errorMessage = "ERROR ON FUNCTION: " + req.getFunction();
            String result = null;
            try {
                System.out.println("\nTesting D[" + req.getFunction() + ", " + req.getVar() + "]");
                long start = System.nanoTime();
                result = ExpressionUtils.getDerivative(req.getFunction(), req.getVar()).toString();
                long stop = System.nanoTime();
                System.out.println("Elapsed time: " + (stop - start) / 1000000d + " ms");
                System.out.println("D[" + req.getFunction() + ", " + req.getVar() + "] -> " + result);
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail(errorMessage);
            }
            Assert.assertEquals(errorMessage, tests.get(req), result);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class DerivativeRequest {
        private String function;
        private char var;
    }
}
