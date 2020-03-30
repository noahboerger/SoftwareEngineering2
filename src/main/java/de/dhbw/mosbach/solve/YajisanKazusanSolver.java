package de.dhbw.mosbach.solve;

import de.dhbw.mosbach.matchfield.MatchField;
import de.dhbw.mosbach.matchfield.fields.FieldState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    private void solve() {
        for(int x = 0; x < solvedMatchField.getSize(); x++) {
            for(int y = 0; y < solvedMatchField.getSize(); y++) {
                FieldState randomState = new Random().nextBoolean() ? FieldState.WHITE: FieldState.BLACK;
                solvedMatchField.getFieldAt(x,y).setFieldState(randomState);
                solvingOrderList.add(new FieldIndex(x,y));
            }
        }
        System.out.println("Solving is not supported right now!!!");
    }
}
