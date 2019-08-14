package com.nemesis.mathcore.expressionsolver.models;


/*

 */

import java.math.BigDecimal;

public class Number extends Factor {

    public Number(String number) {
        value = new BigDecimal(number);
    }

    public Number(BigDecimal number) {
        value = number;
    }
}
