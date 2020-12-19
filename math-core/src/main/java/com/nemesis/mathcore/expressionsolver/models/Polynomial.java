package com.nemesis.mathcore.expressionsolver.models;

import com.nemesis.mathcore.expressionsolver.components.*;
import com.nemesis.mathcore.expressionsolver.exception.IncompatibleMonomialsException;
import com.nemesis.mathcore.expressionsolver.exception.UnexpectedComponentTypeException;
import com.nemesis.mathcore.expressionsolver.monomial.Monomial;
import com.nemesis.mathcore.expressionsolver.monomial.MonomialOperations;
import com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.stringbuilder.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;

import static com.nemesis.mathcore.expressionsolver.operators.Sign.MINUS;

@Slf4j
public class Polynomial {

    public static final Polynomial IDENTITY_ELEMENT = new Polynomial(Monomial.getMonomial(new Constant(1)));

    private SortedSet<Monomial> monomials;

    public Polynomial(SortedSet<Monomial> monomials) {
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
            return getPolynomial(parExpression);
        }
        if (component instanceof Expression expression) {
            return getPolynomial(expression);
        }
        if (component instanceof Factor factor) {
            return getPolynomial(factor);
        }
        if (component instanceof Term term) {
            return getPolynomial(term);
        }
        throw new UnexpectedComponentTypeException("Unexpected type [" + component.getClass() + "]");
    }

    private static Polynomial getPolynomial(Term term) {
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

    private static Polynomial getPolynomial(Factor factor) {
        final Monomial monomial = Monomial.getMonomial(factor);
        if (monomial != null) {
            return new Polynomial(monomial);
        }
        return null;
    }

    private static Polynomial getPolynomial(Expression expression) {
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

    private static Polynomial getPolynomial(ParenthesizedExpression parExpression) {
        Expression expression;
        if (parExpression.getSign() == MINUS) {
            // Remove MINUS sign, changing all signs inside parenthesis
            expression = ComponentUtils.applyConstantToExpression(parExpression.getExpression(), new Constant("-1"), TermOperator.MULTIPLY);
        } else {
            expression = parExpression.getExpression();
        }
        return getPolynomial(expression);   // Call this method again so that will be executed the "Expression" case (see below)
    }


    private void append(Monomial monomial) {
        this.monomials.add(monomial);
    }

    private void append(Polynomial polynomial) {
        this.monomials.addAll(polynomial.getMonomials());
    }

    public SortedSet<Monomial> getMonomials() {
        return monomials;
    }

    public void setMonomials(SortedSet<Monomial> monomials) {
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

    public Polynomial multiply(Polynomial other) {

        final SortedSet<Monomial> thisMonomials = this.getMonomials();
        final SortedSet<Monomial> otherMonomials = other.getMonomials();

        final List<Monomial> monomialProducts = new ArrayList<>();

        for (Monomial thisMonomial : thisMonomials) {
            for (Monomial otherMonomial : otherMonomials) {
                if (Objects.equals(thisMonomial.getLiteralPart().getClassifier(), otherMonomial.getLiteralPart().getClassifier())) {
                    final Monomial monomialProduct = Monomial.getMonomial(MonomialOperations.multiply(thisMonomial, otherMonomial));
                    if (monomialProduct != null) {
                        monomialProducts.add(monomialProduct);
                    } else {
                        throw new RuntimeException("Product of two monomials cannot be null");
                    }
                } else {
                    throw new IncompatibleMonomialsException();
                }
            }
        }
        final Polynomial polynomial = new Polynomial(new TreeSet<>(ComponentUtils.sumSimilarMonomials(monomialProducts)));
        log.debug("Multiplied [{}] and [{}] with result [{}]", this.toString(), other.toString(), polynomial.toString());
        return polynomial;
    }

    @Override
    public String toString() {
        return monomials.stream().map(Monomial::toString).reduce("", ExpressionBuilder::sum);
    }
}
