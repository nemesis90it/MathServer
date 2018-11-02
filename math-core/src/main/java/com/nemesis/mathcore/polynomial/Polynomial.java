package com.nemesis.mathcore.polynomial;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Sebastiano Motta on 07/08/2018.
 */
public class Polynomial {

    private ArrayList<BigDecimal> coefficients;

    public Polynomial(int degree) {
        coefficients = new ArrayList<>(degree);
        for (int i = 0; i <= degree; i++) {
            coefficients.add(BigDecimal.ZERO);
        }
    }

    public BigDecimal get(int index) {
        if (index >= coefficients.size()) {
            return BigDecimal.ZERO;
        }
        return coefficients.get(index);
    }


    public int getDegree() {
        return coefficients.size() - 1;
    }

    public void set(int i, BigDecimal coefficient) {
        coefficients.set(i, coefficient);
    }

    public void set(int i, Long coefficient) {
        coefficients.set(i, BigDecimal.valueOf(coefficient));
    }

    public void set(int i, Integer coefficient) {
        coefficients.set(i, BigDecimal.valueOf(coefficient));
    }

    @Override
    public String toString() {
        List<?> clone = (List<?>) coefficients.clone();
        Collections.reverse(clone);
        return clone.toString();
    }
}
