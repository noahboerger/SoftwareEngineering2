package de.dhbw.mosbach.solve;

import de.dhbw.mosbach.TestUtils;
import de.dhbw.mosbach.matchfield.MatchField;
import de.dhbw.mosbach.matchfield.fields.Field;
import de.dhbw.mosbach.matchfield.fields.HintField;
import de.dhbw.mosbach.matchfield.fields.StandardField;
import de.dhbw.mosbach.matchfield.utils.Direction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class YajisanKazusanSolverTest {

    private YajisanKazusanSolver testSolver;

    @Before
    public void setUp() {
        testSolver = new YajisanKazusanSolver(new MatchField(TestUtils.getTestFieldList()));
    }
}
