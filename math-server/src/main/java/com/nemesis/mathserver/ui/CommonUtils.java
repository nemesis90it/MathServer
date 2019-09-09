package com.nemesis.mathserver.ui;

import com.nemesis.mathserver.ui.js.JsFunctions;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Element;
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

    static final String MAIN_WIDTH = "80%";
    private static final Integer TABS = 2;
    private static final Integer BUTTONS_PER_LINE = 6;
    public static final String BUTTON_WIDTH = (100 / BUTTONS_PER_LINE) + "%";
    static final String TAB_WIDTH = 100 / TABS + "%";


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

    public Component buildInputButton(final String buttonText) {
        Button button = new Button(buttonText);
        button.setWidth(BUTTON_WIDTH);
        button.addClickListener(event -> {
            String text = buttonText.replaceAll("\\\\\\(", "").replaceAll("\\\\\\)", "");
            Element element = inputBox.getElement();
            element.executeJs(getJsFunctionExecutor(JsFunctions.insertTextJsFunction), element, text);

        });
        return button;
    }

    public <R> Button buildButtonWithSingleArgumentAction(Function<BigDecimal, R> function, String text) {
        Button button = new Button(text);
        button.setWidth("30%");
        ComponentEventListener<ClickEvent<Button>> clickEventComponentEventListener = event -> {
            try {
                outputBox.setValue(String.valueOf(function.apply(new BigDecimal(inputBox.getValue()))));
            } catch (Exception e) {
                outputBox.setValue("Invalid input: [" + inputBox.getValue() + "] for requested operation");
            }
        };
        button.addClickListener(clickEventComponentEventListener);
        return button;
    }

    public Tab buildTab(String s) {
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

    public static String getJsFunctionExecutor(String jsFunction) {
        return "setTimeout(" + jsFunction + ", 0)";
    }
}
