package de.dhbw.mosbach.matchfield;

import com.fasterxml.jackson.annotation.*;
import de.dhbw.mosbach.matchfield.fields.Field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MatchField {

    final private List<List<Field>> fieldList;

    @JsonCreator
    public MatchField(@JsonProperty("fieldList") List<List<Field>> fieldList) {
        if (fieldList == null || fieldList.isEmpty() || fieldList.get(0) == null || fieldList.get(0).isEmpty()) {
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

    public Field getNeighbourTo(int x, int y, Direction direction) {
        try {
            switch (direction) {
                case UP:
                    return fieldList.get(x).get(y - 1);
                case RIGHT:
                    return fieldList.get(x + 1).get(y);
                case DOWN:
                    return fieldList.get(x).get(y + 1);
                case LEFT:
                    return fieldList.get(x - 1).get(y);
            }
        } catch (IndexOutOfBoundsException ie) {
            return null;
        }
        return null;
    }

    public List<Field> getFieldsToDirection(int x, int y, Direction direction) {
        int xChange = 0, yChange = 0;
        switch (direction) {
            case UP:
                yChange--;
                break;
            case RIGHT:
                xChange++;
                break;
            case DOWN:
                yChange++;
                break;
            case LEFT:
                xChange--;
                break;
        }
        int actX = (x + xChange), actY = (y + yChange);
        List<Field> returnList = new ArrayList<>();
        try {
            while (actX >= 0 && actY >= 0 && actX < getSize() && actY < getSize()) {
                returnList.add(fieldList.get(actX).get(actY));
                actX += xChange;
                actY += yChange;
            }
            return returnList;
        } catch (IndexOutOfBoundsException ie) {
            return Collections.EMPTY_LIST;
        }
    }

    @JsonIgnore
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

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("MatchField [");
        for (int x = 0; x < fieldList.size(); x++) {
            for (int y = 0; y < fieldList.get(x).size(); y++) {
                stringBuilder.append(fieldList.get(x).get(y));
            }
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchField that = (MatchField) o;
        return fieldList.equals(that.fieldList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldList);
    }
}
