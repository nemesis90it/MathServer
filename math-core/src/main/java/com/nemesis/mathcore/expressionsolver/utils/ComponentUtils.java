package com.nemesis.mathcore.expressionsolver.utils;


import com.nemesis.mathcore.expressionsolver.ExpressionUtils;
import com.nemesis.mathcore.expressionsolver.expression.components.*;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.models.Monomial;
import com.nemesis.mathcore.expressionsolver.models.Monomial.LiteralPart;
import com.nemesis.mathcore.utils.MathUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.NONE;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.MULTIPLY;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;

public class ComponentUtils {

    public static Pair<Set<? extends Factor>, Set<? extends Factor>> simplifyExponentialSets(Set<Exponential> numeratorExponentialSet, Set<Exponential> denominatorExponentialSet) {

        final LiteralPart clonedNumerator = new LiteralPart(numeratorExponentialSet);
        final LiteralPart clonedDenominator = new LiteralPart(denominatorExponentialSet);

        final Set<Factor> newNumeratorFactors = new TreeSet<>();
        final Set<Factor> newDenominatorFactors = new TreeSet<>();

        for (Iterator<Exponential> numeratorIterator = clonedNumerator.iterator(); numeratorIterator.hasNext(); ) {
            Exponential numeratorFactor = numeratorIterator.next();
            for (Iterator<Exponential> denominatorIterator = clonedDenominator.iterator(); denominatorIterator.hasNext(); ) {
                Exponential denominatorFactor = denominatorIterator.next();
                // The factors in 'denominatorFactor' (and in 'numeratorFactor') will have all different classifier with each others,
                // then the following condition will be true at most one time for each couple of similar factors (ie with same classifier)
                if (Objects.equals(numeratorFactor.classifier(), denominatorFactor.classifier())) {
                    Factor quotient = simplifySimilarExponential(numeratorFactor, denominatorFactor);
                    if (quotient != null) {
                        if (quotient instanceof Exponential exp && !isPositive(exp.getExponent())) {
                            // Exponent is negative then change its exponent sign (make it PLUS) and add to the new denominators set
                            exp.setExponent(ComponentUtils.cloneAndChangeSign(exp.getExponent()));
                            newDenominatorFactors.add(exp);
                            numeratorIterator.remove(); // Already simplified, disappeared
                            denominatorIterator.remove(); // Already simplified, moved to newDenominatorFactors
                        } else {
                            newNumeratorFactors.add(quotient);
                            numeratorIterator.remove(); // Already simplified, moved to newNumeratorFactors
                            denominatorIterator.remove(); // Already simplified, disappeared
                        }
                    } else { // Current factors are similar (have same classifier) but no simplification can be applied (for some unknown reason...)
                        newNumeratorFactors.add(numeratorFactor);
                        newDenominatorFactors.add(denominatorFactor);
                    }
                }
            }
        }

        if (newNumeratorFactors.isEmpty() && newDenominatorFactors.isEmpty()) { // No simplification was possible, returning original elements
            return Pair.of(numeratorExponentialSet, denominatorExponentialSet);
        }

        // Add factors that could not be simplified, due are no elements left to attempt simplification
        newNumeratorFactors.addAll(clonedNumerator);
        newDenominatorFactors.addAll(clonedDenominator);

        return Pair.of(newNumeratorFactors, newDenominatorFactors);
    }

    private static Factor simplifySimilarExponential(Exponential numerator, Exponential denominator) {

        final Factor numeratorExponent = numerator.getExponent();
        final Factor denominatorExponent = denominator.getExponent();

        if (isInteger(numeratorExponent) && isInteger(denominatorExponent)) {

            Sign newSign = numerator.getSign().equals(denominator.getSign()) ? Sign.PLUS : MINUS;
            BigDecimal newExponent = numeratorExponent.getValue().subtract(denominatorExponent.getValue());

            if (newExponent.compareTo(ONE) == 0) {
                final Base base = numerator.getBase();
                base.setSign(newSign);
                return base;
            }
            if (newExponent.compareTo(ZERO) == 0) {
                return new Constant(1);
            } else {
                return new Exponential(newSign, numerator.getBase(), new Constant(newExponent));
            }

        } else {
            // TODO: support complex exponent subtraction
        }

        return null;

    }

    public static Expression getExpression(Component c) {
        if (c instanceof Expression) {
            return (Expression) c;
        } else if (c instanceof Term) {
            return new Expression((Term) c);
        } else if (c instanceof Factor) {
            return new Expression(new Term((Factor) c));
        } else if (c instanceof Monomial) {
            return new Expression(Term.getTerm(c));
        } else {
            throw new RuntimeException("Unexpected type [" + c.getClass() + "]");
        }
    }

    public static Expression applyConstantToExpression(Expression expr, Constant constant, TermOperator operator) {

        Term term = new Term(constant, operator, expr.getTerm());
        Expression result = new Expression(Term.getTerm(ExpressionUtils.simplify(term)));

        if (!Objects.equals(expr.getOperator(), NONE)) {
            result.setOperator(expr.getOperator());
            result.setSubExpression(applyConstantToExpression(expr.getSubExpression(), constant, operator));
        }

        return result;
    }

    public static Factor cloneAndChangeSign(Factor factor) {
        Sign sign = factor.getSign().equals(MINUS) ? PLUS : MINUS;
        if (factor instanceof Logarithm logarithm) {
            return new Logarithm(sign, new BigDecimal(logarithm.getBase().toPlainString()), logarithm.getArgument().getClone());
        } else if (factor instanceof Variable variable) {
            return new Variable(sign, variable.getName());
        } else if (factor instanceof Constant constant) {
            BigDecimal value = constant.getValue();
            boolean isNegative = value.compareTo(BigDecimal.ZERO) < 0;
            Sign constantSign = isNegative ? MINUS : PLUS;
            sign = sign == constantSign ? PLUS : MINUS;
            if (sign == PLUS) {
                value = value.abs();
            }
            return new Constant(sign, new BigDecimal(value.toPlainString()));
        } else if (factor instanceof Exponential exponential) {
            return new Exponential(sign, exponential.getBase().getClone(), exponential.getExponent().getClone());
        } else if (factor instanceof AbsExpression absExpression) {
            return new AbsExpression(sign, absExpression.getExpression().getClone());
        } else if (factor instanceof ParenthesizedExpression parenthesizedExpression) {
            return new ParenthesizedExpression(sign, parenthesizedExpression.getExpression().getClone());
        } else {
            // TODO
            throw new UnsupportedOperationException("Please implement it for class [" + factor.getClass() + "]");
        }
    }

    public static Term applyTermOperator(Constant a, Constant b, TermOperator operator) {

        if (operator == TermOperator.NONE) {
            throw new IllegalArgumentException("Cannot apply operator " + TermOperator.NONE.name());
        }

        /* Apply operator to simple constant */

        if (a.getClass().equals(Constant.class) && b.getClass().equals(Constant.class)) {
            if (operator == MULTIPLY) {
                return new Term(getProduct(a, b));
            } else {
                return new Term(getQuotient(a, b));
            }
        }

        /* Apply operator to constant functions */

        boolean aIsComponentFunction = a.getClass().equals(ConstantFunction.class);
        boolean bIsComponentFunction = b.getClass().equals(ConstantFunction.class);
        if (aIsComponentFunction || bIsComponentFunction) {
            Component aComp = aIsComponentFunction ? ((ConstantFunction) a).getComponent() : a;
            Component bComp = bIsComponentFunction ? ((ConstantFunction) b).getComponent() : b;
            return new Term(Factor.getFactor(aComp), operator, Factor.getFactor(bComp));
        }

        /* Apply operator to fractions */

        Fraction af;
        if (a instanceof Fraction) {
            af = (Fraction) a;
        } else {
            af = new Fraction(a, new Constant("1"));
        }

        Fraction bf;
        if (b instanceof Fraction) {
            bf = (Fraction) b;
        } else {
            bf = new Fraction(new Constant("1"), b);
        }

        Constant numerator;
        Constant denominator;
        if (operator == MULTIPLY) {
            numerator = getProduct(af.getNumerator(), bf.getNumerator());
            denominator = getProduct(af.getDenominator(), bf.getDenominator());
        } else {
            numerator = getProduct(af.getNumerator(), bf.getDenominator());
            denominator = getProduct(af.getDenominator(), bf.getNumerator());
        }
        return new Term(new Fraction(numerator, denominator));

    }


    private static Constant getProduct(Constant a, Constant b) {
        return new Constant(a.getValue().multiply(b.getValue()));
    }

    private static Constant getQuotient(Constant a, Constant b) {
        BigDecimal quotient = MathUtils.divide(a.getValue(), (b.getValue()));
        if (!MathUtils.isIntegerValue(quotient)) {
            return new Fraction(a, b);
        } else {
            return new Constant(quotient);
        }
    }

    public static Base getBase(Component component) {
        Factor f = Factor.getFactor(component);
        return f instanceof Base b ? b : new ParenthesizedExpression(f);
    }

    public static boolean isZero(Component component) {
        return component != null && component.isScalar() && component.getValue().compareTo(BigDecimal.ZERO) == 0;
    }

    public static boolean isOne(Component component) {
        return component != null && component.isScalar() && component.getValue().compareTo(BigDecimal.ONE) == 0;
    }

    private static boolean isInteger(Factor factor) {
        return factor.isScalar() && MathUtils.isIntegerValue(factor.getValue());
    }

    private static boolean isPositive(Factor exp) {
        return exp.getValue().compareTo(BigDecimal.ZERO) > 0;
    }
}
