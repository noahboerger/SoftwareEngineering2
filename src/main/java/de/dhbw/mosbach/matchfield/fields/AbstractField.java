package de.dhbw.mosbach.matchfield.fields;

import com.fasterxml.jackson.annotation.JsonIgnore;

abstract class AbstractField implements Field {

    protected State fieldState;

    public AbstractField() {
        fieldState = State.UNKNOWN;
    }

    @Override
    @JsonIgnore
    public State getFieldState() {
        return fieldState;
    }

    @Override
    @JsonIgnore
    public void setFieldState(final State fieldState) {
        this.fieldState = fieldState;
    }

    @Override
    public String toString() {
        return "FieldState: " + fieldState;
    }
}
