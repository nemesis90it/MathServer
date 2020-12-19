package com.nemesis.mathcore.expressionsolver.rewritting.rules;


import com.nemesis.mathcore.expressionsolver.components.*;
import com.nemesis.mathcore.expressionsolver.exception.IncompatibleMonomialsException;
import com.nemesis.mathcore.expressionsolver.models.Polynomial;
import com.nemesis.mathcore.expressionsolver.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.nemesis.mathcore.expressionsolver.operators.TermOperator.NONE;

/*

    Algorithm Structure:

        S = SubTerm
        F = factor
        W = Wrapped Expression
        N = Null
        
                                   [1]                                     TERM
                                           │------------------------------------------------------------------│

                                                FACTOR       OP                    SUB_TERM
                                           │--------------│------│--------------------------------------------│


                                                                                   SubTerm
                                                                      FACTOR       OP        SUB_TERM
                                                                   │-----------│------│--------------------│
     ┬        ┬       ┬        ┬
     │        │       │        │   E(S)        WrExpr       *        WrExpr       *            [1]                 A(W) -> B(*) -> C(W) -> D(*) -> E(S)     Example: (8x-2)*(2x+1)*[SubTerm]
     │        │       │   D(*) │   E(W)        WrExpr       *        WrExpr       *            [2]                 A(W) -> B(*) -> C(W) -> D(*) -> E(W)     Example: (8x-2)*(2x+1)*[WrExpr]
     │        │       │        │   E(F)        WrExpr       *        WrExpr       *            [3]                 A(W) -> B(*) -> C(W) -> D(*) -> E(F)     Example: (8x-2)*(2x+1)*[Factor]
     │        │  C(W) │        ┴
     │        │       │        ┬
     │   B(*) │       │   D(N) │               WrExpr       *        WrExpr      NONE          NULL                A(W) -> B(*) -> C(W) -> D(N)             Example: (8x-2)*(2x+1)
     │        │       ┴        ┴
A(W) │        │       ┬        ┬
     │        │       │        │   E(S)        WrExpr       *        WrExpr       *            [1]                 A(W) -> B(*) -> C(F) -> D(*) -> E(S)     Example: (8x-2)*3*[SubTerm]
     │        │       │   D(*) │   E(W)        WrExpr       *        WrExpr       *            [2]                 A(W) -> B(*) -> C(F) -> D(*) -> E(W)     Example: (8x-2)*3*[WrExpr]
     │        │       │        │   E(F)        WrExpr       *        WrExpr       *            [3]                 A(W) -> B(*) -> C(F) -> D(*) -> E(F)     Example: (8x-2)*3*[Factor]
     │        │  C(F) │        ┴
     │        │       │        ┬
     │        │       │   D(N) │               WrExpr       *        WrExpr      NONE          NULL                A(W) -> B(*) -> C(F) -> D(N)             Example: (8x-2)*3
     │        ┴       ┴        ┴          
     │        ┬
     │   B(N) │                    [2]         WrExpr      NONE                     NULL                           A(W) -> B(N)                             Example: (8x-2)
     ┴        ┴
      

                                                                                 SubTerm
                                                                      FACTOR       OP        SUB_TERM
                                                                   │-----------│------│--------------------│
     ┬        ┬       ┬        ┬
     │        │       │        │   E(S)        Factor       *        WrExpr       *            [1]                 A(F) -> B(*) -> C(W) -> D(*) -> E(S)     Example: ln(1)*(2x+1)*[SubTerm]
     │        │       │   D(*) │   E(W)        Factor       *        WrExpr       *            [2]                 A(F) -> B(*) -> C(W) -> D(*) -> E(W)     Example: ln(1)*(2x+1)*[WrExpr]
     │        │       │        │   E(F)        Factor       *        WrExpr       *            [3]                 A(F) -> B(*) -> C(W) -> D(*) -> E(F)     Example: ln(1)*(2x+1)*[Factor]
     │        │  C(W) │        ┴
     │        │       │        ┬
     │   B(*) │       │   D(N) │               Factor       *        WrExpr      NONE          NULL                A(F) -> B(*) -> C(W) -> D(N)             Example: ln(1)*(2x+1)
     │        │       ┴        ┴
A(F) │        │       ┬        ┬
     │        │       │        │   E(S)        Factor       *        WrExpr       *            [1]                 A(F) -> B(*) -> C(F) -> D(*) -> E(S)     Example: ln(1)*3*[SubTerm]
     │        │       │   D(*) │   E(W)        Factor       *        WrExpr       *            [2]                 A(F) -> B(*) -> C(F) -> D(*) -> E(W)     Example: ln(1)*3*[WrExpr]
     │        │       │        │   E(F)        Factor       *        WrExpr       *            [3]                 A(F) -> B(*) -> C(F) -> D(*) -> E(F)     Example: ln(1)*3*[Factor]
     │        │  C(F) │        ┴
     │        │       │        ┬
     │        │       │   D(N) │               Factor       *        WrExpr      NONE          NULL                A(F) -> B(*) -> C(F) -> D(N)             Example: ln(1)*3
     │        ┴       ┴        ┴          
     │        ┬
     │   B(N) │                    [3]         Factor      NONE                     NULL                           A(F) -> B(N)                             Example: ln(1)
     ┴        ┴
      
 */

public class PolynomialMultiplication implements Rule {

    @Override
    public Predicate<Component> precondition() {
        return Term.class::isInstance;
    }

    @Override
    public Function<Component, ? extends Component> transformer() {
        return component -> {

            Term term = (Term) component;

//            // No polynomials directly in this term
//            if (!(isParenthesizedExpression(term.getFactor()) || isParenthesizedExpression(term.getSubTerm()))) {
//                return component;
//            }

            final Pair<LinkedList<Polynomial>, Term> polynomialsAndSubTerm = this.toPolynomials(term);

            // Component does not contain only polynomial (except optional subTerm)
            if (polynomialsAndSubTerm == null) {
                return component;
            }

            final List<Polynomial> polynomials = polynomialsAndSubTerm.getLeft();

            // No polynomials to multiply
            if (polynomials.size() <= 1) {
                return component;
            }

            final Term subTerm = polynomialsAndSubTerm.getRight();

            final Polynomial result;

            try {
                result = polynomials.stream().reduce(Polynomial.IDENTITY_ELEMENT, Polynomial::multiply);
            } catch (IncompatibleMonomialsException e) {
                return component;
            }

            final Expression product = ComponentUtils.monomialsToExpression(result.getMonomials().iterator());

            if (subTerm == null) {
                return product;
            } else {
                return new Term(product, TermOperator.DIVIDE, subTerm);
            }
        };
    }

    private Pair<LinkedList<Polynomial>, Term> toPolynomials(Term term) {
        final Term subTerm = term.getSubTerm();
        Pair<LinkedList<Polynomial>, Term> factorsAndSubTerm = switch (term.getOperator()) {
            case MULTIPLY -> this.toPolynomials(subTerm);
            case NONE -> Pair.of(new LinkedList<>(), null);
            case DIVIDE -> Pair.of(new LinkedList<>(), subTerm);
        };
        if (factorsAndSubTerm == null) {
            return null;
        }
        final Polynomial polynomial = Polynomial.getPolynomial(term.getFactor());
        if (polynomial == null) {
            return null;
        }
        factorsAndSubTerm.getLeft().addFirst(polynomial);
        return factorsAndSubTerm;
    }

    private boolean isParenthesizedExpression(Factor factor) {
        return factor instanceof ParenthesizedExpression;
    }

    private boolean isParenthesizedExpression(Term st) {
        return st != null && st.getOperator() == NONE && st.getFactor() instanceof ParenthesizedExpression;
    }
}
