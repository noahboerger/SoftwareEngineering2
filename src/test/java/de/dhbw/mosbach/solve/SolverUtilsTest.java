package de.dhbw.mosbach.solve;

import de.dhbw.mosbach.TestUtils;
import de.dhbw.mosbach.matchfield.MatchField;
import de.dhbw.mosbach.matchfield.fields.Field;
import de.dhbw.mosbach.matchfield.fields.HintField;
import de.dhbw.mosbach.matchfield.utils.Direction;
import de.dhbw.mosbach.matchfield.utils.FieldIndex;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class SolverUtilsTest {

    private MatchField testMatchField;

    @Before
    public void setUp() {
        testMatchField = new MatchField(TestUtils.getUnsolvedTestFieldList());
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
        List<SolverUtils.BlackAndWhiteSolutionDTO> potentialSolutions = SolverUtils.getListOfPossibleSolutions(testMatchField, testMatchField.getFieldAt(2, 0), Direction.RIGHT);
        Assert.assertEquals(5, potentialSolutions.size());

        testMatchField.getFieldAt(3, 0).setFieldState(Field.State.BLACK);
        List<SolverUtils.BlackAndWhiteSolutionDTO> potentialSolutionsWithBlackField = SolverUtils.getListOfPossibleSolutions(testMatchField, testMatchField.getFieldAt(2, 0), Direction.RIGHT);
        Assert.assertEquals(1, potentialSolutionsWithBlackField.size());
        Assert.assertEquals(2, potentialSolutionsWithBlackField.get(0).toBeWhitedFields.size());
        Assert.assertEquals(0, potentialSolutionsWithBlackField.get(0).toBeBlackedFields.size());
        Assert.assertEquals(new FieldIndex(2, 0), potentialSolutionsWithBlackField.get(0).toBeWhitedFields.pop());
        Assert.assertEquals(new FieldIndex(4, 0), potentialSolutionsWithBlackField.get(0).toBeWhitedFields.pop());
    }

    @Test
    public void getBlackAndWhiteUseHintTest() {
        SolverUtils.BlackAndWhiteSolutionDTO potentialSolutions = SolverUtils.getBlackAndWhiteUseHint(testMatchField, (HintField) testMatchField.getFieldAt(0, 0));
        Assert.assertNull(potentialSolutions);

        testMatchField.getFieldAt(3, 0).setFieldState(Field.State.BLACK);
        SolverUtils.BlackAndWhiteSolutionDTO potentialSolutionsWithBlackField = SolverUtils.getBlackAndWhiteUseHint(testMatchField, (HintField) testMatchField.getFieldAt(0, 0));
        Assert.assertNotNull(potentialSolutionsWithBlackField);
        Assert.assertEquals(2, potentialSolutionsWithBlackField.toBeWhitedFields.size());
        Assert.assertEquals(1, potentialSolutionsWithBlackField.toBeBlackedFields.size());
        Assert.assertEquals(new FieldIndex(1, 0), potentialSolutionsWithBlackField.toBeBlackedFields.get(0));
    }

    @Test
    public void canWhiteFieldsStillBeConnectedTest() {
        Assert.assertTrue(SolverUtils.canOrAreWhiteFieldsStillBeConnected(testMatchField));
        testMatchField.getFieldAt(4, 3).setFieldState(Field.State.BLACK);
        testMatchField.getFieldAt(3, 3).setFieldState(Field.State.BLACK);
        testMatchField.getFieldAt(0, 1).setFieldState(Field.State.WHITE);
        Assert.assertTrue(SolverUtils.canOrAreWhiteFieldsStillBeConnected(testMatchField));
        testMatchField.getFieldAt(2, 4).setFieldState(Field.State.BLACK);
        Assert.assertFalse(SolverUtils.canOrAreWhiteFieldsStillBeConnected(testMatchField));
    }

    @Test
    public void areNoBlackFieldsConnectedTest() {
        Assert.assertTrue(SolverUtils.areNoBlackFieldsConnected(testMatchField));
        testMatchField.getFieldAt(2, 4).setFieldState(Field.State.BLACK);
        testMatchField.getFieldAt(3, 3).setFieldState(Field.State.BLACK);
        testMatchField.getFieldAt(0, 1).setFieldState(Field.State.WHITE);
        Assert.assertTrue(SolverUtils.areNoBlackFieldsConnected(testMatchField));
        testMatchField.getFieldAt(4, 3).setFieldState(Field.State.BLACK);
        Assert.assertFalse(SolverUtils.areNoBlackFieldsConnected(testMatchField));
    }

    @Test
    public void canEveryWhiteHintFieldStillGetCorrectTest() {
        Assert.assertTrue(SolverUtils.canOrIsEveryWhiteHintFieldStillGetCorrect(testMatchField));
        testMatchField.getFieldAt(0, 0).setFieldState(Field.State.WHITE);
        testMatchField.getFieldAt(4, 1).setFieldState(Field.State.WHITE);
        testMatchField.getFieldAt(1, 0).setFieldState(Field.State.BLACK);
        Assert.assertTrue(SolverUtils.canOrIsEveryWhiteHintFieldStillGetCorrect(testMatchField));
        testMatchField.getFieldAt(4, 0).setFieldState(Field.State.BLACK);
        Assert.assertFalse(SolverUtils.canOrIsEveryWhiteHintFieldStillGetCorrect(testMatchField));
    }

    @Test
    public void isSolvedCorrectlyTest() {
        Assert.assertFalse(SolverUtils.isSolvedCorrectly(TestUtils.getUnsolvedTestMatchField()));
        Assert.assertTrue(SolverUtils.isSolvedCorrectly(TestUtils.getSolvedTestMatchField()));
    }
}
