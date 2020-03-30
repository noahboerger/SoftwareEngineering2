package de.dhbw.mosbach.solve;

import de.dhbw.mosbach.matchfield.MatchField;

import java.util.ArrayList;
import java.util.List;

public class YajisanKazusanSolver {

    private final MatchField unsolvedMatchField;

    private final MatchField solvedMatchField;
    boolean isSolved = false;
    private List<FieldIndex> solvingOrderList = new ArrayList<>();

    public YajisanKazusanSolver(MatchField unsolvedMatchField) {
        this.unsolvedMatchField = MatchField.deepCopy(unsolvedMatchField);
        this.solvedMatchField= MatchField.deepCopy(unsolvedMatchField);
    }

    public static  class FieldIndex {
        private final int x;
        private final int y;

        public FieldIndex(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    private void solve() {
        System.out.println("Solving is not supported right now!!!");
    }

    public MatchField getUnsolvedMatchField() {
        return MatchField.deepCopy(unsolvedMatchField);
    }

    public MatchField getSolvedMatchField() {
        if(!isSolved) {
            solve();
            isSolved = true;
        }
        return MatchField.deepCopy(solvedMatchField);
    }

    public List<FieldIndex> getSolvingParsingOrder() {
        if(!isSolved) {
            solve();
            isSolved = true;
        }
        return List.copyOf(solvingOrderList);
    }
}
