package com.nemesis.mathserver.mathserverboot.controller;


import com.nemesis.mathcore.expressionsolver.ExpressionParser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/expression")
public class ExpressionController {

    @GetMapping("/evaluate")
    public BigDecimal evaluate(@RequestParam String expression) {
        System.out.println("Evaluating: [" + expression + "]");
        BigDecimal result = ExpressionParser.parse(expression);
        System.out.println("Result [" + result + "]");
        return result;
    }

}
