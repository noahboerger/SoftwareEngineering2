package de.dhbw.mosbach.matchfield;

import de.dhbw.mosbach.matchfield.fields.Field;

import java.util.List;

public class MatchField {
    final List<List<Field>> parsedField;

    public MatchField(List<List<Field>> parsedField) {
        this.parsedField = parsedField;
    }
}
