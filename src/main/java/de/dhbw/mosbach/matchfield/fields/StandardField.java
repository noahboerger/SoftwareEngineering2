package de.dhbw.mosbach.matchfield.fields;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Objects;

public class StandardField extends AbstractField {

    @JsonCreator
    public StandardField() {
        super();
    }

    @Override
    public String toString() {
        return "StandardField [" + super.toString() + "]";
    }

    @Override
    public Field deepCopy() {
        StandardField copy = new StandardField();
        copy.setFieldState(this.fieldState);
        return copy;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldState);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StandardField) {
            StandardField standardField = (StandardField) obj;
            return super.fieldState == standardField.fieldState;
        }
        return false;
    }
}
