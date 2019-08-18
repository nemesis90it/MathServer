package com.nemesis.mathserver.ui;


import com.nemesis.mathcore.expressionsolver.ExpressionParser;
import com.nemesis.mathcore.utils.MathUtils;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;

import static com.nemesis.mathserver.ui.CommonUtils.TAB_WIDTH;

@Configuration
@UIScope
@Lazy
public class DivConfiguration {

    private CommonUtils commonsUtils;

    private final TextField inputBox;
    private final TextField outputBox;

    @Autowired
    public DivConfiguration(CommonUtils commonsUtils
                            /*@Qualifier("inputBox") TextField inputBox,
                            @Qualifier("outputBox") TextField outputBox*/) {
        this.commonsUtils = commonsUtils;
        this.inputBox = commonsUtils.getInputBox();
        this.outputBox = commonsUtils.getOutputBox();
    }

    @Bean
    @UIScope
    //@VaadinSessionScope
    public Div simpleCalculatorDiv() {
        List<HorizontalLayout> functionCommandsRows = new ArrayList<>();

        functionCommandsRows.add(new HorizontalLayout(
                commonsUtils.buildButtonWithSingleArgumentAction(MathUtils::exponential, "\\(e^x\\)"),
                commonsUtils.buildButtonWithSingleArgumentAction(MathUtils::factorial, "\\(x!\\)"),
                commonsUtils.buildButtonWithSingleArgumentAction(MathUtils::ln, "\\(ln(x)\\)")
        ));

        functionCommandsRows.add(new HorizontalLayout(
                commonsUtils.buildButtonWithSingleArgumentAction(MathUtils::sin, "\\(sin(x)\\)"),
                commonsUtils.buildButtonWithSingleArgumentAction(MathUtils::cos, "\\(cos(x)\\)"),
                commonsUtils.buildButtonWithSingleArgumentAction(MathUtils::tan, "\\(tan(x)\\)")
        ));

        Div div = new Div();
        div.add(functionCommandsRows.toArray(new Component[0]));
        div.setWidth(TAB_WIDTH);

        return div;
    }

    @Bean
    @UIScope
    //@VaadinSessionScope
    public Div expressionSolverDiv() {

        ComponentEventListener<ClickEvent<Button>> evaluate = getEvaluateEventListener();

        List<HorizontalLayout> functionCommandsRows = new ArrayList<>();

        functionCommandsRows.add(new HorizontalLayout(
                commonsUtils.buildInputButton("1"),
                commonsUtils.buildInputButton("2"),
                commonsUtils.buildInputButton("3"),
                commonsUtils.buildInputButton("4"),
                commonsUtils.buildInputButton("5")
        ));

        functionCommandsRows.add(new HorizontalLayout(
                commonsUtils.buildInputButton("6"),
                commonsUtils.buildInputButton("7"),
                commonsUtils.buildInputButton("8"),
                commonsUtils.buildInputButton("9"),
                commonsUtils.buildInputButton("0")
        ));

        functionCommandsRows.add(new HorizontalLayout(
                commonsUtils.buildInputButton("."),
                commonsUtils.buildInputButton("+"),
                commonsUtils.buildInputButton("-"),
                commonsUtils.buildInputButton("*"),
                commonsUtils.buildInputButton("/")
        ));
        functionCommandsRows.add(new HorizontalLayout(
                commonsUtils.buildInputButton("("),
                commonsUtils.buildInputButton(")"),
                commonsUtils.buildInputButton("^"),
                commonsUtils.buildInputButton("!"),
                commonsUtils.buildButton(evaluate, "=", "20%")
        ));

        Div div = new Div();
        div.add(functionCommandsRows.toArray(new Component[0]));
        div.setWidth(TAB_WIDTH);
        div.setVisible(false);

        return div;
    }

    private ComponentEventListener<ClickEvent<Button>> getEvaluateEventListener() {

        if (inputBox.getValue().contains("x")) {
            return event -> outputBox.setValue("Invalid input: [" + inputBox.getValue() + "]");
        } else {
            return event -> {
                try {
                    outputBox.setValue(String.valueOf(ExpressionParser.evaluate(inputBox.getValue())));
                } catch (Exception e) {
                    outputBox.setValue("Invalid input: [" + inputBox.getValue() + "]");
                }
            };
        }

    }

}
