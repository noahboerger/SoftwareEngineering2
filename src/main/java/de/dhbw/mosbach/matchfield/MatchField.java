package de.dhbw.mosbach.matchfield;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.dhbw.mosbach.matchfield.fields.Field;
import de.dhbw.mosbach.matchfield.utils.Direction;
import de.dhbw.mosbach.matchfield.utils.FieldIndex;

import java.util.*;
import java.util.stream.Collectors;

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

    public Field getFieldAt(FieldIndex index) {
        return getFieldAt(index.getX(), index.getY());
    }

    public Field getFieldAt(int x, int y) {
        if (isIndexUnreachable(x, y)) {
            return null;
        }
        return fieldList.get(x).get(y);
    }

    public Field getNeighbourTo(Field field, Direction direction) {
        FieldIndex index = getIndexOfField(field);
        if (index == null) {
            return null;
        }
        int x = index.getX(), y = index.getY();
        switch (direction) {
            case UP:
                y--;
                break;
            case RIGHT:
                x++;
                break;
            case DOWN:
                y++;
                break;
            case LEFT:
                x--;
                break;
        }
        if (isIndexUnreachable(x, y)) {
            return null;
        }
        return fieldList.get(x).get(y);
    }

    public List<Field> getFieldsToDirection(Field field, Direction direction) {
        FieldIndex index = getIndexOfField(field);
        if (index == null) {
            return Collections.emptyList();
        }
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
        int actX = (index.getX() + xChange), actY = (index.getY() + yChange);
        List<Field> returnList = new ArrayList<>();
        while (actX >= 0 && actY >= 0 && actX < getSize() && actY < getSize()) {
            returnList.add(fieldList.get(actX).get(actY));
            actX += xChange;
            actY += yChange;
        }
        return returnList;
    }

    //List-Referenzen bleiben unveränderlich aber Referenzen auf Felder werden zurückgegeben
    public List<List<Field>> getAllFields() {
        List<List<Field>> copyList = new ArrayList<>();
        for (List<Field> actFieldList : fieldList) {
            copyList.add(List.copyOf(actFieldList));
        }
        return copyList;
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

    public FieldIndex getIndexOfField(Field field) {
        final int size = getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Field actField = fieldList.get(x).get(y);
                if (actField == field) {
                    return new FieldIndex(x, y);
                }
            }
        }
        return null;
    }

    public List<Field> getAllNeighbours(Field field) {
        return Arrays.stream(Direction.values()).map(direction -> getNeighbourTo(field, direction)).filter( x -> x!= null).collect(Collectors.toList());
    }

    private boolean isIndexUnreachable(int x, int y) {
        final int size = getSize();
        return x < 0 || y < 0 || x >= size || y >= size;
    }

    @JsonIgnore
    public int getSize() {
        return fieldList.size();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("MatchField [");
        for (List<Field> fields : fieldList) {
            for (Field field : fields) {
                stringBuilder.append(field);
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