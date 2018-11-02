package com.nemesis.mathserver.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.nemesis.mathserver.component.CommonUtils.MAIN_WIDTH;

@Route("calculator")
@Component
@UIScope
//@VaadinSessionScope
public class MainView extends VerticalLayout {

    private final TextField inputBox;
    private final TextField outputBox;

    private final CommonUtils commonUtils;

    private final Div simpleCalculatorDiv;
    private final Div expressionSolverDiv;


    @Autowired
    public MainView(@Qualifier("inputBox") TextField inputBox,
                    @Qualifier("outputBox") TextField outputBox,
                    @Qualifier("simpleCalculatorDiv") Div simpleCalculatorDiv,
                    @Qualifier("expressionSolverDiv") Div expressionSolverDiv,
                    CommonUtils commonUtils) {

        this.inputBox = inputBox;
        this.outputBox = outputBox;
        this.commonUtils = commonUtils;
        this.simpleCalculatorDiv = simpleCalculatorDiv;
        this.expressionSolverDiv = expressionSolverDiv;
        buildUI();
    }

    private void buildUI() {

        /* Text boxes */

        outputBox.setReadOnly(true);
        outputBox.setSizeFull();
        inputBox.setSizeFull();
        inputBox.setAutofocus(true);

        add(inputBox);
        add(outputBox);

        /* Base commands */

        ComponentEventListener<ClickEvent<Button>> clean = event -> {
            outputBox.clear();
            inputBox.clear();
        };

        HorizontalLayout baseCommands = new HorizontalLayout();
        baseCommands.setWidth(MAIN_WIDTH);
        baseCommands.setPadding(true);
        baseCommands.add(commonUtils.buildButton(clean, "Clean", "100%"));
        add(baseCommands);

        /* Tabs*/

        Tabs tabs = new Tabs();
        tabs.setWidth(MAIN_WIDTH);
        Tab simpleCalculatorTab = commonUtils.getTab("Simple calculator");
        Tab expressionSolverTab = commonUtils.getTab("Expression solver");
        tabs.add(simpleCalculatorTab);
        tabs.add(expressionSolverTab);


        Map<Tab, Div> tabsComponentsMap = new HashMap<>();
        tabsComponentsMap.put(simpleCalculatorTab, simpleCalculatorDiv);
        tabsComponentsMap.put(expressionSolverTab, expressionSolverDiv);

        tabs.addSelectedChangeListener(event -> {
                    tabsComponentsMap.values().forEach(elem -> elem.setVisible(false));
                    tabsComponentsMap.get(tabs.getSelectedTab()).setVisible(true);
                }
        );

        add(tabs);
        add(simpleCalculatorDiv);
        add(expressionSolverDiv);

    }

}
