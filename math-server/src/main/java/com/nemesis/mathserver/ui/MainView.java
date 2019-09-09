package com.nemesis.mathserver.ui;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import static com.nemesis.mathserver.Constants.HOME_PAGE;
import static com.nemesis.mathserver.ui.CommonUtils.MAIN_WIDTH;

;

@PreAuthorize("hasRole('USER')")
@Route(HOME_PAGE)
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
//        Tab simpleCalculatorTab = commonUtils.buildTab("Simple calculator");
        Tab expressionSolverTab = commonUtils.buildTab("Expression solver");
//        tabs.add(simpleCalculatorTab);
        tabs.add(expressionSolverTab);


//        Map<Tab, Div> tabsComponentsMap = new HashMap<>();
//        tabsComponentsMap.put(simpleCalculatorTab, simpleCalculatorDiv);
//        tabsComponentsMap.put(expressionSolverTab, expressionSolverDiv);

//        tabs.addSelectedChangeListener(event -> {
//                    tabsComponentsMap.values().forEach(elem -> elem.setVisible(false));
//                    tabsComponentsMap.get(tabs.getSelectedTab()).setVisible(true);
//                }
//        );

        add(tabs);
//        add(simpleCalculatorDiv);
        add(expressionSolverDiv);

    }

}
