package com.nemesis.mathserver.ui.js;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class JsFunctions {

    public static final String insertTextJsFunction;

    static {
        try {
            insertTextJsFunction = IOUtils.toString(
                    Objects.requireNonNull(JsFunctions.class.getClassLoader().getResourceAsStream("js/insertTextAtCursor.js")),
                    StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
