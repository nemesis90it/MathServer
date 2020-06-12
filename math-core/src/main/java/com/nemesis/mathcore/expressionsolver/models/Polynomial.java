//package com.nemesis.mathcore.expressionsolver.models;
//
//import com.nemesis.mathcore.expressionsolver.expression.components.*;
//import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
//import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
//import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.MINUS;
//import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.MULTIPLY;
//import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.NONE;
//
//public class Polynomial {
//
//    private List<Monomial> monomials;
//
//    public Polynomial(List<Monomial> monomials) {
//        this.monomials = monomials;
//    }
//
//    public static Polynomial getPolynomial(Component component) {
//
//        Term term;
//        List<Monomial> monomials = new ArrayList<>();
//
//        if (component instanceof ParenthesizedExpression parExpression) {
//            if (parExpression.getOperator().equals(ExpressionOperator.NONE)) { // see line (2), TODO:  EXPRESSION operation could not be NONE
//                Expression expression;
//                if (parExpression.getSign() == MINUS) {
//                    // Remove MINUS sign, changing all signs inside parenthesis
//                    expression = ComponentUtils.applyConstantToExpression(parExpression.getExpression(), new Constant("-1"), MULTIPLY);
//                } else {
//                    expression = parExpression.getExpression();
//                }
//                // Call this method again so that will be executed the "Expression" case (see below)
//                return getPolynomial(expression);
//            } else {
//                return null;
//            }
//        } else if (component instanceof Expression expression) {
//            term = expression.getTerm();
//            monomials.addAll(getMonomials(term));
//            if (!expression.getOperator().equals(ExpressionOperator.NONE)) {  // see line (2), TODO:  EXPRESSION operation could not be NONE
//                Expression subExpression = expression.getSubExpression();
//                // Call this method so that will be executed the "Expression" case again, using subExpression as input
//                final Polynomial polynomial = getPolynomial(subExpression);
//                if (polynomial != null) {
//                    monomials.addAll(polynomial.getMonomials());
//                }
//            }
//        } else if (component instanceof Factor) {
//            term = new Term((Factor) component); // see line (3) with OP = NONE, then see line (9)
//        } else if (component instanceof Term) {
//            term = (Term) component;
//        } else {
//            throw new IllegalArgumentException("Unexpected type [" + component.getClass() + "]");
//        }
//
//        monomials.addAll(getMonomials(term));
//        return new Polynomial(monomials);
//    }
//
//    private static List<Monomial> getMonomials(Term term) {
//
//        TermOperator termOperator = term.getOperator();
//        if (termOperator != NONE) {
//
//            Term rightTerm = term.getSubTerm();
//            if (rightTerm.getOperator() != NONE) { // see line (4))
//                return new ArrayList<>();
//            }
//
//            Factor leftFactor = term.getFactor();
//            Factor rightFactor = rightTerm.getFactor();
//            if (leftFactor instanceof Constant) {
//                return Arrays.asList(Monomial.buildMonomial((Constant) leftFactor, termOperator, rightFactor)); // see lines (5) and (6)
//            }
//            if (rightFactor instanceof Constant) {
//                return Arrays.asList(Monomial.buildMonomial((Constant) rightFactor, termOperator, leftFactor)); // see lines (7) and (8)
//            }
//        } else { // see line (9)
//            Factor factor = term.getFactor();
//            if (factor instanceof ParenthesizedExpression) {
//                // Call this method again so that will be executed the "ParenthesizedExpression" case to get the TERM
//                final Polynomial polynomial = getPolynomial(factor);
//                if (polynomial != null) {
//                    return polynomial.getMonomials();
//                } else {
//                    return new ArrayList<>();
//                }
//            }
//            if (factor instanceof Constant) {
//                // Monomial is a constant
//                return Arrays.asList(Monomial.buildMonomial((Constant) factor, MULTIPLY, Monomial.IDENTITY_BASE));
//            }
//            Constant constant;
//            if (factor.getSign().equals(MINUS)) {
//                // Move MINUS sign from factor to a new constant (-1)
//                factor = ComponentUtils.cloneAndChangeSign(factor);
//                constant = new Constant("-1");
//            } else {
//                constant = new Constant("1");
//            }
//            return Arrays.asList(Monomial.buildMonomial(constant, MULTIPLY, factor));
//        }
//        return new ArrayList<>();
//    }
//
//
//    public List<Monomial> getMonomials() {
//        return monomials;
//    }
//
//    public void setMonomials(List<Monomial> monomials) {
//        this.monomials = monomials;
//    }
//}
