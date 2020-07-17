package com.nemesis.mathserver.mathserverboot.model;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nemesis.mathserver.mathserverboot.serializer.EvaluationResultSerializer;
import lombok.Data;

import java.util.TreeSet;

@Data
@JsonSerialize(using = EvaluationResultSerializer.class)
public class EvaluationResult {

    private String simplifiedForm;
    private String numericValue;
    private String derivative;
    private TreeSet<String> roots = new TreeSet<>();
    private String domain;

}
