package de.dhbw.mosbach.solve;

import de.dhbw.mosbach.TestUtils;
import de.dhbw.mosbach.matchfield.MatchField;
import de.dhbw.mosbach.matchfield.utils.FieldIndex;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class YajisanKazusanSolverTest {

    private YajisanKazusanSolver testSolver;
    private MatchField unsolvedTestMatchField;
    private MatchField solvedTestMatchField;

    @Before
    public void setUp() {
        unsolvedTestMatchField = TestUtils.getUnsolvedTestMatchField();
        solvedTestMatchField = TestUtils.getSolvedTestMatchField();
        testSolver = new YajisanKazusanSolver(unsolvedTestMatchField);
    }

    @Test
    public void getSolvedMatchFieldTest() {
        Assert.assertEquals(solvedTestMatchField, testSolver.getSolvedMatchField());
    }

    @Test
    public void getUnsolvedMatchFieldTest() {
        Assert.assertEquals(unsolvedTestMatchField, testSolver.getUnsolvedMatchField());
    }

    @Test
    public void getSolvingOrderTest() {
        List<FieldIndex> solvingOrder = testSolver.getSolvingOrder();

        for (int x = 0; x < unsolvedTestMatchField.getEdgeSize(); x++) {
            for (int y = 0; y < unsolvedTestMatchField.getEdgeSize(); y++) {
                Assert.assertTrue(solvingOrder.contains(new FieldIndex(x, y)));
            }
        }
    }
}
