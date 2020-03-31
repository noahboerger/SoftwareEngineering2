package de.dhbw.mosbach.matchfield.fields;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class AbstractField implements Field {

    protected FieldState fieldState;

    public AbstractField() {
        fieldState = FieldState.UNKNOWN;
    }

    @Override
    @JsonIgnore
    public FieldState getFieldState() {
        return fieldState;
    }

    @Override
    @JsonIgnore
    public void setFieldState(FieldState fieldState) {
        this.fieldState = fieldState;
    }

    @Override
    public String toString() {
        return "FieldState: " + fieldState;
    }
}
