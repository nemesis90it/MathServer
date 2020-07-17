package com.nemesis.mathserver.mathserverboot.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nemesis.mathserver.mathserverboot.model.EvaluationResult;

import java.io.IOException;

public class EvaluationResultSerializer extends StdSerializer<EvaluationResult> {

    public EvaluationResultSerializer() {
        super(EvaluationResult.class);
    }

    public EvaluationResultSerializer(Class<EvaluationResult> t) {
        super(t);
    }

    @Override
    public void serialize(EvaluationResult evaluationResult, JsonGenerator jGen, SerializerProvider serializerProvider) throws IOException {
        jGen.writeStartObject();
        jGen.writeStringField("simplifiedForm", evaluationResult.getSimplifiedForm());
        jGen.writeStringField("numericValue", evaluationResult.getNumericValue());
        jGen.writeStringField("derivative", evaluationResult.getDerivative());
        jGen.writeStringField("roots", evaluationResult.getRoots().stream().reduce("", (r1, r2) -> r1 + ", " + r2));
        jGen.writeStringField("domain", evaluationResult.getDomain());
        jGen.writeEndObject();
    }

}
