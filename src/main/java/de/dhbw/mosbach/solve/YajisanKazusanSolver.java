package de.dhbw.mosbach.solve;

import de.dhbw.mosbach.matchfield.MatchField;
import de.dhbw.mosbach.matchfield.fields.Field;
import de.dhbw.mosbach.matchfield.fields.HintField;
import de.dhbw.mosbach.matchfield.utils.FieldIndex;

import java.util.*;
import java.util.stream.Collectors;

public class YajisanKazusanSolver {

    private final MatchField unsolvedMatchField;
    private final MatchField solvedMatchField;
    private final List<FieldIndex> solvingOrderList = new ArrayList<>();
    private final Stack<FieldIndex> backtrackingStack = new Stack<>();
    private final Set<FieldIndex> potentialMustBeWhiteStack = new HashSet<>();
    private final Set<FieldIndex> alreadySolvedFieldIndexes = new HashSet<>();
    private boolean isSolved;

    public YajisanKazusanSolver(final MatchField unsolvedMatchField) {
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
        final long start = System.currentTimeMillis();
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
        System.out.println("Solved " + solvedMatchField.getEdgeSize() + "x" + solvedMatchField.getEdgeSize() + " Matchfield in " + (System.currentTimeMillis() - start) + " Milis!");
    }

    private void setImpossibleHintFieldsToBlack() {
        for (final List<Field> allField : solvedMatchField.getAllFields()) {
            for (final Field actField : allField) {
                if (actField.getFieldState() != Field.State.UNKNOWN || !(actField instanceof HintField)) {
                    continue;
                }
                final HintField actHintField = (HintField) actField;
                final int maxPossibleBlackFields = SolverUtils.calculateMaxPossibleBlackFieldsToDirection(solvedMatchField, actHintField, actHintField.getArrowDirection());
                if (actHintField.getAmount() > maxPossibleBlackFields) {
                    setStateAndAddToSolution(actHintField, Field.State.BLACK);
                }
            }
        }
    }

    private void useHintsOfWhiteHintFields() {
        for (final List<Field> allField : solvedMatchField.getAllFields()) {
            for (final Field actField : allField) {
                if (actField.getFieldState() != Field.State.WHITE || !(actField instanceof HintField)) {
                    continue;
                }
                final SolverUtils.BlackAndWhiteSolutionDTO solution = SolverUtils.getBlackAndWhiteUseHint(solvedMatchField, (HintField) actField);
                if (solution == null) {
                    continue;
                }
                for (final Field whiteField : solution.toBeWhitedFields) {
                    setStateAndAddToSolution(whiteField, Field.State.WHITE);
                }
                for (final Field blackField : solution.toBeBlackedFields) {
                    setStateAndAddToSolution(blackField, Field.State.BLACK);
                }
            }
        }
    }

    private void processPotentialWhiteFieldsForConnectedShape() {
        final List<Field> toBeWhitedFields = new ArrayList<>();
        for (final FieldIndex actFieldIndex : potentialMustBeWhiteStack) {
            final Field actField = solvedMatchField.getFieldAt(actFieldIndex);
            if (actField == null) {
                continue;
            }
            if (actField.getFieldState() == Field.State.UNKNOWN) {
                final int xStart = actFieldIndex.getX();
                final int yStart = actFieldIndex.getY();
                int count = 0;
                for (int x = -1; x <= 1; x += 2) {
                    for (int y = -1; y <= 1; y += 2) {
                        final Field borderField = solvedMatchField.getFieldAt(xStart + x, yStart + y);
                        if (borderField != null && borderField.getFieldState() == Field.State.BLACK) {
                            count++;
                        }
                    }
                }
                final int minimumNeighboursForBlockingField = 1;
                if (count >= minimumNeighboursForBlockingField) {
                    actField.setFieldState(Field.State.BLACK);
                    if (!SolverUtils.canOrAreWhiteFieldsStillBeConnected(solvedMatchField)) {
                        toBeWhitedFields.add(actField);
                    }
                    actField.setFieldState(Field.State.UNKNOWN);
                }
            }
        }
        potentialMustBeWhiteStack.clear();
        for (final Field whiteField : toBeWhitedFields) {
            setStateAndAddToSolution(whiteField, Field.State.WHITE);
        }
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
        final FieldIndex lastGuess = backtrackingStack.pop();
        final List<FieldIndex> toBeUnsetFieldIndexes = solvingOrderList.stream()
                .dropWhile(index -> !index.equals(lastGuess)).collect(Collectors.toList());

        for (final FieldIndex toBeUnsetFieldIndex : toBeUnsetFieldIndexes) {
            solvedMatchField.getFieldAt(toBeUnsetFieldIndex).setFieldState(Field.State.UNKNOWN);
            solvingOrderList.remove(toBeUnsetFieldIndex);
            alreadySolvedFieldIndexes.remove(toBeUnsetFieldIndex);
        }

        setStateAndAddToSolution(solvedMatchField.getFieldAt(lastGuess), Field.State.WHITE);
    }

    private void setStateAndAddToSolution(final Field field, final Field.State fieldState) {
        final FieldIndex index = solvedMatchField.getIndexOfField(field);
        if (alreadySolvedFieldIndexes.contains(index)) {
            return;
        }
        //State setzen
        field.setFieldState(fieldState);
        solvingOrderList.add(index);
        alreadySolvedFieldIndexes.add(index);
        //Wenn State schwarz alle Nachbarn weiß setzen
        if (fieldState == Field.State.BLACK) {
            for (final Field actNeighbourField : solvedMatchField.getAllNeighbours(field)) {
                if (actNeighbourField != null && actNeighbourField.getFieldState() == Field.State.UNKNOWN) {
                    setStateAndAddToSolution(actNeighbourField, Field.State.WHITE);
                }
            }
            //Felder finden, die weiß sein müssen, um eine zusammenhänge Fläche zu erhalten
        } else if (fieldState == Field.State.WHITE) {
            potentialMustBeWhiteStack.addAll(solvedMatchField.getAllNeighbours(field).stream().map(solvedMatchField::getIndexOfField).collect(Collectors.toList()));
        }
    }
}
