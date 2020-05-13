package de.dhbw.mosbach.matchfield;

import de.dhbw.mosbach.TestUtils;
import de.dhbw.mosbach.matchfield.fields.Field;
import de.dhbw.mosbach.matchfield.fields.StandardField;
import de.dhbw.mosbach.matchfield.utils.Direction;
import de.dhbw.mosbach.matchfield.utils.FieldIndex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class MatchFieldTest {

    private MatchField unsolvedTestMatchField;
    private MatchField solvedTestMatchField;

    @BeforeEach
    public void setUp() {
        unsolvedTestMatchField = TestUtils.getUnsolvedTestMatchField();
        solvedTestMatchField = TestUtils.getSolvedTestMatchField();
    }

    @Test
    public void createIllegalMatchFieldEmptyTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new MatchField(new ArrayList<>()));
    }

    @Test
    public void createIllegalMatchFieldDimensionTest() {
        List<List<Field>> fieldList = new ArrayList<>();
        List<Field> column = new ArrayList<>();
        column.add(new StandardField());
        column.add(new StandardField());
        fieldList.add(column);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new MatchField(fieldList));
    }

    @Test
    public void getFieldAtTest() {
        Assertions.assertEquals(TestUtils.getUnsolvedTestFieldList().get(0).get(0), unsolvedTestMatchField.getFieldAt(new FieldIndex(0, 0)));
    }

    @Test
    public void getFieldAtUnresolvableIndexTest() {
        Assertions.assertNull(unsolvedTestMatchField.getFieldAt(-1, 0));
    }

    @Test
    public void getAllFieldsTest() {
        Assertions.assertEquals(TestUtils.getUnsolvedTestFieldList(), unsolvedTestMatchField.getAllFields());
    }

    @Test
    public void getNeighbourTest() {
        Assertions.assertEquals(unsolvedTestMatchField.getFieldAt(0, 0), unsolvedTestMatchField.getNeighbourTo(unsolvedTestMatchField.getFieldAt(0, 1), Direction.UP));
    }

    @Test
    public void getNumberOfFieldsWithStateTest() {
        Assertions.assertEquals(7, solvedTestMatchField.getFieldsWithState(Field.State.BLACK).size());
    }

    @Test
    public void getNumberOfFieldsNotWithStateTest() {
        Assertions.assertEquals(18, solvedTestMatchField.getFieldsNotWithState(Field.State.BLACK).size());
    }

    @Test
    public void getAllNeighboursTest() {
        List<Field> neighbours = unsolvedTestMatchField.getAllNeighbours(unsolvedTestMatchField.getFieldAt(2, 4));
        Assertions.assertTrue(neighbours.contains(unsolvedTestMatchField.getFieldAt(1, 4)));
        Assertions.assertTrue(neighbours.contains(unsolvedTestMatchField.getFieldAt(2, 3)));
        Assertions.assertTrue(neighbours.contains(unsolvedTestMatchField.getFieldAt(3, 4)));
        Assertions.assertEquals(3, neighbours.size());
    }

    @Test
    public void getNeighbourNotExistingTest() {
        Assertions.assertNull(unsolvedTestMatchField.getNeighbourTo(unsolvedTestMatchField.getFieldAt(0, 0), Direction.UP));
    }

    @Test
    public void getFieldsToDirectionTest() {
        List<Field> expectedFields = new ArrayList<>();
        expectedFields.add(unsolvedTestMatchField.getFieldAt(4, 2));
        expectedFields.add(unsolvedTestMatchField.getFieldAt(4, 3));
        expectedFields.add(unsolvedTestMatchField.getFieldAt(4, 4));
        Assertions.assertEquals(expectedFields, unsolvedTestMatchField.getFieldsToDirection(unsolvedTestMatchField.getFieldAt(4, 1), Direction.DOWN));
    }

    @Test
    public void getFieldsToDirectionOutOfBoundTest() {
        Assertions.assertTrue(unsolvedTestMatchField.getFieldsToDirection(new StandardField(), Direction.DOWN).isEmpty());
    }

    @Test
    public void deepCopyTest() {
        Assertions.assertEquals(unsolvedTestMatchField, MatchField.deepCopy(unsolvedTestMatchField));
    }

    @Test
    public void getFieldIndexTest() {
        Assertions.assertEquals(new FieldIndex(2, 2), unsolvedTestMatchField.getIndexOfField(unsolvedTestMatchField.getFieldAt(2, 2)));
        Assertions.assertNull(unsolvedTestMatchField.getIndexOfField(new StandardField()));
    }
}
