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
    public MatchField(@JsonProperty("fieldList") final List<List<Field>> fieldList) {
        if (fieldList == null || fieldList.isEmpty() || fieldList.get(0) == null || fieldList.get(0).isEmpty()) {
            throw new IllegalArgumentException();
        }
        final int size = fieldList.size();
        for (final List<Field> row : fieldList) {
            if (row.size() != size) {
                throw new IllegalArgumentException();
            }
        }
        this.fieldList = fieldList;
    }

    public static MatchField deepCopy(final MatchField matchField) {
        final List<List<Field>> copyFieldList = new ArrayList<>();
        for (final List<Field> rowFields : matchField.fieldList) {
            final List<Field> copyRowFields = new ArrayList<>();
            copyFieldList.add(copyRowFields);
            for (final Field field : rowFields) {
                copyRowFields.add(Field.deepCopy(field));
            }
        }
        return new MatchField(copyFieldList);
    }

    public Field getFieldAt(final FieldIndex index) {
        return getFieldAt(index.getX(), index.getY());
    }

    public Field getFieldAt(final int x, final int y) {
        if (isIndexUnreachable(x, y)) {
            return null;
        }
        return fieldList.get(x).get(y);
    }

    public Field getNeighbourTo(final Field field, final Direction direction) {
        final FieldIndex index = getIndexOfField(field);
        if (index == null) {
            return null;
        }
        int x = index.getX();
        int y = index.getY();
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

    public List<Field> getFieldsToDirection(final Field field, final Direction direction) {
        final FieldIndex index = getIndexOfField(field);
        if (index == null) {
            return Collections.emptyList();
        }
        int xChange = 0;
        int yChange = 0;
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
        int actX = index.getX() + xChange;
        int actY = index.getY() + yChange;
        final List<Field> returnList = new ArrayList<>();
        while (actX >= 0 && actY >= 0 && actX < getEdgeSize() && actY < getEdgeSize()) {
            returnList.add(fieldList.get(actX).get(actY));
            actX += xChange;
            actY += yChange;
        }
        return returnList;
    }

    //List-Referenzen bleiben unveränderlich aber Referenzen auf Felder werden zurückgegeben
    public List<List<Field>> getAllFields() {
        final List<List<Field>> copyList = new ArrayList<>();
        for (final List<Field> actFieldList : fieldList) {
            copyList.add(List.copyOf(actFieldList));
        }
        return copyList;
    }

    public FieldIndex getIndexOfField(final Field field) {
        final int size = getEdgeSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (fieldList.get(x).get(y) == field) {
                    return new FieldIndex(x, y);
                }
            }
        }
        return null;
    }

    public List<Field> getAllNeighbours(final Field field) {
        return Arrays.stream(Direction.values())
                .map(direction -> getNeighbourTo(field, direction))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Field> getFieldsWithState(final Field.State state) {
        return fieldList.stream()
                .flatMap(Collection::stream)
                .filter(field -> field.getFieldState() == state)
                .collect(Collectors.toList());
    }

    public List<Field> getFieldsNotWithState(final Field.State notState) {
        return fieldList.stream()
                .flatMap(Collection::stream)
                .filter(field -> field.getFieldState() != notState)
                .collect(Collectors.toList());
    }

    private boolean isIndexUnreachable(final int x, final int y) {
        final int size = getEdgeSize();
        return x < 0 || y < 0 || x >= size || y >= size;
    }

    @JsonIgnore
    public int getEdgeSize() {
        return fieldList.size();
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("MatchField [");
        for (final List<Field> fields : fieldList) {
            for (final Field field : fields) {
                stringBuilder.append(field);
            }
        }
        stringBuilder.append(']');
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MatchField that = (MatchField) o;
        return fieldList.equals(that.fieldList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldList);
    }
}