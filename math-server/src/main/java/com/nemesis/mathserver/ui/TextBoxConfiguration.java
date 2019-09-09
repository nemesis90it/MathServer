package com.nemesis.mathserver.ui;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@UIScope
@Lazy
public class TextBoxConfiguration {

    @Bean
    @UIScope
//    @Primary
    //@VaadinSessionScope
    public TextField inputBox() {
        TextField inputTextField = new TextField("Input");
        inputTextField.setAutofocus(true);
        return inputTextField;
    }

    @Bean
    @UIScope
    //@VaadinSessionScope
    public TextField outputBox() {
        return new TextField("Output");
    }

}
