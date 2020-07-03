package com.nemesis.mathcore.expressionsolver.models;

import com.nemesis.mathcore.expressionsolver.expression.components.*;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.TreeSet;

import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.MINUS;

public class Polynomial {

    private TreeSet<Monomial> monomials;

    public Polynomial(TreeSet<Monomial> monomials) {
        this.monomials = monomials;
    }

    public Polynomial(Monomial monomial) {
        this.monomials = new TreeSet<>();
        monomials.add(monomial);
    }

    public Polynomial() {
        this.monomials = new TreeSet<>();
    }

    /* Polynomial Tree
        (1)     -----------------------------------EXPRESSION-----------------------------------
        (2.1)   -------------------TERM (MONOMIAL) ----------------------   NONE       null
        (2.2)   -------------------TERM (MONOMIAL) ----------------------    OP     see line (1)
    */
    public static Polynomial getPolynomial(Component component) {

        if (component instanceof ParenthesizedExpression parExpression) {
            Expression expression;
            if (parExpression.getSign() == MINUS) {
                // Remove MINUS sign, changing all signs inside parenthesis
                expression = ComponentUtils.applyConstantToExpression(parExpression.getExpression(), new Constant("-1"), TermOperator.MULTIPLY);
            } else {
                expression = parExpression.getExpression();
            }
            return getPolynomial(expression);   // Call this method again so that will be executed the "Expression" case (see below)
        }

        if (component instanceof Expression expression) {
            final Polynomial polynomial = new Polynomial();
            final Monomial monomial = Monomial.getMonomial(expression.getTerm());
            if (monomial != null) {
                polynomial.append(monomial);
                if (!expression.getOperator().equals(ExpressionOperator.NONE)) {    // see line (2.2)
                    final Polynomial otherPolynomial = getPolynomial(expression.getSubExpression());
                    if (otherPolynomial != null) {
                        polynomial.append(otherPolynomial);
                    } else {
                        return null;
                    }
                }
                return polynomial;
            }
            return null;
        }

        if (component instanceof Factor factor) {
            final Monomial monomial = Monomial.getMonomial(factor);
            if (monomial != null) {
                return new Polynomial(monomial);
            }
            return null;
        }

        if (component instanceof Term term) {
            if (term.getOperator() == TermOperator.NONE) {
                return getPolynomial(term.getFactor());    // Call this method again so that will be executed one of the other cases (see above)
            } else {
                final Monomial monomial = Monomial.getMonomial(term);
                if (monomial != null) {
                    return new Polynomial(monomial);
                }
            }
            return null;
        }

        throw new IllegalArgumentException("Unexpected type [" + component.getClass() + "]");

    }

    private void append(Monomial monomial) {
        this.monomials.add(monomial);
    }

    private void append(Polynomial polynomial) {
        this.monomials.addAll(polynomial.getMonomials());
    }

    public TreeSet<Monomial> getMonomials() {
        return monomials;
    }

    public void setMonomials(TreeSet<Monomial> monomials) {
        this.monomials = monomials;
    }

    public Integer getDegree(Variable variable) {
        return this.monomials.stream()
                .flatMap(monomial -> monomial.getLiteralPart().stream())
                .filter(exponential -> exponential.contains(variable))
                .map(Exponential::getExponent)
                .filter(ComponentUtils::isInteger)
                .map(Factor::getValue)
                .map(BigDecimal::intValueExact)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }
}
