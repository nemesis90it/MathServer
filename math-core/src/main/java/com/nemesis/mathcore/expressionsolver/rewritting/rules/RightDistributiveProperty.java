package com.nemesis.mathcore.expressionsolver.rewritting.rules;

import com.nemesis.mathcore.expressionsolver.expression.components.*;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.MULTIPLY;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.NONE;

public class RightDistributiveProperty implements Rule {

    @Override
    public Predicate<Component> precondition() {
        return component -> {
            if (!(component instanceof Term)) {
                return false;
            }
            Term term = ((Term) component);
            Factor factor = term.getFactor();
            Term subTerm = term.getSubTerm();

            return subTerm != null
                    && factor instanceof ParenthesizedExpression
                    && Objects.equals(term.getOperator(), MULTIPLY)   // Distributive property cannot be applied with DIVISION
                    && subTerm.getFactor() instanceof Constant
                    && Objects.equals(subTerm.getOperator(), NONE);
        };
    }

    @Override
    public Function<Component, ? extends Component> transformer() {
        return component -> {
            Term term = (Term) component;
            ParenthesizedExpression parExpression = (ParenthesizedExpression) term.getFactor();
            Constant constant = (Constant) term.getSubTerm().getFactor();    // TODO: support distributive property with other components
            if (parExpression.getSign() == (MINUS)) {
                // Move sign from parenthesis to constant
                constant = (Constant) ComponentUtils.cloneAndChangeSign(constant);
            }
            return ComponentUtils.applyConstantToExpression(parExpression.getExpression(), constant, term.getOperator());
        };
    }

}