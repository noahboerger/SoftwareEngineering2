package de.dhbw.mosbach.solve;

import de.dhbw.mosbach.matchfield.MatchField;
import de.dhbw.mosbach.matchfield.fields.Field;
import de.dhbw.mosbach.matchfield.fields.HintField;
import de.dhbw.mosbach.matchfield.utils.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class SolverUtils {
    private SolverUtils() {
        throw new IllegalStateException("Uninstanzierbare Klasse");
    }

    static int calculateMaxPossibleBlackFieldsToDirection(MatchField matchField, Field field, Direction rowDirection) {
        List<Field> rowOrColumn = matchField.getFieldsToDirection(field, rowDirection);

        int counter = 0;
        boolean beforePotentialBlackField = false;

        for (Field actField : rowOrColumn) {
            switch (actField.getFieldState()) {
                case WHITE:
                    beforePotentialBlackField = false;
                    continue;
                case BLACK:
                    beforePotentialBlackField = true;
                    counter++;
                    break;
                case UNKNOWN:
                    boolean potentialBlackField = !beforePotentialBlackField;
                    if (potentialBlackField) {
                        potentialBlackField = isAbleToBeBlack(matchField, actField);
                    }
                    if (potentialBlackField) {
                        counter++;
                    }
                    beforePotentialBlackField = potentialBlackField;
            }
        }
        return counter;
    }

    private static boolean isAbleToBeBlack(MatchField matchField, Field field) {
        boolean potentialBlackField = true;
        for (Direction allDirections : Direction.values()) {
            Field actNeighbourField = matchField.getNeighbourTo(field, allDirections);
            if (actNeighbourField == null) {
                continue;
            }
            if (actNeighbourField.getFieldState() == Field.State.BLACK) {
                potentialBlackField = false;
                break;
            }
        }
        return potentialBlackField;
    }

    //Weißes Hintfield -> Hint nutzen, wenn möglich, gibt zu schwärzende Felder zurück
    public static BlackAndWhiteSolution getBlackAndWhiteUseHint(MatchField matchField, HintField hintField) {
        Field startField = matchField.getNeighbourTo(hintField, hintField.getArrowDirection());

        BlackAndWhiteSolution potentialCorrectSolution = null;
        for (BlackAndWhiteSolution actSolution : getListOfPossibleSolutions(matchField, startField, hintField.getArrowDirection())) {
            if(actSolution.toBeBlackedFields.size() == hintField.getAmount()) {
                if(potentialCorrectSolution == null) {
                    potentialCorrectSolution = actSolution;
                } else {
                    return null;
                }
            }
        }
        return potentialCorrectSolution;
    }

    static List<BlackAndWhiteSolution> getListOfPossibleSolutions(MatchField matchField, Field actField, Direction direction) {
        List<Field> upcomingFields = matchField.getFieldsToDirection(actField, direction);
        List<BlackAndWhiteSolution> solutionsList = new ArrayList<>();
        //Einfachster Fall, ein Feld übrig
        if (upcomingFields.isEmpty()) {
            //Farbe schon bekannt, leere Lösung zurückgeben
            if (actField.getFieldState() != Field.State.UNKNOWN) {
                solutionsList.add(new BlackAndWhiteSolution());
                return solutionsList;
            } else {
                //Farbe noch nicht bekannt
                if (isAbleToBeBlack(matchField, actField)) {
                    solutionsList.add(new BlackAndWhiteSolution(Arrays.asList(actField), new ArrayList<>()));
                }
                solutionsList.add(new BlackAndWhiteSolution(new ArrayList<>(), Arrays.asList(actField)));
                return solutionsList;
            }
        } else {
            //Mehrere Felder übrig
            List<BlackAndWhiteSolution> subProblemSolutionsList = getListOfPossibleSolutions(matchField, upcomingFields.get(0), direction);
            //Farbe schon bekannt, vorherige Lösungen zurückgeben
            if (actField.getFieldState() != Field.State.UNKNOWN) {
                return subProblemSolutionsList;
            } else {
                //Farbe noch nicht bekannt
                if (isAbleToBeBlack(matchField, actField)) {
                    for (BlackAndWhiteSolution subSolutions : subProblemSolutionsList) {
                        BlackAndWhiteSolution copySolution = BlackAndWhiteSolution.copyOf(subSolutions);
                        copySolution.toBeBlackedFields.add(0, actField);
                        solutionsList.add(copySolution);
                    }
                }
                for (BlackAndWhiteSolution subSolutions : subProblemSolutionsList) {
                    BlackAndWhiteSolution copySolution = BlackAndWhiteSolution.copyOf(subSolutions);
                    copySolution.toBeWhitedFields.add(0, actField);
                    solutionsList.add(copySolution);
                }
            }
            return solutionsList;
        }
    }

    static class BlackAndWhiteSolution {
        List<Field> toBeBlackedFields;
        List<Field> toBeWhitedFields;

        public BlackAndWhiteSolution(List<Field> toBeBlackedFields, List<Field> toBeWhitedFields) {
            this.toBeBlackedFields = toBeBlackedFields;
            this.toBeWhitedFields = toBeWhitedFields;
        }

        public BlackAndWhiteSolution() {
            this(new ArrayList(), new ArrayList());
        }

        public static BlackAndWhiteSolution copyOf(BlackAndWhiteSolution solution) {
            return new BlackAndWhiteSolution(new ArrayList<>(solution.toBeBlackedFields), new ArrayList<>(solution.toBeWhitedFields));
        }
    }
}
