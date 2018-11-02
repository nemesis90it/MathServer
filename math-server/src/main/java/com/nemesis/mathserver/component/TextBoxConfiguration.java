package com.nemesis.mathserver.component;

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
        return new TextField("Input");
    }

    @Bean
    @UIScope
    //@VaadinSessionScope
    public TextField outputBox() {
        return new TextField("Output");
    }

}
