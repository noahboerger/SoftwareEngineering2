package de.dhbw.mosbach.solve;

import de.dhbw.mosbach.matchfield.MatchField;
import de.dhbw.mosbach.matchfield.fields.Field;
import de.dhbw.mosbach.matchfield.fields.HintField;
import de.dhbw.mosbach.matchfield.utils.Direction;
import de.dhbw.mosbach.matchfield.utils.FieldIndex;

import java.util.*;
import java.util.stream.Collectors;

//Uninstanzierbare Klasse mit Hilfsmethoden zum finden einer Lösung
final class SolverUtils {
    private SolverUtils() {
        throw new IllegalStateException("Uninstanzierbare Klasse");
    }

    //Statische DTO-Klasse zum zurückgeben einer Liste an zu schwärzenden und weißen Feldern
    static class BlackAndWhiteSolutionDTO {
        //TODO: Use Stacks
        List<Field> toBeBlackedFields;
        List<Field> toBeWhitedFields;

        private BlackAndWhiteSolutionDTO(List<Field> toBeBlackedFields, List<Field> toBeWhitedFields) {
            this.toBeBlackedFields = toBeBlackedFields;
            this.toBeWhitedFields = toBeWhitedFields;
        }

        private BlackAndWhiteSolutionDTO() {
            this(new ArrayList<>(), new ArrayList<>());
        }

        private static BlackAndWhiteSolutionDTO copyOf(BlackAndWhiteSolutionDTO solution) {
            return new BlackAndWhiteSolutionDTO(new ArrayList<>(solution.toBeBlackedFields), new ArrayList<>(solution.toBeWhitedFields));
        }
    }

    //Prüft ob das Feld bereits korrekt gelöst wurde
    static boolean isSolvedCorrectly(MatchField matchField) {
        return canOrAreWhiteFieldsStillBeConnected(matchField) &&
                canOrIsEveryWhiteHintFieldStillGetCorrect(matchField) &&
                areNoBlackFieldsConnected(matchField) &&
                matchField.getNumberOfFieldsWithState(Field.State.UNKNOWN) == 0;
    }

    //Berechnet wie viele Schwarze Felder sich ab einem Feld in eine spezifische Richtung befeinden (ohne das Feld selbst)
    static int calculateAlreadySetBlackFieldsToDirection(MatchField matchField, Field field, Direction rowDirection) {
        List<Field> rowOrColumn = matchField.getFieldsToDirection(field, rowDirection);
        int counter = 0;
        for (Field actField : rowOrColumn) {
            if (actField.getFieldState() == Field.State.BLACK) {
                counter++;
            }
        }
        return counter;
    }

    //Berechnet wie viele Schwarze Felder maximal ab einem Feld in eine spezifische Richtung gesetzt werden können (ohne das Feld selbst)
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

    //Überprüft ob ein feld noch geschwärzt werden darf (keine geschwärzten Nachbaren)
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

    //TODO: eventuell die methoden drinnen für bessere Performanz verbinden
    //Gibt zurück ob ein Feld nicht mehr lösbar ist (z.B. aufgrund falsch geratener Felder)
    static boolean isDefinitelyUnableToBeSolvedAnyMore(MatchField matchField) {
        return !(canOrAreWhiteFieldsStillBeConnected(matchField) &&
                canOrIsEveryWhiteHintFieldStillGetCorrect(matchField) &&
                areNoBlackFieldsConnected(matchField));
    }

    //Überprüft ob die Hinweise von weißen Hinweißfeldern Feldern noch erfüllt werden können
    static boolean canOrIsEveryWhiteHintFieldStillGetCorrect(MatchField matchField) {
        for (List<Field> fields : matchField.getAllFields()) {
            for (Field actField : fields) {
                if (actField instanceof HintField && actField.getFieldState() == Field.State.WHITE) {
                    HintField actHintField = (HintField) actField;
                    if (calculateMaxPossibleBlackFieldsToDirection(matchField, actHintField, actHintField.getArrowDirection()) < actHintField.getAmount() || calculateAlreadySetBlackFieldsToDirection(matchField, actHintField, actHintField.getArrowDirection()) > actHintField.getAmount()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //Überprüft ob keine schwarzen Felder illegal miteinander verbunden sind
    static boolean areNoBlackFieldsConnected(MatchField matchField) {
        for (List<Field> fields : matchField.getAllFields()) {
            for (Field actField : fields) {
                if (actField.getFieldState() == Field.State.BLACK) {
                    for (Field neighbourField : matchField.getAllNeighbours(actField)) {
                        if (neighbourField.getFieldState() == Field.State.BLACK) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    //Überprüft ob die weißen und ungesetzten Felder noch verbunden alle verbunden sind
    static boolean canOrAreWhiteFieldsStillBeConnected(MatchField matchField) {
        Field firstWhiteField = findFirstFieldWithState(matchField, Field.State.WHITE);
        if (firstWhiteField == null) {
            return true;
        }
        FieldIndex indexOfFirstField = matchField.getIndexOfField(firstWhiteField);
        Set<FieldIndex> foundFields = new HashSet<>();
        List<FieldIndex> toBeProcessedFields = new ArrayList<>();
        foundFields.add(indexOfFirstField);
        toBeProcessedFields.add(indexOfFirstField);

        while (!toBeProcessedFields.isEmpty()) {
            Field actField = matchField.getFieldAt(toBeProcessedFields.remove(0));
            List<FieldIndex> unprocessedFieldIndexes = matchField.getAllNeighbours(actField).stream()
                    .filter(field -> field.getFieldState() != Field.State.BLACK)
                    .map(matchField::getIndexOfField)
                    .filter(field -> !foundFields.contains(field))
                    .collect(Collectors.toList());
            toBeProcessedFields.addAll(unprocessedFieldIndexes);
            foundFields.addAll(unprocessedFieldIndexes);
        }
        return foundFields.size() == matchField.getNumberOfFieldsNotWithState(Field.State.BLACK);
    }

    //Gibt das erste gefundene Felde mit angegebenem Status oder null zurück
    static Field findFirstFieldWithState(MatchField matchField, Field.State fieldState) {
        for (List<Field> fields : matchField.getAllFields()) {
            for (Field actField : fields) {
                if (actField.getFieldState() == fieldState) {
                    return actField;
                }
            }
        }
        return null;
    }

    //Weißes Hintfield -> Hint nutzen, wenn möglich, gibt zu schwärzende und weiße Felder zurück
    static BlackAndWhiteSolutionDTO getBlackAndWhiteUseHint(MatchField matchField, HintField hintField) {
        Field startField = matchField.getNeighbourTo(hintField, hintField.getArrowDirection());
        final int alreadyBlackInRow = getAlreadyBlackedFieldsToDirection(matchField, hintField, hintField.getArrowDirection());

        BlackAndWhiteSolutionDTO potentialCorrectSolution = null;
        for (BlackAndWhiteSolutionDTO actSolution : getListOfPossibleSolutions(matchField, startField, hintField.getArrowDirection())) {
            if (actSolution.toBeBlackedFields.size() + alreadyBlackInRow == hintField.getAmount()) {
                if (potentialCorrectSolution == null) {
                    potentialCorrectSolution = actSolution;
                } else {
                    return null;
                }
            }
        }
        return potentialCorrectSolution;
    }

    //Berechnet alle noch möglichen Lösungen für eine Reihe inklusive des aktuellen Feldes (Hilfsmethode für getBlackAndWhiteUseHint)
    static List<BlackAndWhiteSolutionDTO> getListOfPossibleSolutions(MatchField matchField, Field actField, Direction direction) {
        List<Field> upcomingFields = matchField.getFieldsToDirection(actField, direction);
        List<BlackAndWhiteSolutionDTO> solutionsList = new ArrayList<>();
        //Einfachster Fall, ein Feld übrig
        if (upcomingFields.isEmpty()) {
            //Farbe schon bekannt, leere Lösung zurückgeben
            if (actField.getFieldState() != Field.State.UNKNOWN) {
                solutionsList.add(new BlackAndWhiteSolutionDTO());
            } else {
                //Farbe noch nicht bekannt
                if (isAbleToBeBlack(matchField, actField)) {
                    solutionsList.add(new BlackAndWhiteSolutionDTO(Collections.singletonList(actField), new ArrayList<>()));
                }
                solutionsList.add(new BlackAndWhiteSolutionDTO(new ArrayList<>(), Collections.singletonList(actField)));
            }
        } else {
            //Mehrere Felder übrig
            List<BlackAndWhiteSolutionDTO> subProblemSolutionsList = getListOfPossibleSolutions(matchField, upcomingFields.get(0), direction);
            //Farbe schon bekannt, vorherige Lösungen zurückgeben
            if (actField.getFieldState() != Field.State.UNKNOWN) {
                return subProblemSolutionsList;
            } else {
                //Farbe noch nicht bekannt
                if (isAbleToBeBlack(matchField, actField)) {
                    for (BlackAndWhiteSolutionDTO subSolutions : subProblemSolutionsList) {
                        BlackAndWhiteSolutionDTO copySolution = BlackAndWhiteSolutionDTO.copyOf(subSolutions);
                        copySolution.toBeBlackedFields.add(0, actField);
                        solutionsList.add(copySolution);
                    }
                }
                for (BlackAndWhiteSolutionDTO subSolutions : subProblemSolutionsList) {
                    BlackAndWhiteSolutionDTO copySolution = BlackAndWhiteSolutionDTO.copyOf(subSolutions);
                    copySolution.toBeWhitedFields.add(0, actField);
                    solutionsList.add(copySolution);
                }
            }
        }
        return solutionsList;
    }

    //TODO: with STREAMS
    static int getAlreadyBlackedFieldsToDirection(MatchField matchField, Field field, Direction direction) {
        int count = 0;
        for (Field actField : matchField.getFieldsToDirection(field, direction)) {
            if (actField.getFieldState() == Field.State.BLACK) {
                count++;
            }
        }
        return count;
    }

    /*
    METHODEN ZUM MÖGLICHST RICHTIGEN ERRATEN EINES FELDES:
    */

    //TODO: kann verbessert werden, indem auch reihen statt nur hinweißfedlder Überprüft werden
    static Field findPotentialBestBlackGuessHintField(MatchField matchField) {
        List<HintField> unknownHintFields = matchField.getAllFields().stream()
                .flatMap(column -> column.stream())
                .filter(field -> field.getFieldState() == Field.State.UNKNOWN)
                .filter(field -> field instanceof HintField)
                .map(field -> (HintField) field)
                .collect(Collectors.toList());

        Map<HintField, Integer> possibleHintSolutions = new HashMap<>();
        for(HintField actHintField : unknownHintFields) {
            Field startNeighbourField = matchField.getNeighbourTo(actHintField, actHintField.getArrowDirection());
            List<BlackAndWhiteSolutionDTO> allSolutions = getListOfPossibleSolutions(matchField, startNeighbourField, actHintField.getArrowDirection());
            final int alreadyBlackInRow = getAlreadyBlackedFieldsToDirection(matchField, actHintField, actHintField.getArrowDirection());

            final int correctSolutions = (int) allSolutions.stream()
                    .filter(solution -> solution.toBeBlackedFields.size() + alreadyBlackInRow == actHintField.getAmount()).count();

            possibleHintSolutions.put(actHintField, correctSolutions);
        }
        return possibleHintSolutions.keySet().stream()
                .sorted(Comparator.comparingInt(key -> possibleHintSolutions.get(key)))
                .findFirst().orElse(null);
    }
}