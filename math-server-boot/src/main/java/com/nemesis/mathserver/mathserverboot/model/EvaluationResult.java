package com.nemesis.mathserver.mathserverboot.model;


import com.nemesis.mathcore.expressionsolver.components.Variable;
import com.nemesis.mathcore.expressionsolver.models.Domain;
import lombok.Data;

import java.util.Map;
import java.util.TreeSet;

@Data
public class EvaluationResult {

    String simplyfiedForm;
    String numericValue;
    Map<Variable, String> derivatives;
    TreeSet<String> roots;
    Map<Variable, Domain> domains;

    public void addDerivative(Variable variable, String latex) {
        derivatives.put(variable, latex);
    }

    public void addDomain(Variable variable, Domain domain) {
        domains.put(variable, domain);
    }
}
