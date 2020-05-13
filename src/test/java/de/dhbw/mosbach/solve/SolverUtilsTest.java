package de.dhbw.mosbach.solve;

import de.dhbw.mosbach.TestUtils;
import de.dhbw.mosbach.matchfield.MatchField;
import de.dhbw.mosbach.matchfield.fields.Field;
import de.dhbw.mosbach.matchfield.fields.HintField;
import de.dhbw.mosbach.matchfield.utils.Direction;
import de.dhbw.mosbach.matchfield.utils.FieldIndex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SolverUtilsTest {

    private MatchField testMatchField;

    @BeforeEach
    public void setUp() {
        testMatchField = new MatchField(TestUtils.getUnsolvedTestFieldList());
    }

    @Test
    public void calculateMaxPossibleBlackFieldsToDirectionTest() {
        Assertions.assertEquals(2, SolverUtils.calculateMaxPossibleBlackFieldsToDirection(testMatchField, testMatchField.getFieldAt(0, 4), Direction.RIGHT));

        testMatchField.getFieldAt(1, 4).setFieldState(Field.State.WHITE);
        testMatchField.getFieldAt(2, 4).setFieldState(Field.State.WHITE);
        testMatchField.getFieldAt(4, 4).setFieldState(Field.State.WHITE);

        Assertions.assertEquals(1, SolverUtils.calculateMaxPossibleBlackFieldsToDirection(testMatchField, testMatchField.getFieldAt(0, 4), Direction.RIGHT));
    }

    @Test
    public void getListOfPossibleSolutionsTest() {
        List<SolverUtils.BlackAndWhiteSolutionDTO> potentialSolutions = SolverUtils.getListOfPossibleSolutions(testMatchField, testMatchField.getFieldAt(2, 0), Direction.RIGHT);
        Assertions.assertEquals(5, potentialSolutions.size());

        testMatchField.getFieldAt(3, 0).setFieldState(Field.State.BLACK);
        List<SolverUtils.BlackAndWhiteSolutionDTO> potentialSolutionsWithBlackField = SolverUtils.getListOfPossibleSolutions(testMatchField, testMatchField.getFieldAt(2, 0), Direction.RIGHT);
        Assertions.assertEquals(1, potentialSolutionsWithBlackField.size());
        Assertions.assertEquals(2, potentialSolutionsWithBlackField.get(0).toBeWhitedFields.size());
        Assertions.assertEquals(0, potentialSolutionsWithBlackField.get(0).toBeBlackedFields.size());
        Assertions.assertEquals(new FieldIndex(2, 0), potentialSolutionsWithBlackField.get(0).toBeWhitedFields.pop());
        Assertions.assertEquals(new FieldIndex(4, 0), potentialSolutionsWithBlackField.get(0).toBeWhitedFields.pop());
    }

    @Test
    public void getBlackAndWhiteUseHintTest() {
        SolverUtils.BlackAndWhiteSolutionDTO potentialSolutions = SolverUtils.getBlackAndWhiteUseHint(testMatchField, (HintField) testMatchField.getFieldAt(0, 0));
        Assertions.assertNull(potentialSolutions);

        testMatchField.getFieldAt(3, 0).setFieldState(Field.State.BLACK);
        SolverUtils.BlackAndWhiteSolutionDTO potentialSolutionsWithBlackField = SolverUtils.getBlackAndWhiteUseHint(testMatchField, (HintField) testMatchField.getFieldAt(0, 0));
        Assertions.assertNotNull(potentialSolutionsWithBlackField);
        Assertions.assertEquals(2, potentialSolutionsWithBlackField.toBeWhitedFields.size());
        Assertions.assertEquals(1, potentialSolutionsWithBlackField.toBeBlackedFields.size());
        Assertions.assertEquals(new FieldIndex(1, 0), potentialSolutionsWithBlackField.toBeBlackedFields.peekFirst());
    }

    @Test
    public void canWhiteFieldsStillBeConnectedTest() {
        Assertions.assertTrue(SolverUtils.canOrAreWhiteFieldsStillBeConnected(testMatchField));
        testMatchField.getFieldAt(4, 3).setFieldState(Field.State.BLACK);
        testMatchField.getFieldAt(3, 3).setFieldState(Field.State.BLACK);
        testMatchField.getFieldAt(0, 1).setFieldState(Field.State.WHITE);
        Assertions.assertTrue(SolverUtils.canOrAreWhiteFieldsStillBeConnected(testMatchField));
        testMatchField.getFieldAt(2, 4).setFieldState(Field.State.BLACK);
        Assertions.assertFalse(SolverUtils.canOrAreWhiteFieldsStillBeConnected(testMatchField));
    }

    @Test
    public void areNoBlackFieldsConnectedTest() {
        Assertions.assertTrue(SolverUtils.areNoBlackFieldsConnected(testMatchField));
        testMatchField.getFieldAt(2, 4).setFieldState(Field.State.BLACK);
        testMatchField.getFieldAt(3, 3).setFieldState(Field.State.BLACK);
        testMatchField.getFieldAt(0, 1).setFieldState(Field.State.WHITE);
        Assertions.assertTrue(SolverUtils.areNoBlackFieldsConnected(testMatchField));
        testMatchField.getFieldAt(4, 3).setFieldState(Field.State.BLACK);
        Assertions.assertFalse(SolverUtils.areNoBlackFieldsConnected(testMatchField));
    }

    @Test
    public void canEveryWhiteHintFieldStillGetCorrectTest() {
        Assertions.assertTrue(SolverUtils.canOrIsEveryWhiteHintFieldStillGetCorrect(testMatchField));
        testMatchField.getFieldAt(0, 0).setFieldState(Field.State.WHITE);
        testMatchField.getFieldAt(4, 1).setFieldState(Field.State.WHITE);
        testMatchField.getFieldAt(1, 0).setFieldState(Field.State.BLACK);
        Assertions.assertTrue(SolverUtils.canOrIsEveryWhiteHintFieldStillGetCorrect(testMatchField));
        testMatchField.getFieldAt(4, 0).setFieldState(Field.State.BLACK);
        Assertions.assertFalse(SolverUtils.canOrIsEveryWhiteHintFieldStillGetCorrect(testMatchField));
    }

    @Test
    public void isSolvedCorrectlyTest() {
        Assertions.assertFalse(SolverUtils.isSolvedCorrectly(TestUtils.getUnsolvedTestMatchField()));
        Assertions.assertTrue(SolverUtils.isSolvedCorrectly(TestUtils.getSolvedTestMatchField()));
    }
}
