package com.nemesis.mathcore.polynomial;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;


public class PolynomialTest {

    @Test
    public void test() {

        Polynomial a = new Polynomial(1);
        a.set(1, 50);
        a.set(0, 10);

        Polynomial b = new Polynomial(0);
        b.set(0, 5);

        Polynomial subtract = PolynomialUtils.subtract(a, b);
        Assert.assertEquals("Subtract KO on degree 1", new BigDecimal(50), subtract.get(1));
        Assert.assertEquals("Subtract KO on degree 0", new BigDecimal(5), subtract.get(0));

        Polynomial multiply = PolynomialUtils.multiply(a, b);
        Assert.assertEquals("Multiply KO on degree 1", new BigDecimal(250), multiply.get(1));
        Assert.assertEquals("Multiply KO on degree 0", new BigDecimal(50), multiply.get(0));

        PolynomialDivisionResult divide = PolynomialUtils.divide(a, b); // TODO: fix it|
        Assert.assertEquals("Divide KO on degree 1 of Q", new BigDecimal(10), divide.getQ().get(1));
        Assert.assertEquals("Divide KO on degree 0 of Q", new BigDecimal(2), divide.getQ().get(0));
        Assert.assertEquals("Divide KO on degree 0 of R", new BigDecimal(0), divide.getR().get(0));

//        System.out.println("Subtract:" + subtract);
//        System.out.println("Multiply:" + multiply);
//        System.out.println("Divide (Q):" + divide.getQ());
//        System.out.println("Divide (R):" + divide.getR());
    }
}
