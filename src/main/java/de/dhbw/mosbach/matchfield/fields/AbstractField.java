package de.dhbw.mosbach.matchfield.fields;

public abstract class AbstractField implements Field {

    protected FieldState fieldState;

    public AbstractField() {
        fieldState = FieldState.UNKNOWN;
    }

    @Override
    public FieldState getFieldState() {
        return fieldState;
    }

    @Override
    public void setFieldState(FieldState fieldState) {
        this.fieldState = fieldState;
    }

    @Override
    public String toString() {
        return "FieldState: " + fieldState;
    }
}
