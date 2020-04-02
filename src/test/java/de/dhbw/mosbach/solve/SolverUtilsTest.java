package de.dhbw.mosbach.solve;

import de.dhbw.mosbach.TestUtils;
import de.dhbw.mosbach.matchfield.MatchField;
import de.dhbw.mosbach.matchfield.fields.Field;
import de.dhbw.mosbach.matchfield.fields.HintField;
import de.dhbw.mosbach.matchfield.utils.Direction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class SolverUtilsTest {

    private MatchField testMatchField;

    @Before
    public void setUp() {
        testMatchField = new MatchField(TestUtils.getTestFieldList());
    }

    @Test
    public void calculateMaxPossibleBlackFieldsToDirectionTest() {
        Assert.assertEquals(2, SolverUtils.calculateMaxPossibleBlackFieldsToDirection(testMatchField, testMatchField.getFieldAt(0, 4), Direction.RIGHT));

        testMatchField.getFieldAt(1, 4).setFieldState(Field.State.WHITE);
        testMatchField.getFieldAt(2, 4).setFieldState(Field.State.WHITE);
        testMatchField.getFieldAt(4, 4).setFieldState(Field.State.WHITE);

        Assert.assertEquals(1, SolverUtils.calculateMaxPossibleBlackFieldsToDirection(testMatchField, testMatchField.getFieldAt(0, 4), Direction.RIGHT));
    }

    @Test
    public void getListOfPossibleSolutionsTest() {
        List<SolverUtils.BlackAndWhiteSolution> potentialSolutions = SolverUtils.getListOfPossibleSolutions(testMatchField, testMatchField.getFieldAt(2, 0), Direction.RIGHT);
        Assert.assertEquals(8, potentialSolutions.size());

        testMatchField.getFieldAt(3, 0).setFieldState(Field.State.BLACK);
        List<SolverUtils.BlackAndWhiteSolution> potentialSolutionsWithBlackField = SolverUtils.getListOfPossibleSolutions(testMatchField, testMatchField.getFieldAt(2, 0), Direction.RIGHT);
        Assert.assertEquals(1, potentialSolutionsWithBlackField.size());
        Assert.assertEquals(2, potentialSolutionsWithBlackField.get(0).toBeWhitedFields.size());
        Assert.assertEquals(0, potentialSolutionsWithBlackField.get(0).toBeBlackedFields.size());
        Assert.assertEquals(testMatchField.getFieldAt(2, 0), potentialSolutionsWithBlackField.get(0).toBeWhitedFields.get(0));
        Assert.assertEquals(testMatchField.getFieldAt(4, 0), potentialSolutionsWithBlackField.get(0).toBeWhitedFields.get(1));
    }

    @Test
    public void getBlackAndWhiteUseHintTest() {
        SolverUtils.BlackAndWhiteSolution potentialSolutions = SolverUtils.getBlackAndWhiteUseHint(testMatchField, (HintField) testMatchField.getFieldAt(0, 0));
        Assert.assertNull(potentialSolutions);

        testMatchField.getFieldAt(3, 0).setFieldState(Field.State.BLACK);
        SolverUtils.BlackAndWhiteSolution potentialSolutionsWithBlackField = SolverUtils.getBlackAndWhiteUseHint(testMatchField, (HintField) testMatchField.getFieldAt(0, 0));
        Assert.assertNotNull(potentialSolutionsWithBlackField);
        Assert.assertEquals(2, potentialSolutionsWithBlackField.toBeWhitedFields.size());
        Assert.assertEquals(1, potentialSolutionsWithBlackField.toBeBlackedFields.size());
        Assert.assertEquals(testMatchField.getFieldAt(1, 0), potentialSolutionsWithBlackField.toBeBlackedFields.get(0));
    }

    @Test
    public void isDefinitelyUnableToBeSolvedAnyMoreTest() {
        Assert.assertFalse(SolverUtils.isDefinitelyUnableToBeSolvedAnyMore(testMatchField));
        testMatchField.getFieldAt(4,3).setFieldState(Field.State.BLACK);
        testMatchField.getFieldAt(3,3).setFieldState(Field.State.BLACK);
        testMatchField.getFieldAt(0,1).setFieldState(Field.State.WHITE);
        Assert.assertFalse(SolverUtils.isDefinitelyUnableToBeSolvedAnyMore(testMatchField));
        testMatchField.getFieldAt(2,4).setFieldState(Field.State.BLACK);
        Assert.assertTrue(SolverUtils.isDefinitelyUnableToBeSolvedAnyMore(testMatchField));
    }
}
