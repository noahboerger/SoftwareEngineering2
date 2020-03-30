package de.dhbw.mosbach.matchfield;

import de.dhbw.mosbach.matchfield.fields.Field;

import java.util.ArrayList;
import java.util.List;

public class MatchField {
    final private List<List<Field>> fieldList;

    public MatchField(List<List<Field>> fieldList) {
        if(fieldList == null || fieldList.isEmpty() || fieldList.get(0) == null || fieldList.get(0).isEmpty()) {
            throw new IllegalArgumentException();
        }
        int size = fieldList.size();
        for (List<Field> row : fieldList) {
            if (row.size() != size) {
                throw new IllegalArgumentException();
            }
        }
        this.fieldList = fieldList;
    }

    public Field getFieldAt(int x, int y) {
        return fieldList.get(x).get(y);
    }

    public int getSize() {
        return fieldList.size();
    }

    public static MatchField deepCopy(MatchField matchField) {
        List<List<Field>> copyFieldList = new ArrayList<>();
        for (List<Field> rowFields : matchField.fieldList) {
            List<Field> copyRowFields = new ArrayList<>();
            copyFieldList.add(copyRowFields);
            for (Field field : rowFields) {
                copyRowFields.add(Field.deepCopy(field));
            }
        }
        return new MatchField(copyFieldList);
    }
}
