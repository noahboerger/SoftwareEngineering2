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
        /*for (int x = 0; x < solvedMatchField.getSize(); x++) {
            for (int y = 0; y < solvedMatchField.getSize(); y++) {
                Field.State randomState = new Random().nextBoolean() ? Field.State.WHITE : Field.State.BLACK;
                solvedMatchField.getFieldAt(x, y).setFieldState(randomState);
                solvingOrderList.add(new FieldIndex(x, y));
            }
        }*/
        setImpossibleHintFieldsToBlack();
        //TODO
        System.out.println("Solving is not supported right now!!!");
    }

    private void setImpossibleHintFieldsToBlack() {
        for (List<Field> allField : solvedMatchField.getAllFields()) {
            for (Field actField : allField) {
                if (actField.getFieldState() != Field.State.UNKNOWN || !(actField instanceof HintField)) {
                    continue;
                }
                HintField actHintField = (HintField) actField;
                final int maxPossibleBlackFields = SolverUtils.calculateMaxPossibleBlackFieldsToDirection(solvedMatchField, actHintField, actHintField.getArrowDirection());
                if (actHintField.getAmount() > maxPossibleBlackFields) {
                    setStateAndAddToSolution(actHintField, Field.State.BLACK);
                }
            }
        }
    }

    private void useHintsOfWhiteHintFields() {
        for (List<Field> allField : solvedMatchField.getAllFields()) {
            for (Field actField : allField) {
                if (actField.getFieldState() != Field.State.WHITE || !(actField instanceof HintField)) {
                    continue;
                }
                //List<Field> blackFields = SolverUtils.getBlackFieldsOfPotentialCompletedRow(); TODO
            }
        }
    }

    private void setStateAndAddToSolution(Field field, Field.State fieldState) {
        //State setzen
        field.setFieldState(fieldState);
        solvingOrderList.add(solvedMatchField.getIndexOfField(field));
        //Wenn State schwarz alle Nachbarn weiß setzen
        if (fieldState == Field.State.BLACK) {
            for (Direction directions : Direction.values()) {
                Field actNeighbourField = solvedMatchField.getNeighbourTo(field, directions);
                if (actNeighbourField != null && actNeighbourField.getFieldState() == Field.State.UNKNOWN) {
                    actNeighbourField.setFieldState(Field.State.WHITE);
                    solvingOrderList.add(solvedMatchField.getIndexOfField(actNeighbourField));
                }
            }
        }
    }
}
