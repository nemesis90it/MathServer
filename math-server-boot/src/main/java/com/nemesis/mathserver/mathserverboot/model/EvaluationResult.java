package com.nemesis.mathserver.mathserverboot.model;


import com.nemesis.mathcore.expressionsolver.models.Domain;
import lombok.Data;

import java.util.TreeSet;

@Data
public class EvaluationResult {

    String simplyfiedForm;
    String numericValue;
    String derivative;
    TreeSet<String> roots;
    Domain domain;

}
