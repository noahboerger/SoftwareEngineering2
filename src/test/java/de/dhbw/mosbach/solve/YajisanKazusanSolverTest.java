package de.dhbw.mosbach.solve;

import de.dhbw.mosbach.TestUtils;
import de.dhbw.mosbach.matchfield.MatchField;
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

    @Test(expected = IllegalStateException.class)
    public void solveUnsolvableMatchFieldTest() {
        List<List<Field>> fieldList = new ArrayList<>();
        List<Field> column1 = new ArrayList<>();
        List<Field> column2 = new ArrayList<>();

        column1.add(new HintField(Direction.LEFT, 2));
        column1.add(new StandardField());
        column2.add(new StandardField());
        column2.add(new HintField(Direction.RIGHT, 2));

        fieldList.add(column1);
        fieldList.add(column2);

        YajisanKazusanSolver solver = new YajisanKazusanSolver(new MatchField(fieldList));
        solver.getSolvedMatchField();
    }
}
