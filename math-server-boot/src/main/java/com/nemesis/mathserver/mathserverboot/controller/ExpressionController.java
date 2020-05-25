package com.nemesis.mathserver.mathserverboot.controller;


import com.nemesis.mathcore.expressionsolver.ExpressionParser;
import com.nemesis.mathcore.expressionsolver.ExpressionUtils;
import com.nemesis.mathcore.expressionsolver.utils.Constants;
import com.nemesis.mathcore.expressionsolver.utils.MathCoreContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping(value = "/expression", produces = "text/plain")
public class ExpressionController {

    private static final Pattern genericNumPattern = Pattern.compile(Constants.IS_GENERIC_NUM_REGEX);
    private static final Pattern derivativePattern = Pattern.compile(Constants.DERIVATIVE_INPUT_REGEX);

    @PostConstruct
    public void init() {
        MathCoreContext.setNumericMode(MathCoreContext.Mode.FRACTIONAL);
    }

    @GetMapping("/evaluate")
    public String evaluate(@RequestParam String expression) {

        try {
            String result;
            expression = expression.replace(" ", "");
            if (genericNumPattern.matcher(expression).matches()) {
                log.info("Evaluating: [" + expression + "]");
                result = String.valueOf(ExpressionParser.parse(expression).getValue());
            } else {
                final Matcher derivativeInputMatcher = derivativePattern.matcher(expression);
                if (derivativeInputMatcher.matches()) {
                    final String function = derivativeInputMatcher.group(1);
                    final String variable = derivativeInputMatcher.group(2);
                    log.info("Evaluating derivative of [{}] for variable [{}]", function, variable);
                    result = ExpressionUtils.getDerivative(function, variable.charAt(0)).toLatex();
                } else {
                    log.info("Simplifying function [" + expression + "]");
                    result = ExpressionUtils.simplify(expression).toLatex();
                }
            }
            log.info("Result [" + result + "]");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

}
