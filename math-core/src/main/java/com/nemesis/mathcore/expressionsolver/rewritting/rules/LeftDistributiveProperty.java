package com.nemesis.mathcore.expressionsolver.rewritting.rules;

import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.components.Constant;
import com.nemesis.mathcore.expressionsolver.components.ParenthesizedExpression;
import com.nemesis.mathcore.expressionsolver.components.Term;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.nemesis.mathcore.expressionsolver.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.operators.TermOperator.MULTIPLY;
import static com.nemesis.mathcore.expressionsolver.operators.TermOperator.NONE;

public class LeftDistributiveProperty implements Rule {

    @Override
    public Predicate<Component> precondition() {
        return component -> {
            if (!(component instanceof Term term)) {
                return false;
            }
            Term subTerm = term.getSubTerm();
            return subTerm != null
                    && term.getFactor() instanceof Constant            // TODO: support distributive property with other components
                    && subTerm.getFactor() != null
                    && subTerm.getFactor() instanceof ParenthesizedExpression
                    && Objects.equals(term.getOperator(), MULTIPLY)   // Distributive property cannot be applied with DIVISION
                    && Objects.equals(subTerm.getOperator(), NONE);
        };
    }

    @Override
    public Function<Component, ? extends Component> transformer() {
        return component -> {
            Term term = (Term) component;
            ParenthesizedExpression parExpression = (ParenthesizedExpression) term.getSubTerm().getFactor();
            Constant constant = (Constant) term.getFactor();    // TODO: support distributive property with other components
            if (parExpression.getSign() == MINUS) {
                // Move sign from parenthesis to constant
                constant = (Constant) ComponentUtils.cloneAndChangeSign(constant);
            }
            return ComponentUtils.applyConstantToExpression(parExpression.getExpression(), constant, term.getOperator());
        };
    }

}