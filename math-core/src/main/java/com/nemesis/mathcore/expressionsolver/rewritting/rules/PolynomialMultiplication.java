//package com.nemesis.mathcore.expressionsolver.rewritting.rules;
//
//import com.nemesis.mathcore.expressionsolver.expression.components.Component;
//import com.nemesis.mathcore.expressionsolver.expression.components.Factor;
//import com.nemesis.mathcore.expressionsolver.expression.components.ParenthesizedExpression;
//import com.nemesis.mathcore.expressionsolver.expression.components.Term;
//import com.nemesis.mathcore.expressionsolver.models.Monomial;
//import com.nemesis.mathcore.expressionsolver.models.Polynomial;
//import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.function.Function;
//import java.util.function.Predicate;
//
//import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.NONE;
//
///* TODO
//         Example: (8*x)*(2*x+1) -> 16x^2 + 8x
//
//         ParenthesizedExpression                           *                    ParenthesizedExpression
//                   (8*x)                                                                (2*x+1)
//                Expression                                                             Expression
//                    8*x                                                                  2*x+1
//                   Term                                                                  Term       +       SubExpression
//                    8*x                                                                   2*x                   Term
//              Factor * SubTerm                                                      Factor * SubTerm            Factor
//                 8        Factor NONE null                                              2       x                 1
//                            x
//
//         Example: 8*x*(2*x+1)+1+(3*x)  -> 16x^2 + 8x + 1 + 3x
//         Example: x*(2*x+1)+1+(3*x)  -> 2x^2 + 2x + 1 + 3x
//         Example: (2*x+1)*(8*x)+1+(3*x)  -> 16x^2 + 8x + 1 + 3x
//         Example: (2*x+1)*8*x+1+(3*x)  -> 16x^2 + 8x + 1 + 3x
//         Example: (2*x+1)*x+1+(3*x)  -> 2x^2 + 2x + 1 + 3x
// */
//
//public class PolynomialMultiplication implements Rule {
//
//    @Override
//    public Predicate<Component> precondition() {
//        return Term.class::isInstance;
//    }
//
//    @Override
//    public Function<Component, ? extends Component> transformer() {
//        return component -> {
//            Term term = (Term) component;
//
//            Factor leftComponent = term.getFactor();
//
//            Term subTerm = term.getSubTerm();
//            Component rightComponent = (subTerm != null && subTerm.getOperator() == NONE) ? subTerm.getFactor() : subTerm;
//
//            if (leftComponent instanceof ParenthesizedExpression leftExpr && rightComponent instanceof ParenthesizedExpression rightExpr) {
//                if (rightComponent == null) {
//                    return component;
//                }
//
//                // TODO: manage empty polynomials
//
//                List<Term> multipliedMonomials = new ArrayList<>();
//
//                Polynomial leftPolynomial = Polynomial.getPolynomial(leftExpr);
//                Polynomial rightPolynomial = Polynomial.getPolynomial(rightExpr);
//                for (Monomial leftMonomial : leftPolynomial.getMonomials()) {
//                    for (Monomial rightMonomial : rightPolynomial.getMonomials()) {
//                        multipliedMonomials.add(Monomial.multiply(leftMonomial, rightMonomial));
//                    }
//
//                }
//
//
//                // TODO: multiply polynomials (see old implementation)
//            }
//
//            throw new UnsupportedOperationException("Not implemented");
//        };
//    }
//}
