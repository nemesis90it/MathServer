package com.nemesis.mathcore.polynomial;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Sebastiano Motta on 07/08/2018.
 */
public class PolynomialUtils {

    public static PolynomialDivisionResult divide(Polynomial a, Polynomial b) {

        if (b.getDegree() > a.getDegree()) {
            throw new IllegalArgumentException("Degree of B(x) must be less or equal than degree of A(x)");
        }

        int degree_a = a.getDegree();
        int degree_b = b.getDegree();

        Polynomial quotient = new Polynomial(degree_a - degree_b + 1);
        Polynomial rest;

        int pos_b = degree_b - 1;
        int pos_a = degree_a - 1;

        do {
            BigDecimal coefficient_b = b.get(pos_b);
            BigDecimal coefficient_a = a.get(pos_a);

            int pos_q = pos_a - pos_b;

            quotient.set(pos_q, coefficient_a.divide(coefficient_b, RoundingMode.HALF_EVEN));

            Polynomial temp = PolynomialUtils.multiply(quotient, b);
            rest = PolynomialUtils.subtract(a, temp);

            int degree_rest = rest.getDegree();

            if (degree_rest < degree_b) {
                break;
            }

            a = rest;

            pos_b--;
            pos_a--;

        } while (pos_b >= 0);

        return new PolynomialDivisionResult(quotient, rest);
    }

    public static Polynomial subtract(Polynomial a, Polynomial b) {

        int maxDegree = (a.getDegree() > b.getDegree() ? a : b).getDegree();

        Polynomial result = new Polynomial(maxDegree);

        for (int pos = maxDegree; pos >= 0; pos--) {
            result.set(pos, a.get(pos).subtract(b.get(pos)));
        }

        return result;
    }

    public static Polynomial multiply(Polynomial a, Polynomial b) {

        Polynomial result = new Polynomial(a.getDegree() + b.getDegree());

        for (int i = 0; i <= a.getDegree(); i++) {
            for (int j = 0; j <= b.getDegree(); j++) {
                result.set(i + j, result.get(i + j).add(a.get(i).multiply(b.get(j))));
            }
        }
        return result;
    }

}
