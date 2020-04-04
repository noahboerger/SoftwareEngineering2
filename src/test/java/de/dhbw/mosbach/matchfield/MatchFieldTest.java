package de.dhbw.mosbach.matchfield;

import de.dhbw.mosbach.TestUtils;
import de.dhbw.mosbach.matchfield.fields.Field;
import de.dhbw.mosbach.matchfield.fields.StandardField;
import de.dhbw.mosbach.matchfield.utils.Direction;
import de.dhbw.mosbach.matchfield.utils.FieldIndex;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class MatchFieldTest {

    private MatchField unsolvedTestMatchField;
    private MatchField solvedTestMatchField;

    @Before
    public void setUp() {
        unsolvedTestMatchField = TestUtils.getUnsolvedTestMatchField();
        solvedTestMatchField = TestUtils.getSolvedTestMatchField();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createIllegalMatchFieldEmptyTest() {
        new MatchField(new ArrayList<>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createIllegalMatchFieldDimensionTest() {
        List<List<Field>> fieldList = new ArrayList<>();
        List<Field> column = new ArrayList<>();
        column.add(new StandardField());
        column.add(new StandardField());
        fieldList.add(column);
        new MatchField(fieldList);
    }

    @Test
    public void getFieldAtTest() {
        Assert.assertEquals(TestUtils.getUnsolvedTestFieldList().get(0).get(0), unsolvedTestMatchField.getFieldAt(new FieldIndex(0, 0)));
    }

    @Test
    public void getFieldAtUnresolvableIndexTest() {
        Assert.assertNull(unsolvedTestMatchField.getFieldAt(-1, 0));
    }

    @Test
    public void getAllFieldsTest() {
        Assert.assertEquals(TestUtils.getUnsolvedTestFieldList(), unsolvedTestMatchField.getAllFields());
    }

    @Test
    public void getNeighbourTest() {
        Assert.assertEquals(unsolvedTestMatchField.getFieldAt(0, 0), unsolvedTestMatchField.getNeighbourTo(unsolvedTestMatchField.getFieldAt(0, 1), Direction.UP));
    }

    @Test
    public void getNumberOfFieldsWithStateTest() {
        Assert.assertEquals(7, solvedTestMatchField.getFieldsWithState(Field.State.BLACK).size());
    }

    @Test
    public void getNumberOfFieldsNotWithStateTest() {
        Assert.assertEquals(18, solvedTestMatchField.getFieldsNotWithState(Field.State.BLACK).size());
    }

    @Test
    public void getAllNeighboursTest() {
        List<Field> neighbours = unsolvedTestMatchField.getAllNeighbours(unsolvedTestMatchField.getFieldAt(2, 4));
        Assert.assertTrue(neighbours.contains(unsolvedTestMatchField.getFieldAt(1, 4)));
        Assert.assertTrue(neighbours.contains(unsolvedTestMatchField.getFieldAt(2, 3)));
        Assert.assertTrue(neighbours.contains(unsolvedTestMatchField.getFieldAt(3, 4)));
        Assert.assertEquals(3, neighbours.size());
    }

    @Test
    public void getNeighbourNotExistingTest() {
        Assert.assertNull(unsolvedTestMatchField.getNeighbourTo(unsolvedTestMatchField.getFieldAt(0, 0), Direction.UP));
    }

    @Test
    public void getFieldsToDirectionTest() {
        List<Field> expectedFields = new ArrayList<>();
        expectedFields.add(unsolvedTestMatchField.getFieldAt(4, 2));
        expectedFields.add(unsolvedTestMatchField.getFieldAt(4, 3));
        expectedFields.add(unsolvedTestMatchField.getFieldAt(4, 4));
        Assert.assertEquals(expectedFields, unsolvedTestMatchField.getFieldsToDirection(unsolvedTestMatchField.getFieldAt(4, 1), Direction.DOWN));
    }

    @Test
    public void getFieldsToDirectionOutOfBoundTest() {
        Assert.assertTrue(unsolvedTestMatchField.getFieldsToDirection(new StandardField(), Direction.DOWN).isEmpty());
    }

    @Test
    public void deepCopyTest() {
        Assert.assertEquals(unsolvedTestMatchField, MatchField.deepCopy(unsolvedTestMatchField));
    }

    @Test
    public void getFieldIndexTest() {
        Assert.assertEquals(new FieldIndex(2, 2), unsolvedTestMatchField.getIndexOfField(unsolvedTestMatchField.getFieldAt(2, 2)));
        Assert.assertNull(unsolvedTestMatchField.getIndexOfField(new StandardField()));
    }
}
