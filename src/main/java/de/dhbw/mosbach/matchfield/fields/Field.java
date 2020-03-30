package de.dhbw.mosbach.matchfield.fields;

public interface Field {

    FieldState getFieldState();

    void setFieldState(FieldState fieldState);

    Field deepCopy();

    public static Field deepCopy(Field field) {
        return field.deepCopy();
    }
}
