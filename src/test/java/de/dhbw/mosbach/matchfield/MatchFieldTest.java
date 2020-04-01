package de.dhbw.mosbach.matchfield;

import de.dhbw.mosbach.matchfield.fields.Field;
import de.dhbw.mosbach.matchfield.fields.HintField;
import de.dhbw.mosbach.matchfield.fields.StandardField;
import de.dhbw.mosbach.matchfield.utils.Direction;
import de.dhbw.mosbach.matchfield.utils.FieldIndex;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class MatchFieldTest {

    private MatchField testMatchField;

    @Before
    public void setUp() {
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

        testMatchField = new MatchField(fieldList);
    }

    @Test
    public void getNeighbourTest() {
        Assert.assertEquals(testMatchField.getFieldAt(0, 0), testMatchField.getNeighbourTo(testMatchField.getFieldAt(0, 1), Direction.UP));
    }

    @Test
    public void getNeighbourNotExistingTest() {
        Assert.assertNull(testMatchField.getNeighbourTo(testMatchField.getFieldAt(0, 0), Direction.UP));
    }

    @Test
    public void getFieldsToDirectionTest() {
        List<Field> expectedFields = new ArrayList<>();
        expectedFields.add(testMatchField.getFieldAt(4, 2));
        expectedFields.add(testMatchField.getFieldAt(4, 3));
        expectedFields.add(testMatchField.getFieldAt(4, 4));
        Assert.assertEquals(expectedFields, testMatchField.getFieldsToDirection(testMatchField.getFieldAt(4, 1), Direction.DOWN));
    }

    @Test
    public void getFieldsToDirectionOutOfBoundTest() {
        Assert.assertTrue(testMatchField.getFieldsToDirection(new StandardField(), Direction.DOWN).isEmpty());
    }

    @Test
    public void deepCopyTest() {
        Assert.assertEquals(testMatchField, MatchField.deepCopy(testMatchField));
    }

    @Test
    public void getFieldIndexTest() {
        Assert.assertEquals(new FieldIndex(2, 2), testMatchField.getIndexOfField(testMatchField.getFieldAt(2, 2)));
        Assert.assertNull(testMatchField.getIndexOfField(new StandardField()));
    }
}
