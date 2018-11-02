package com.nemesis.mathcore.expressionsolver.models;


/*

 */

import java.math.BigDecimal;

public class Number extends Factor {

    public Number(String expression) {
        value = new BigDecimal(expression);
    }

    public Number(BigDecimal number) {
        value = number;
    }
}
