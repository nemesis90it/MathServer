package com.nemesis.mathserver.mathserverboot.controller;


import com.nemesis.mathcore.expressionsolver.ExpressionParser;
import com.nemesis.mathcore.expressionsolver.ExpressionUtils;
import com.nemesis.mathcore.expressionsolver.utils.Constants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Pattern;

@RestController
@RequestMapping(value = "/expression", produces = "text/plain")
public class ExpressionController {

    @GetMapping("/evaluate")
    public String evaluate(@RequestParam String expression) {
        String result;
        if (Pattern.compile(Constants.IS_GENERIC_NUM_REGEX).matcher(expression).matches()) {
            result = String.valueOf(ExpressionParser.parse(expression).getValue());
        } else {
            result = ExpressionUtils.simplify(expression);
        }
        System.out.println("Evaluating: [" + expression + "]");
        System.out.println("Result [" + result + "]");
        return result;
    }

}
