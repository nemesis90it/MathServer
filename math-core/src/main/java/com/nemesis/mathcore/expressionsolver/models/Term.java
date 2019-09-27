package com.nemesis.mathcore.expressionsolver.models;

/*
         Term ::= Factor * Term
         Term ::= Factor / Term
         Term ::= Factor
 */

import com.nemesis.mathcore.utils.MathUtils;

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.ExpressionBuilder.*;
import static com.nemesis.mathcore.expressionsolver.models.TermOperator.DIVIDE;
import static com.nemesis.mathcore.expressionsolver.models.TermOperator.MULTIPLY;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.IS_ZERO_REGEXP;

public class Term extends Component {

    private Factor factor;
    private TermOperator operator;
    private Term subTerm;

    public Term(Factor factor, TermOperator operator, Term subTerm) {
        this.factor = factor;
        this.operator = operator;
        this.subTerm = subTerm;
    }

    public Term(Factor factor) {
        this.factor = factor;
        this.operator = TermOperator.NONE;
    }

    public Factor getFactor() {
        return factor;
    }

    public TermOperator getOperator() {
        return operator;
    }

    public Term getSubTerm() {
        return subTerm;
    }

    @Override
    public BigDecimal getValue() {
        switch (operator) {
            case NONE:
                return factor.getValue();
            case DIVIDE:
                return MathUtils.divide(factor.getValue(), subTerm.getValue());
            case MULTIPLY:
                return factor.getValue().multiply(subTerm.getValue());
            default:
                throw new RuntimeException("Illegal term operator '" + operator + "'");
        }
    }

    @Override
    public String getDerivative() {

        String factor = this.factor.toString();
        String subTerm;
        if (this.subTerm != null) {
            subTerm = this.subTerm.toString();
        } else {
            subTerm = "0";
        }

        String factorDerivative = this.factor.getDerivative();
        if (this.subTerm == null) {
            return factorDerivative;
        }
        String subTermDerivative = this.subTerm.getDerivative();

        switch (operator) {
            case NONE:
                if (factorDerivative.matches(IS_ZERO_REGEXP)) {
                    return "0";
                }
            case DIVIDE:
                return division(difference(product(factorDerivative, subTerm), product(factor, subTermDerivative)), power(subTerm, "2"));
            case MULTIPLY:
                return sum(product(factorDerivative, subTerm), product(factor, subTermDerivative));
            default:
                throw new RuntimeException("Unexpected operator");
        }
    }

    @Override
    public String toString() {
        if (subTerm == null) {
            return "" + factor;
        } else {
            if (operator.equals(DIVIDE)) {
                return division(factor.toString(), subTerm.toString());
            } else if (operator.equals(MULTIPLY)) {
                return product(factor.toString(), subTerm.toString());
            }
        }
        throw new RuntimeException("Unexpected operator [" + operator + "]");
    }

}
