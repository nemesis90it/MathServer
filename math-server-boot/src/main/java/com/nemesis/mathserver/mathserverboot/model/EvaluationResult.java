package com.nemesis.mathserver.mathserverboot.model;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nemesis.mathserver.mathserverboot.serializer.EvaluationResultSerializer;
import lombok.Data;

@Data
@JsonSerialize(using = EvaluationResultSerializer.class)
public class EvaluationResult {

    private String input;
    private String simplifiedForm;
    private String numericValue;
    private String derivative;
    private String roots;
    private String domain;

}
