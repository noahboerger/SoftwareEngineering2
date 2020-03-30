package de.dhbw.mosbach.matchfield.fields;

public class StandardField extends AbstractField {

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
}
