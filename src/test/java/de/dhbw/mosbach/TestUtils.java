package de.dhbw.mosbach;

import de.dhbw.mosbach.matchfield.fields.Field;
import de.dhbw.mosbach.matchfield.fields.HintField;
import de.dhbw.mosbach.matchfield.fields.StandardField;
import de.dhbw.mosbach.matchfield.utils.Direction;

import java.util.ArrayList;
import java.util.List;

final public class TestUtils {
    private TestUtils() {
        throw new IllegalStateException("Uninstanzierbare Klasse");
    }

    public static List<List<Field>> getTestFieldList() {
        List<List<Field>> fieldList = new ArrayList<>();

        List<Field> column0 = new ArrayList<>();
        List<Field> column1 = new ArrayList<>();
        List<Field> column2 = new ArrayList<>();
        List<Field> column3 = new ArrayList<>();
        List<Field> column4 = new ArrayList<>();

        column0.add(new HintField(Direction.RIGHT, 2));
        column0.add(new StandardField());
        column0.add(new StandardField());
        column0.add(new HintField(Direction.UP, 1));
        column0.add(new HintField(Direction.RIGHT, 3));

        column1.add(new HintField(Direction.RIGHT, 2));
        column1.add(new StandardField());
        column1.add(new HintField(Direction.RIGHT, 1));
        column1.add(new StandardField());
        column1.add(new StandardField());

        column2.add(new StandardField());
        column2.add(new HintField(Direction.DOWN, 1));
        column2.add(new StandardField());
        column2.add(new HintField(Direction.UP, 0));
        column2.add(new StandardField());

        column3.add(new StandardField());
        column3.add(new StandardField());
        column3.add(new HintField(Direction.LEFT, 2));
        column3.add(new StandardField());
        column3.add(new HintField(Direction.LEFT, 1));

        column4.add(new HintField(Direction.DOWN, 1));
        column4.add(new HintField(Direction.UP, 0));
        column4.add(new StandardField());
        column4.add(new StandardField());
        column4.add(new HintField(Direction.LEFT, 2));

        fieldList.add(column0);
        fieldList.add(column1);
        fieldList.add(column2);
        fieldList.add(column3);
        fieldList.add(column4);

        return fieldList;
    }
}
