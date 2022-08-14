package com.nemesis.mathserver.mathserverboot.model;


import com.nemesis.mathcore.expressionsolver.components.Variable;
import com.nemesis.mathcore.expressionsolver.models.Input;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
public class InputParsingResult {
    private Input parsingResult;
    private Set<Variable> variables;

}
