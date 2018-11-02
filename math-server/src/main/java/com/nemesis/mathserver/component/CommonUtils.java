package com.nemesis.mathserver.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;

import java.math.BigDecimal;
import java.util.function.Function;

@org.springframework.stereotype.Component
@Lazy
@UIScope
//@VaadinSessionScope
public class CommonUtils {

    public static final String MAIN_WIDTH = "50%";
    protected static final Integer TABS = 2;
    public static final String TAB_WIDTH = 100 / TABS + "%";

    private final TextField inputBox;
    private final TextField outputBox;

    @Autowired
    public CommonUtils(@Qualifier("inputBox") TextField inputBox, @Qualifier("outputBox") TextField outputBox) {
        this.inputBox = inputBox;
        this.outputBox = outputBox;
    }

    public Button buildButton(ComponentEventListener<ClickEvent<Button>> eventListener, String text, String width) {
        Button button = new Button(text);
        button.addClickListener(eventListener);
        button.setWidth(width);

        return button;
    }

    public Component buildInputButton(String text) {
        Button button = new Button(text);
        button.setWidth("20%");
        button.addClickListener(event -> inputBox.setValue(inputBox.getValue().concat(text)));
        return button;
    }

    public <R> Button buildButtonWithSingleArgumentAction(Function<BigDecimal, R> function, String text) {
        Button button = new Button(text);
        button.setWidth("30%");
        ComponentEventListener<ClickEvent<Button>> clickEventComponentEventListener = event -> outputBox.setValue(String.valueOf(function.apply(new BigDecimal(inputBox.getValue()))));
        button.addClickListener(clickEventComponentEventListener);
        return button;
    }

    public Tab getTab(String s) {
        Tab tab = new Tab(s);
        tab.setFlexGrow(1);
        return tab;
    }

    public TextField getInputBox() {
        return inputBox;
    }

    public TextField getOutputBox() {
        return outputBox;
    }
}
