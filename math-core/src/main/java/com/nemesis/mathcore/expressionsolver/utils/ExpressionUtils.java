package com.nemesis.mathcore.expressionsolver.utils;

import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.components.Expression;
import com.nemesis.mathcore.expressionsolver.components.Variable;
import com.nemesis.mathcore.expressionsolver.models.Domain;
import com.nemesis.mathcore.expressionsolver.parser.ExpressionParser;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.rewritting.Rules;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.math.BigDecimal;
import java.util.*;

import static com.nemesis.mathcore.expressionsolver.utils.ComponentUtils.isZero;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Slf4j
public class ExpressionUtils {

    private static final String EXECUTION_CHAIN_MDC_PARAM = "executionId";
    private static int currentDepth = 0;

    private static Map<String, Map<Integer, String>> lastFinishedExecutionByThread = new HashMap<>();


    public static BigDecimal evaluate(String expression) {
        final Expression parsedExpression = ExpressionParser.parse(expression);
        BigDecimal rawResult = parsedExpression.getValue();
        return SyntaxUtils.removeNonSignificantZeros(rawResult);
    }

    public static Component getDerivative(String expression, Variable var) {
        Expression parsedExpr = ExpressionParser.parse(expression);
        Component simplifiedDerivative = getDerivative(parsedExpr, var);
        return simplifiedDerivative;
    }

    public static Component getDerivative(Component function, Variable var) {
        Component derivative = function.getDerivative(var);
        log.info("Calculated derivative of [{}] for variable [{}]. Raw result: [{}]", function, var, derivative);
        Component simplifiedDerivative = ExpressionUtils.simplify(derivative);
        return simplifiedDerivative;
    }

    public static Component simplify(String expression) {
        Component parsedExpr = ExpressionParser.parse(expression);
        return simplify(parsedExpr);
    }

    public static Component simplify(Component component) {

        currentDepth++;
        updateMDC();

        try {
            int iteration = 0;
            boolean componentHasChanged;
            Component rewrittenComponent;
            Set<String> componentTransformationHistory = new HashSet<>();
            componentTransformationHistory.add(component.toString());

            do {
                updateMDC(++iteration);
                log.info("Simplifying [{}]...", component);
                componentHasChanged = false;
                String originalComponentAsString;
                for (Rule rule : Rules.rules) {
                    originalComponentAsString = component.toString();
                    rewrittenComponent = component.rewrite(rule);
                    final String rewrittenComponentAsString = rewrittenComponent.toString();
                    final boolean componentHasChangedByCurrentRule = !Objects.equals(rewrittenComponentAsString, originalComponentAsString);
                    if (componentHasChangedByCurrentRule) {
                        if (componentTransformationHistory.contains(rewrittenComponentAsString)) {
                            log.info("Loop detected with rewritten component [{}]: no more rules will be applied", rewrittenComponentAsString);
                            return rewrittenComponent;
                        } else {
                            componentTransformationHistory.add(rewrittenComponentAsString);
                        }
                    }
                    componentHasChanged |= componentHasChangedByCurrentRule;
                    component = rewrittenComponent;
                }
                log.info("Finished");
            } while (componentHasChanged);
            return component;
        } finally {
            removeLastExecutionFromMDC();
            lastFinishedExecutionByThread.get(Thread.currentThread().getName()).remove(currentDepth + 1);
            currentDepth--;
        }
    }

    private static void removeLastExecutionFromMDC() {
        final String executionChainInMDC = MDC.get(EXECUTION_CHAIN_MDC_PARAM);
        LinkedList<String> executionChain = new LinkedList<>(Arrays.asList(executionChainInMDC.split("->")));
        executionChain.removeLast();
        MDC.put(EXECUTION_CHAIN_MDC_PARAM, String.join("->", executionChain));
    }

    private static void updateMDC() {

        Map<Integer, String> executionsByDepth = lastFinishedExecutionByThread.computeIfAbsent(Thread.currentThread().getName(), k -> new HashMap<>());

        final String executionChainInMDC = MDC.get(EXECUTION_CHAIN_MDC_PARAM);

        final String lastExecutionForCurrentDepth = executionsByDepth.get(currentDepth);
        final String newExecutionForCurrentDepth;

        if (lastExecutionForCurrentDepth == null) {
            newExecutionForCurrentDepth = "1.0";
        } else {
            final String[] spiltLastExecution = lastExecutionForCurrentDepth.split("\\.");
            final String execution = spiltLastExecution[0];
            final String iteration = spiltLastExecution[1];
            newExecutionForCurrentDepth = String.valueOf(Integer.parseInt(execution) + 1).concat(".").concat(iteration);
        }

        executionsByDepth.put(currentDepth, newExecutionForCurrentDepth);

        if (isEmpty(executionChainInMDC)) {
            MDC.put(EXECUTION_CHAIN_MDC_PARAM, newExecutionForCurrentDepth);
        } else {
            MDC.put(EXECUTION_CHAIN_MDC_PARAM, executionChainInMDC + "->" + newExecutionForCurrentDepth);
        }

    }

    private static void updateMDC(int iteration) {
        String executionChainInMDC = MDC.get(EXECUTION_CHAIN_MDC_PARAM);
        executionChainInMDC = executionChainInMDC.replaceAll("\\.[0-9]+$", "." + iteration);
        MDC.put(EXECUTION_CHAIN_MDC_PARAM, executionChainInMDC);
    }

    public static Domain getDomain(Expression expression, Variable variable) {
        return simplify(expression).getDomain(variable);
    }

}
