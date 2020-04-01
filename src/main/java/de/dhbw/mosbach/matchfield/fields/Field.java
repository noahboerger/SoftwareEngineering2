package de.dhbw.mosbach.matchfield.fields;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = StandardField.class, name = "STANDARD"),
        @JsonSubTypes.Type(value = HintField.class, name = "HINT")})
public interface Field {

    State getFieldState();

    void setFieldState(State fieldState);

    Field deepCopy();

    static Field deepCopy(Field field) {
        return field.deepCopy();
    }

    enum State {
        UNKNOWN, WHITE, BLACK
    }
}
