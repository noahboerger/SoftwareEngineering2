package de.dhbw.mosbach.solve;

import de.dhbw.mosbach.matchfield.utils.Direction;
import de.dhbw.mosbach.matchfield.MatchField;
import de.dhbw.mosbach.matchfield.fields.Field;
import de.dhbw.mosbach.matchfield.fields.HintField;
import de.dhbw.mosbach.matchfield.utils.FieldIndex;

import java.util.*;
import java.util.stream.Collectors;

public class YajisanKazusanSolver {

    private final MatchField unsolvedMatchField;

    private final MatchField solvedMatchField;
    private boolean isSolved = false;

    private Set<FieldIndex> alreadySolvedFieldIndexes = new HashSet<>();
    private List<FieldIndex> solvingOrderList = new ArrayList<>();

    Stack<FieldIndex> backtrackingStack = new Stack<>();
    Set<FieldIndex> potentialMustBeWhiteStack = new HashSet<>();

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

    public List<FieldIndex> getSolvingOrder() {
        if (!isSolved) {
            solve();
            isSolved = true;
        }
        return List.copyOf(solvingOrderList);
    }

    private void solve() {
        while (!SolverUtils.isSolvedCorrectly(solvedMatchField)) {
            while (!SolverUtils.isDefinitelyUnableToBeSolvedAnyMore(solvedMatchField) && !SolverUtils.isSolvedCorrectly(solvedMatchField)) {
                int blackAndWhitesBefore;
                do {
                    blackAndWhitesBefore = solvedMatchField.getFieldsNotWithState(Field.State.UNKNOWN).size();
                    setImpossibleHintFieldsToBlack();
                    useHintsOfWhiteHintFields();
                    processPotentialWhiteFieldsForConnectedShape();
                } while (solvedMatchField.getFieldsNotWithState(Field.State.UNKNOWN).size() != blackAndWhitesBefore);
                doEducatedGuess();
            }
            doBacktracking();
        }
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
                SolverUtils.BlackAndWhiteSolutionDTO solution = SolverUtils.getBlackAndWhiteUseHint(solvedMatchField, (HintField) actField);
                if (solution == null) {
                    continue;
                }
                for (Field whiteField : solution.toBeWhitedFields) {
                    setStateAndAddToSolution(whiteField, Field.State.WHITE);
                }
                for (Field blackField : solution.toBeBlackedFields) {
                    setStateAndAddToSolution(blackField, Field.State.BLACK);
                }
            }
        }
    }

    private void processPotentialWhiteFieldsForConnectedShape() {
        for (FieldIndex actFieldIndex : potentialMustBeWhiteStack) {
            Field actField = solvedMatchField.getFieldAt(actFieldIndex);
            if(actField == null) {
                continue;
            }
            if (actField.getFieldState() == Field.State.UNKNOWN) {
                actField.setFieldState(Field.State.BLACK);
                if (SolverUtils.canOrAreWhiteFieldsStillBeConnected(solvedMatchField)) {
                    actField.setFieldState(Field.State.UNKNOWN);
                } else {
                    setStateAndAddToSolution(actField, Field.State.WHITE);
                }
            }
        }
        potentialMustBeWhiteStack.clear();
    }

    private void doEducatedGuess() {
        Field guessBlackField = SolverUtils.findPotentialBestBlackGuessHintField(solvedMatchField);
        if (guessBlackField == null) {
            guessBlackField = SolverUtils.findPotentialBestBlackGuessStandardField(solvedMatchField);
        }
        if (guessBlackField == null) {
            guessBlackField = SolverUtils.findFirstFieldWithState(solvedMatchField, Field.State.UNKNOWN);
        }
        if (guessBlackField == null) {
            return;
        }
        backtrackingStack.push(solvedMatchField.getIndexOfField(guessBlackField));
        setStateAndAddToSolution(guessBlackField, Field.State.BLACK);
    }

    private void doBacktracking() {
        if (SolverUtils.isSolvedCorrectly(solvedMatchField)) {
            return;
        }
        if (backtrackingStack.empty()) {
            throw new IllegalStateException();
        }
        FieldIndex lastGuess = backtrackingStack.pop();
        List<FieldIndex> toBeUnsetFieldIndexes = solvingOrderList.stream()
                .dropWhile(index -> !index.equals(lastGuess)).collect(Collectors.toList());

        for (FieldIndex toBeUnsetFieldIndex : toBeUnsetFieldIndexes) {
            solvedMatchField.getFieldAt(toBeUnsetFieldIndex).setFieldState(Field.State.UNKNOWN);
            solvingOrderList.remove(toBeUnsetFieldIndex);
            alreadySolvedFieldIndexes.remove(toBeUnsetFieldIndex);
        }

        setStateAndAddToSolution(solvedMatchField.getFieldAt(lastGuess), Field.State.WHITE);
    }

    private void setStateAndAddToSolution(Field field, Field.State fieldState) {
        FieldIndex index = solvedMatchField.getIndexOfField(field);
        if (alreadySolvedFieldIndexes.contains(index)) {
            return;
        }
        //State setzen
        field.setFieldState(fieldState);
        solvingOrderList.add(index);
        alreadySolvedFieldIndexes.add(index);
        //Wenn State schwarz alle Nachbarn weiß setzen
        if (fieldState == Field.State.BLACK) {
            for (Direction directions : Direction.values()) {
                Field actNeighbourField = solvedMatchField.getNeighbourTo(field, directions);
                if (actNeighbourField != null && actNeighbourField.getFieldState() == Field.State.UNKNOWN) {
                    setStateAndAddToSolution(actNeighbourField, Field.State.WHITE);
                    //Felder finden, die eventuell weiß sein müssen, um eine zusammenhängende Fläche zu haben
                    FieldIndex fieldIndexOfActField = solvedMatchField.getIndexOfField(field);
                    potentialMustBeWhiteStack.add(new FieldIndex(fieldIndexOfActField.getX() + 1, fieldIndexOfActField.getY() + 1));
                    potentialMustBeWhiteStack.add(new FieldIndex(fieldIndexOfActField.getX() + 1, fieldIndexOfActField.getY() - 1));
                    potentialMustBeWhiteStack.add(new FieldIndex(fieldIndexOfActField.getX() - 1, fieldIndexOfActField.getY() + 1));
                    potentialMustBeWhiteStack.add(new FieldIndex(fieldIndexOfActField.getX() - 1, fieldIndexOfActField.getY() - 1));
                }
            }
        }
    }
}
