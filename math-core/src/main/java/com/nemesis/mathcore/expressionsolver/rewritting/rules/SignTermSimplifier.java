package com.nemesis.mathcore.expressionsolver.rewritting.rules;

import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.components.Term;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.FactorSignInverter;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import static com.nemesis.mathcore.expressionsolver.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.operators.TermOperator.NONE;

public class SignTermSimplifier implements Rule {

    @Override
    public Predicate<Component> precondition() {
        return component -> {

            Term term;
            if (component instanceof Term) {
                term = ((Term) component);
            } else {
                return false;
            }

            return term.getFactor() != null
                    && term.getSubTerm() != null
                    && term.getSubTerm().getFactor() != null
                    && Objects.equals(term.getFactor().getSign(), MINUS)
                    && !Objects.equals(term.getOperator(), NONE)
                    && Objects.equals(term.getSubTerm().getFactor().getSign(), MINUS);
        };
    }

    @Override
    public UnaryOperator<Component> transformer() {
        return component -> {
            Term term = ((Term) component);
            term.setFactor(FactorSignInverter.cloneAndChangeSign(term.getFactor()));
            term.getSubTerm().setFactor(FactorSignInverter.cloneAndChangeSign(term.getSubTerm().getFactor()));
            return term;
        };
    }

}