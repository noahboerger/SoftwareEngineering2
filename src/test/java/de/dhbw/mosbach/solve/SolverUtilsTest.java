package de.dhbw.mosbach.solve;

import de.dhbw.mosbach.TestUtils;
import de.dhbw.mosbach.matchfield.MatchField;
import de.dhbw.mosbach.matchfield.utils.Direction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SolverUtilsTest {

    private MatchField testMatchField;

    @Before
    public void setUp() {
        testMatchField = new MatchField(TestUtils.getTestFieldList());
    }

    @Test
    public void calculateMaxPossibleBlackFieldsToDirectionTest() {
        final int calculatedSolution = SolverUtils.calculateMaxPossibleBlackFieldsToDirection(testMatchField, testMatchField.getFieldAt(0,4), Direction.RIGHT);
        Assert.assertEquals(2, calculatedSolution);
    }
}
