package de.dhbw.mosbach.solve;

import de.dhbw.mosbach.matchfield.utils.Direction;
import de.dhbw.mosbach.matchfield.MatchField;
import de.dhbw.mosbach.matchfield.fields.Field;
import de.dhbw.mosbach.matchfield.fields.HintField;
import de.dhbw.mosbach.matchfield.utils.FieldIndex;

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
        this.solvedMatchField = MatchField.deepCopy(unsolvedMatchField);
    }

    public MatchField getUnsolvedMatchField() {
        return MatchField.deepCopy(unsolvedMatchField);
    }

    public MatchField getSolvedMatchField() {
        if (!isSolved) {
            solve();
            isSolved = true;
        }
        return MatchField.deepCopy(solvedMatchField);
    }

    public List<FieldIndex> getSolvingParsingOrder() {
        if (!isSolved) {
            solve();
            isSolved = true;
        }
        return List.copyOf(solvingOrderList);
    }

    private void solve() {
        for (int x = 0; x < solvedMatchField.getSize(); x++) {
            for (int y = 0; y < solvedMatchField.getSize(); y++) {
                Field.State randomState = new Random().nextBoolean() ? Field.State.WHITE : Field.State.BLACK;
                solvedMatchField.getFieldAt(x, y).setFieldState(randomState);
                solvingOrderList.add(new FieldIndex(x, y));
            }
        }
        //TODO
        System.out.println("Solving is not supported right now!!!");
    }

    private void setImpossibleFieldsToBlack() {
        //TODO
        System.out.println("NOT SUPPORTED SO FAR");
        throw new UnsupportedOperationException();
    }

    private void setNeighboursOfBlackFieldsToWhite() {
        List<List<Field>> allFields = solvedMatchField.getAllFields();
        for (List<Field> allField : allFields) {
            for (Field actField : allField) {
                if (!(actField instanceof HintField) || actField.getFieldState() != Field.State.BLACK) {
                    continue;
                }
                for (Direction directions : Direction.values()) {
                    Field actNeighbourField = solvedMatchField.getNeighbourTo(actField, directions);
                    if (actNeighbourField != null) {
                        setStateAndAddToSolution(actNeighbourField, Field.State.WHITE);
                    }
                }
            }
        }
    }

    private void setStateAndAddToSolution(Field field, Field.State fieldState) {
        field.setFieldState(fieldState);
        solvingOrderList.add(solvedMatchField.getIndexOfField(field));
    }
}
