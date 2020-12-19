package com.nemesis.mathcore.expressionsolver.monomial;

import com.nemesis.mathcore.expressionsolver.components.*;
import com.nemesis.mathcore.expressionsolver.exception.UnexpectedComponentTypeException;
import com.nemesis.mathcore.expressionsolver.models.Domain;
import com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.stringbuilder.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import com.nemesis.mathcore.expressionsolver.utils.FactorMultiplier;
import com.nemesis.mathcore.expressionsolver.utils.FactorSignInverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static com.nemesis.mathcore.expressionsolver.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.operators.TermOperator.DIVIDE;
import static com.nemesis.mathcore.expressionsolver.utils.ComponentUtils.isOne;
import static java.math.BigDecimal.ONE;

@Data
@EqualsAndHashCode(callSuper = false)
public class Monomial extends Component {

    private Constant coefficient;
    private LiteralPart literalPart;

    public Monomial(Constant coefficient, LiteralPart literalPart) {
        this.coefficient = coefficient;
        this.literalPart = literalPart.getClone();
        moveMinusSignFromBasesToCoefficient(this);
    }

    public Constant getCoefficient() {
        return coefficient;
    }

    public static Monomial getZero(LiteralPart exponentials) {
        return new Monomial(new Constant(0), exponentials);
    }


    /* Monomial Tree
     (1)     -----------------------------------EXPRESSION-----------------------------------
     (2)     ----------------------------TERM-------------------------------   NONE     null
     (3)     --LEFT_FACT--  OP   --------------RIGHT_TERM---------------
     (4)                         --RIGHT_FACT--   OP   ---LEFT_TERM---
     (5)         CONST      *         FACT       NONE      null
     (6)         CONST      *         EXPON      NONE      null
     (7)         FACT       *         CONST      NONE      null
     (8)         EXPON      *         CONST      NONE      null
     (5.1)       CONST      *         FACT        *    [see line (4)]
     (6.1)       CONST      *         EXPON       *    [see line (4)]
     (7.1)       FACT       *         CONST       *    [see line (4)]
     (8.1)       EXPON      *         CONST       *    [see line (4)]
     (9)         FACT      NONE                  null
 */
    public static Monomial getMonomial(Component component) {

        Term term;
        if (component instanceof ParenthesizedExpression parExpression) {
            return getMonomial(parExpression);
        } else if (component instanceof Expression expression) {
            return getMonomial(expression);
        } else if (component instanceof Factor) {
            term = new Term((Factor) component); // see line (3) with OP = NONE, then see line (9)
        } else if (component instanceof Term t) {
            if (t.getOperator() == TermOperator.NONE) {
                // Call this method again so that will be executed one of the other cases (see above)
                return getMonomial(t.getFactor());
            } else {
                term = t;
            }
        } else {
            throw new UnexpectedComponentTypeException("Unexpected type [" + component.getClass() + "]");
        }

        if (term.getOperator() == DIVIDE && !term.getSubTerm().isScalar()) {
            return null; // rational monomials aren't considered as monomials
        }

        final Set<Factor> originalFactors = getFactors(term);
        if (originalFactors.isEmpty()) {
            return null;
        }
        Set<Factor> factors = Factor.multiplyFactors(originalFactors);

        final List<Factor> constants = factors.stream()
                .filter(Component::isScalar)
                .collect(Collectors.toList());

        Constant constant;
        if (!constants.isEmpty()) {
            constant = FactorMultiplier.get(Factor.class).apply(constants).getValueAsConstant();
            factors.removeAll(constants);
        } else {
            constant = new Constant(1);
        }

        final TreeSet<Exponential> exponentials = factors.stream().map(Exponential::getExponential).collect(Collectors.toCollection(TreeSet::new));

        return new Monomial(constant, new LiteralPart(exponentials));
    }

    private static Monomial getMonomial(Expression expression) {
        if (expression.getOperator().equals(ExpressionOperator.NONE)) {  // see line (2), EXPRESSION operation must be NONE
            return getMonomial(expression.getTerm());
        } else {
            return null;
        }
    }

    private static Monomial getMonomial(ParenthesizedExpression parExpression) {
        if (parExpression.getOperator().equals(ExpressionOperator.NONE)) { // see line (2), EXPRESSION operation must be NONE
            Expression expression;
            if (parExpression.getSign() == MINUS) {
                // Remove MINUS sign, changing all signs inside parenthesis
                expression = ComponentUtils.applyConstantToExpression(parExpression.getExpression(), new Constant("-1"), TermOperator.MULTIPLY);
            } else {
                expression = parExpression.getExpression();
            }
            // Call this method again so that will be executed the "Expression" case (see below)
            return getMonomial(expression);
        } else {
            return null;
        }
    }

    private static Set<Factor> getFactors(Term term) {

        if (term == null) {
            return new TreeSet<>();
        }

        Set<Factor> factors = new TreeSet<>();

        // Add LEFT_FACT and RIGHT_FACT. See lines (5), (6), (7), (8), (5.1), (6.1), (7.1), (8.1)
        factors.add(term.getFactor());

        Term rightTerm = term.getSubTerm();  // see line (4))

        if (rightTerm != null) { // see lines (5.1), (6.1), (7.1), (8.1)
            Set<Factor> otherFactors = getFactors(rightTerm);
            if (term.getOperator() == DIVIDE) {  // a/b/c... => a*1/b*1/c...
                otherFactors = otherFactors.stream()
                        .map(factor -> new ParenthesizedExpression(new Term(new Constant(ONE), DIVIDE, factor)))
                        .collect(Collectors.toSet());
            }

            factors.addAll(otherFactors);
            return factors;
        } else {
            return factors; // see line (9)
        }
    }

    @Override
    public BigDecimal getValue() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Component getDerivative(Variable var) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Component rewrite(Rule rule) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Boolean isScalar() {
        return this.coefficient.isScalar() && this.literalPart.stream().allMatch(Exponential::isScalar);
    }

    @Override
    public Constant getValueAsConstant() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean contains(Variable variable) {
        return literalPart.stream()
                .anyMatch(exponential -> exponential.contains(variable));
    }

    @Override
    public Monomial getClone() {
        return new Monomial(coefficient.getClone(), this.literalPart.getClone());
    }

    @Override
    public Domain getDomain(Variable variable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Variable> getVariables() {
        throw new UnsupportedOperationException();
    }


    /*
       Comparing each n-th element of the two literalPart (this and other), then compare the two coefficients.

       Looping over the elements:
        - if the two n-th elements are not equals, they difference determines the first difference of the two sets
        - if the two n-th elements are the same, continue the loop to compare the next elements

       Examples:
            [x^2, y^5, z^3] is considered before of [x^2, y^4, z]
                1) x^2 = x^2, continue loop
                2) y^5 < y^4
                    2.1)  5 > 4, then reverse (see exponential comparator), then the comparison of the two sets inherit the comparison result between these two elements
            [x^2, y^5] is considered before of [x^2, z^6]
                1) x^2 = x^2, continue loop
                2) y^5 > z^6 (because y > z), then the comparison of the two sets inherit the comparison result between these two elements
            [x^2, y^5] is considered before of [x^2]
                1) x^2 = x^2, continue loop
                2) No more elements on second set, then the comparison of the two sets inherit the comparison result between y^5 and null (y^5 > null)
    */
    @Override
    public int compareTo(Component c) {
        if (c instanceof Monomial m) {
            Comparator<Monomial> monomialComparator = Comparator.comparing(Monomial::getLiteralPart).thenComparing(Monomial::getCoefficient);
            return monomialComparator.compare(this, m);
        } else {
            throw new UnsupportedOperationException("Comparison between [" + this.getClass() + "] and [" + c.getClass() + "] is not supported yet");
        }
    }


    // Move sign from base to coefficient (actually, '-x' and 'x' have the same base 'x')
    private static void moveMinusSignFromBasesToCoefficient(Monomial monomial) {

        final Set<Exponential> negativeExponentials = new TreeSet<>();

        for (Exponential exponential : monomial.getLiteralPart()) {

            final boolean isBaseNegative = isOne(exponential.getExponent()) && exponential.getBase().getSign() == MINUS;
            final boolean isExponentialNegative = exponential.getSign() == MINUS;

            if (isBaseNegative) {
                exponential.getBase().setSign(PLUS);
            }
            if (isExponentialNegative) {
                exponential.setSign(PLUS);
            }
            if (isBaseNegative ^ isExponentialNegative) {
                negativeExponentials.add(exponential);
            }
        }

        if (negativeExponentials.size() % 2 != 0) {
            monomial.setCoefficient((Constant) FactorSignInverter.cloneAndChangeSign(monomial.getCoefficient()));
        }

    }

    @Override
    public String toString() {
        return ExpressionBuilder.product(coefficient.toString(), literalPart.toString());
    }

    @Override
    public String toLatex() {
        throw new UnsupportedOperationException("Not implemented");
    }

}