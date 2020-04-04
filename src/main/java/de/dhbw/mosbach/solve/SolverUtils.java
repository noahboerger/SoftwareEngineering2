package de.dhbw.mosbach.solve;

import de.dhbw.mosbach.matchfield.MatchField;
import de.dhbw.mosbach.matchfield.fields.Field;
import de.dhbw.mosbach.matchfield.fields.HintField;
import de.dhbw.mosbach.matchfield.fields.StandardField;
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
        Stack<Field> toBeBlackedFields;
        Stack<Field> toBeWhitedFields;

        private BlackAndWhiteSolutionDTO(Stack<Field> toBeBlackedFields, Stack<Field> toBeWhitedFields) {
            this.toBeBlackedFields = toBeBlackedFields;
            this.toBeWhitedFields = toBeWhitedFields;
        }

        private BlackAndWhiteSolutionDTO() {
            this(new Stack<>(), new Stack<>());
        }

        private static BlackAndWhiteSolutionDTO copyOf(BlackAndWhiteSolutionDTO solution) {
            @SuppressWarnings("unchecked") //Typ-Sicher, da der Typ des geclonten Stacks dem des vorherigen entspricht
                    BlackAndWhiteSolutionDTO copy = new BlackAndWhiteSolutionDTO((Stack<Field>) solution.toBeBlackedFields.clone(), (Stack<Field>) solution.toBeWhitedFields.clone());
            return copy;
        }
    }

    //Prüft ob das Feld bereits korrekt gelöst wurde
    static boolean isSolvedCorrectly(MatchField matchField) {
        if (matchField.getFieldsWithState(Field.State.UNKNOWN).size() != 0) {
            return false;
        }
        return canOrAreWhiteFieldsStillBeConnected(matchField) &&
                canOrIsEveryWhiteHintFieldStillGetCorrect(matchField) &&
                areNoBlackFieldsConnected(matchField);
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

    //Gibt zurück ob ein Feld nicht mehr lösbar ist (z.B. aufgrund falsch geratener Felder)
    static boolean isDefinitelyUnableToBeSolvedAnyMore(MatchField matchField) {
        return !canOrIsEveryWhiteHintFieldStillGetCorrect(matchField) ||
                !canOrAreWhiteFieldsStillBeConnected(matchField) ||
                !areNoBlackFieldsConnected(matchField);
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
        return foundFields.size() == matchField.getFieldsNotWithState(Field.State.BLACK).size();
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
        final int alreadyBlackInRow = countFieldsToDirectionWithStateBlack(matchField, hintField, hintField.getArrowDirection());

        //Wenn Nachbar direkt außerhalb, ist die einzige Lösung nichts zu markieren
        if (startField == null) {
            return new BlackAndWhiteSolutionDTO();
        }

        BlackAndWhiteSolutionDTO potentialCorrectSolution = null;
        List<BlackAndWhiteSolutionDTO> solutionDTOList = getListOfPossibleSolutions(matchField, startField, hintField.getArrowDirection());
        if (solutionDTOList.isEmpty()) {
            return null;
        }
        Stack<Field> blacks = solutionDTOList.get(0).toBeBlackedFields;
        Stack<Field> white = solutionDTOList.get(0).toBeWhitedFields;
        for (BlackAndWhiteSolutionDTO actSolution : solutionDTOList) {
            if (actSolution.toBeBlackedFields.size() + alreadyBlackInRow == hintField.getAmount()) {
                if (potentialCorrectSolution == null) {
                    potentialCorrectSolution = actSolution;
                } else {
                    return null;
                }
                blacks.removeAll(potentialCorrectSolution.toBeWhitedFields);
                white.removeAll(potentialCorrectSolution.toBeBlackedFields);
            }
        }
        return Objects.requireNonNullElseGet(potentialCorrectSolution, () -> new BlackAndWhiteSolutionDTO(blacks, white));
    }

    //Berechnet alle noch möglichen Lösungen für eine Reihe inklusive des aktuellen Feldes (Hilfsmethode für getBlackAndWhiteUseHint)
    static List<BlackAndWhiteSolutionDTO> getListOfPossibleSolutions(MatchField matchField, Field actField, Direction direction) {
        List<Field> upcomingFields = matchField.getFieldsToDirection(actField, direction);
        List<BlackAndWhiteSolutionDTO> solutionsList = new ArrayList<>();

        //Wenn direkt außerhalb, Lösung ist nur nichts zu markieren
        if (actField == null) {
            return Collections.singletonList(new BlackAndWhiteSolutionDTO());
        }

        //Einfachster Fall, ein Feld übrig
        if (upcomingFields.isEmpty()) {
            //Farbe schon bekannt, leere Lösung zurückgeben
            if (actField.getFieldState() != Field.State.UNKNOWN) {
                solutionsList.add(new BlackAndWhiteSolutionDTO());
            } else {
                //Farbe noch nicht bekannt
                if (isAbleToBeBlack(matchField, actField)) {
                    Stack<Field> blackStack = new Stack<>();
                    blackStack.add(actField);
                    solutionsList.add(new BlackAndWhiteSolutionDTO(blackStack, new Stack<>()));
                }
                Stack<Field> whiteStack = new Stack<>();
                whiteStack.add(actField);
                solutionsList.add(new BlackAndWhiteSolutionDTO(new Stack<>(), whiteStack));
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
                        copySolution.toBeBlackedFields.push(actField);
                        solutionsList.add(copySolution);
                    }
                }
                for (BlackAndWhiteSolutionDTO subSolutions : subProblemSolutionsList) {
                    BlackAndWhiteSolutionDTO copySolution = BlackAndWhiteSolutionDTO.copyOf(subSolutions);
                    copySolution.toBeWhitedFields.push(actField);
                    solutionsList.add(copySolution);
                }
            }
        }
        return solutionsList;
    }

    //Zählt Felder mit angegebenem Status ab dem angegebenen Feld (exklusiv) in die angegebene Richtung
    static int countFieldsToDirectionWithStateBlack(MatchField matchField, Field field, Direction direction) {
        return (int) matchField.getFieldsToDirection(field, direction).stream()
                .filter(x -> x.getFieldState() == Field.State.BLACK).count();
    }

    //Methode zum möglichst richtigen Erratens eines schwarzen Feldes Hinweiß Feldes
    static Field findPotentialBestBlackGuessHintField(MatchField matchField) {
        List<HintField> unknownHintFields = matchField.getAllFields().stream()
                .flatMap(Collection::stream)
                .filter(field -> field.getFieldState() == Field.State.UNKNOWN)
                .filter(field -> field instanceof HintField)
                .map(field -> (HintField) field)
                .collect(Collectors.toList());

        Map<HintField, Integer> possibleHintSolutions = new HashMap<>();
        for (HintField actHintField : unknownHintFields) {
            Field startNeighbourField = matchField.getNeighbourTo(actHintField, actHintField.getArrowDirection());
            //Bei keinem Feld lässt sich keine Aussage treffen, deshalb setzten eines unerreichbaren Wertes
            if (startNeighbourField == null) {
                possibleHintSolutions.put(actHintField, Integer.MAX_VALUE);
                continue;
            }
            List<BlackAndWhiteSolutionDTO> allSolutions = getListOfPossibleSolutions(matchField, startNeighbourField, actHintField.getArrowDirection());
            final int alreadyBlackInRow = countFieldsToDirectionWithStateBlack(matchField, actHintField, actHintField.getArrowDirection());

            final int correctSolutions = (int) allSolutions.stream()
                    .filter(solution -> solution.toBeBlackedFields.size() + alreadyBlackInRow == actHintField.getAmount()).count();

            possibleHintSolutions.put(actHintField, correctSolutions);
        }
        return possibleHintSolutions.keySet().stream()
                .min(Comparator.comparingInt(possibleHintSolutions::get)).orElse(null);
    }

    //Methode zum möglichst richtigen Erraten eines schwarzen Standard Feldes
    static Field findPotentialBestBlackGuessStandardField(MatchField matchField) {
        List<StandardField> unknownStandardFields = matchField.getAllFields().stream()
                .flatMap(Collection::stream)
                .filter(field -> field.getFieldState() == Field.State.UNKNOWN)
                .filter(field -> field instanceof StandardField)
                .map(field -> (StandardField) field)
                .collect(Collectors.toList());

        Map<StandardField, Double> probability = new HashMap<>();

        for (StandardField actField : unknownStandardFields) {
            probability.put(actField, 0.0);
            for (Direction allDirections : Direction.values()) {
                for (Field actFieldInRowOrColumn : matchField.getFieldsToDirection(actField, allDirections)) {
                    if (!(actFieldInRowOrColumn instanceof HintField)) {
                        continue;
                    }
                    HintField actHintFieldInRowOrColumn = (HintField) actFieldInRowOrColumn;
                    if (actHintFieldInRowOrColumn.getArrowDirection() != allDirections.getOppositeDirection()) {
                        continue;
                    }
                    List<BlackAndWhiteSolutionDTO> solutions = getListOfPossibleSolutions(matchField, matchField.getNeighbourTo(actHintFieldInRowOrColumn, actHintFieldInRowOrColumn.getArrowDirection()), actHintFieldInRowOrColumn.getArrowDirection());
                    int numberOfWithActFieldBlack = (int) solutions.stream()
                            .filter(solution -> solution.toBeBlackedFields.contains(actField)).count();
                    double actPartSolution = (double) solutions.size() / (double) numberOfWithActFieldBlack;
                    if (actHintFieldInRowOrColumn.getFieldState() == Field.State.WHITE) {
                        actPartSolution *= 3;
                    }
                    probability.put(actField, probability.get(actField) + actPartSolution);
                }
            }
        }
        return probability.keySet().stream().max(Comparator.comparingDouble(probability::get)).orElse(null);
    }
}