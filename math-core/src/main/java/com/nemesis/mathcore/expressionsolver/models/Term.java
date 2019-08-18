package com.nemesis.mathcore.expressionsolver.models;

/*
         Term ::= Factor * Term
         Term ::= Factor / Term
         Term ::= Factor
 */

import com.nemesis.mathcore.utils.MathUtils;

import java.math.BigDecimal;

public class Term {

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

}
