package com.nemesis.mathcore.expressionsolver.utils;

import com.nemesis.mathcore.expressionsolver.components.Component;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ParsingResult<T extends Component> {
    private T component;
    private Integer parsedChars;
}
