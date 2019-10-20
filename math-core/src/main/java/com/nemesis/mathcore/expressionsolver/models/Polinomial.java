package com.nemesis.mathcore.expressionsolver.models;

import com.nemesis.mathcore.expressionsolver.expression.components.Component;
import com.nemesis.mathcore.expressionsolver.expression.components.Term;

import java.math.BigDecimal;

public class Polinomial extends Component {

    @Override
    public BigDecimal getValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Component getDerivative() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Term simplify() {
        throw new UnsupportedOperationException();
    }
}
