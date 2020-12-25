package com.nemesis.mathcore.expressionsolver.rewritting;

import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.components.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Rule {

    Predicate<Component> precondition();

    Function<Component, ? extends Component> transformer();

    default Component applyTo(Component component) {
        Logger log = LoggerFactory.getLogger(this.getClass());
        Predicate<Component> condition = this.precondition();
        if (condition.test(component)) {
            Component originalComponent = component.getClone();
            component = this.transformer().apply(component);
            if (!Objects.equals(originalComponent, component)) {
                log.info("Applied rule [{}] to expression [{}], result: [{}]", this.getClass().getSimpleName(), originalComponent, component);
            }
        }
        return component;
    }


}
