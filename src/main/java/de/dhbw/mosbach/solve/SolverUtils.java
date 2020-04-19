package de.dhbw.mosbach.solve;

import de.dhbw.mosbach.matchfield.MatchField;
import de.dhbw.mosbach.matchfield.fields.Field;
import de.dhbw.mosbach.matchfield.fields.HintField;
import de.dhbw.mosbach.matchfield.fields.StandardField;
import de.dhbw.mosbach.matchfield.utils.Direction;
import de.dhbw.mosbach.matchfield.utils.FieldIndex;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

//Uninstanzierbare Klasse mit Hilfsmethoden zum finden einer Lösung
final class SolverUtils {
    private SolverUtils() {
        throw new IllegalStateException("Uninstanzierbare Klasse");
    }

    //Gibt zurück ob ein Feld nicht mehr lösbar ist (z.B. aufgrund falsch geratener Felder)
    static boolean isDefinitelyUnableToBeSolvedAnyMore(final MatchField matchField) {
        return !canOrIsEveryWhiteHintFieldStillGetCorrect(matchField) ||
                !canOrAreWhiteFieldsStillBeConnected(matchField) ||
                !areNoBlackFieldsConnected(matchField);
    }

    //Prüft ob das Feld bereits korrekt gelöst wurde
    static boolean isSolvedCorrectly(final MatchField matchField) {
        return !(matchField.getFieldsWithState(Field.State.UNKNOWN).size() != 0 ||
                !canOrAreWhiteFieldsStillBeConnected(matchField) ||
                !canOrIsEveryWhiteHintFieldStillGetCorrect(matchField) ||
                !areNoBlackFieldsConnected(matchField));
    }

    //Berechnet wie viele Schwarze Felder sich ab einem Feld in eine spezifische Richtung befeinden (ohne das Feld selbst)
    static int calculateAlreadySetBlackFieldsToDirection(final MatchField matchField, final Field field, final Direction rowDirection) {
        final List<Field> rowOrColumn = matchField.getFieldsToDirection(field, rowDirection);
        int counter = 0;
        for (final Field actField : rowOrColumn) {
            if (actField.getFieldState() == Field.State.BLACK) {
                counter++;
            }
        }
        return counter;
    }

    //Berechnet wie viele Schwarze Felder maximal ab einem Feld in eine spezifische Richtung gesetzt werden können (ohne das Feld selbst)
    static int calculateMaxPossibleBlackFieldsToDirection(final MatchField matchField, final Field field, final Direction rowDirection) {
        final List<Field> rowOrColumn = matchField.getFieldsToDirection(field, rowDirection);

        int counter = 0;
        boolean beforePotentialBlackField = false;

        for (final Field actField : rowOrColumn) {
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
                    break;
            }
        }
        return counter;
    }

    //Überprüft ob ein feld noch geschwärzt werden darf (keine geschwärzten Nachbaren)
    private static boolean isAbleToBeBlack(final MatchField matchField, final Field field) {
        boolean potentialBlackField = true;
        for (final Direction allDirections : Direction.values()) {
            final Field actNeighbourField = matchField.getNeighbourTo(field, allDirections);
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

    //Überprüft ob die Hinweise von weißen Hinweißfeldern Feldern noch erfüllt werden können
    static boolean canOrIsEveryWhiteHintFieldStillGetCorrect(final MatchField matchField) {
        for (final List<Field> fields : matchField.getAllFields()) {
            for (final Field actField : fields) {
                if (actField instanceof HintField && actField.getFieldState() == Field.State.WHITE) {
                    final HintField actHintField = (HintField) actField;
                    if (calculateMaxPossibleBlackFieldsToDirection(matchField, actHintField, actHintField.getArrowDirection()) < actHintField.getAmount() || calculateAlreadySetBlackFieldsToDirection(matchField, actHintField, actHintField.getArrowDirection()) > actHintField.getAmount()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //Überprüft ob keine schwarzen Felder illegal miteinander verbunden sind
    static boolean areNoBlackFieldsConnected(final MatchField matchField) {
        for (final List<Field> fields : matchField.getAllFields()) {
            for (final Field actField : fields) {
                if (actField.getFieldState() == Field.State.BLACK) {
                    for (final Field neighbourField : matchField.getAllNeighbours(actField)) {
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
    static boolean canOrAreWhiteFieldsStillBeConnected(final MatchField matchField) {
        final Field firstWhiteField = findFirstFieldWithState(matchField, Field.State.WHITE);
        if (firstWhiteField == null) {
            return true;
        }
        final FieldIndex indexOfFirstField = matchField.getIndexOfField(firstWhiteField);
        final Set<FieldIndex> foundFields = new HashSet<>();
        final List<FieldIndex> toBeProcessedFields = new ArrayList<>();
        foundFields.add(indexOfFirstField);
        toBeProcessedFields.add(indexOfFirstField);

        while (!toBeProcessedFields.isEmpty()) {
            final Field actField = matchField.getFieldAt(toBeProcessedFields.remove(0));
            final List<FieldIndex> unprocessedFieldIndexes = matchField.getAllNeighbours(actField).stream()
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
    static Field findFirstFieldWithState(final MatchField matchField, final Field.State fieldState) {
        return matchField.getAllFields().stream()
                .flatMap(Collection::stream)
                .filter(field -> field.getFieldState() == fieldState)
                .findFirst().orElse(null);
    }

    //Weißes Hintfield -> Hint nutzen, wenn möglich, gibt zu schwärzende und weiße Felder zurück
    static BlackAndWhiteSolutionDTO getBlackAndWhiteUseHint(final MatchField matchField, final HintField hintField) {
        final Field startField = matchField.getNeighbourTo(hintField, hintField.getArrowDirection());

        //Wenn Nachbar direkt außerhalb, ist die einzige Lösung nichts zu markieren
        if (startField == null) {
            return new BlackAndWhiteSolutionDTO();
        }

        final int alreadyBlackInRow = countFieldsToDirectionWithStateBlack(matchField, hintField, hintField.getArrowDirection());
        final List<BlackAndWhiteSolutionDTO> solutionDTOList = getListOfPossibleSolutions(matchField, startField, hintField.getArrowDirection()).stream()
                .filter(x -> x.toBeBlackedFields.size() + alreadyBlackInRow == hintField.getAmount())
                .collect(Collectors.toList());
        if (solutionDTOList.isEmpty()) {
            return null;
        }
        if (solutionDTOList.size() == 1) {
            return solutionDTOList.get(0);
        }
        final Deque<FieldIndex> blacks = solutionDTOList.get(0).toBeBlackedFields;
        final Deque<FieldIndex> white = solutionDTOList.get(0).toBeWhitedFields;
        for (final BlackAndWhiteSolutionDTO actSolution : solutionDTOList) {
            blacks.removeAll(actSolution.toBeWhitedFields);
            white.removeAll(actSolution.toBeBlackedFields);

        }
        if (blacks.isEmpty() && white.isEmpty()) {
            return null;
        }
        return new BlackAndWhiteSolutionDTO(blacks, white
        );
    }

    //Berechnet alle noch möglichen Lösungen für eine Reihe inklusive des aktuellen Feldes (Hilfsmethode für getBlackAndWhiteUseHint und zum "Raten")
    static List<BlackAndWhiteSolutionDTO> getListOfPossibleSolutions(final MatchField matchField, final Field actField, final Direction direction) {
        //Wenn direkt außerhalb, Lösung ist nur nichts zu markieren
        if (actField == null) {
            return Collections.singletonList(new BlackAndWhiteSolutionDTO());
        }
        final List<Field> upcomingFields = matchField.getFieldsToDirection(actField, direction);
        final List<BlackAndWhiteSolutionDTO> solutionsList = new ArrayList<>();
        //Einfachster Fall, ein Feld übrig
        if (upcomingFields.isEmpty()) {
            //Farbe schon bekannt, leere Lösung zurückgeben
            if (actField.getFieldState() != Field.State.UNKNOWN) {
                solutionsList.add(new BlackAndWhiteSolutionDTO());
            } else {
                //Farbe noch nicht bekannt
                if (isAbleToBeBlack(matchField, actField)) {
                    final Deque<FieldIndex> blackStack = new ArrayDeque<>();
                    blackStack.add(matchField.getIndexOfField(actField));
                    solutionsList.add(new BlackAndWhiteSolutionDTO(blackStack, new ArrayDeque<>()));
                }
                final Deque<FieldIndex> whiteStack = new ArrayDeque<>();
                whiteStack.add(matchField.getIndexOfField(actField));
                solutionsList.add(new BlackAndWhiteSolutionDTO(new ArrayDeque<>(), whiteStack));
            }
        } else {
            //Mehrere Felder übrig
            final List<BlackAndWhiteSolutionDTO> subProblemSolutionsList = getListOfPossibleSolutions(matchField, upcomingFields.get(0), direction);
            //Farbe schon bekannt, vorherige Lösungen zurückgeben
            if (actField.getFieldState() != Field.State.UNKNOWN) {
                return subProblemSolutionsList;
            } else {
                //Farbe noch nicht bekannt
                if (isAbleToBeBlack(matchField, actField)) {
                    for (final BlackAndWhiteSolutionDTO subSolutions : subProblemSolutionsList) {
                        if (subSolutions.toBeBlackedFields.contains(matchField.getIndexOfField(matchField.getNeighbourTo(actField, direction)))) {
                            continue;
                        }
                        final BlackAndWhiteSolutionDTO copySolution = BlackAndWhiteSolutionDTO.copyOf(subSolutions);
                        copySolution.toBeBlackedFields.push(matchField.getIndexOfField(actField));
                        solutionsList.add(copySolution);
                    }
                }
                for (final BlackAndWhiteSolutionDTO subSolutions : subProblemSolutionsList) {
                    final BlackAndWhiteSolutionDTO copySolution = BlackAndWhiteSolutionDTO.copyOf(subSolutions);
                    copySolution.toBeWhitedFields.push(matchField.getIndexOfField(actField));
                    solutionsList.add(copySolution);
                }
            }
        }
        return solutionsList;
    }

    //Zählt Felder mit angegebenem Status ab dem angegebenen Feld (exklusiv) in die angegebene Richtung
    static int countFieldsToDirectionWithStateBlack(final MatchField matchField, final Field field, final Direction direction) {
        return (int) matchField.getFieldsToDirection(field, direction).stream()
                .filter(x -> x.getFieldState() == Field.State.BLACK).count();
    }

    //Methode zum möglichst richtigen Erratens eines schwarzen Feldes Hinweiß Feldes
    static Field findPotentialBestBlackGuessHintField(final MatchField matchField) {
        final List<HintField> unknownHintFields = matchField.getAllFields().stream()
                .flatMap(Collection::stream)
                .filter(field -> field.getFieldState() == Field.State.UNKNOWN)
                .filter(field -> field instanceof HintField)
                .map(field -> (HintField) field)
                .collect(Collectors.toList());

        final Map<HintField, Integer> possibleHintSolutions = new HashMap<>();
        for (final HintField actHintField : unknownHintFields) {
            final Field startNeighbourField = matchField.getNeighbourTo(actHintField, actHintField.getArrowDirection());
            //Bei keinem Feld lässt sich keine Aussage treffen, deshalb setzten eines unerreichbaren Wertes
            if (startNeighbourField == null) {
                possibleHintSolutions.put(actHintField, Integer.MAX_VALUE);
                continue;
            }
            final List<BlackAndWhiteSolutionDTO> allSolutions = getListOfPossibleSolutions(matchField, startNeighbourField, actHintField.getArrowDirection());
            final int alreadyBlackInRow = countFieldsToDirectionWithStateBlack(matchField, actHintField, actHintField.getArrowDirection());

            final int correctSolutions = (int) allSolutions.stream()
                    .filter(solution -> solution.toBeBlackedFields.size() + alreadyBlackInRow == actHintField.getAmount()).count();

            possibleHintSolutions.put(actHintField, correctSolutions);
        }
        return possibleHintSolutions.keySet().stream()
                .min(Comparator.comparingInt(possibleHintSolutions::get)).orElse(null);
    }

    //Methode zum möglichst richtigen Erraten eines schwarzen Standard Feldes
    static Field findPotentialBestBlackGuessStandardField(final MatchField matchField) {
        final List<StandardField> unknownStandardFields = matchField.getAllFields().stream()
                .flatMap(Collection::stream)
                .filter(field -> field.getFieldState() == Field.State.UNKNOWN)
                .filter(field -> field instanceof StandardField)
                .map(field -> (StandardField) field)
                .collect(Collectors.toList());

        final Map<StandardField, Double> probability = new HashMap<>();

        for (final StandardField actField : unknownStandardFields) {
            probability.put(actField, 0.0);
            for (final Direction allDirections : Direction.values()) {
                for (final Field actFieldInRowOrColumn : matchField.getFieldsToDirection(actField, allDirections)) {
                    if (!(actFieldInRowOrColumn instanceof HintField)) {
                        continue;
                    }
                    final HintField actHintFieldInRowOrColumn = (HintField) actFieldInRowOrColumn;
                    //Wenn es in die falsche richtung zeigt oder schwarz ist, weiter da keine Informationen
                    if (actHintFieldInRowOrColumn.getArrowDirection() != allDirections.getOppositeDirection() || actFieldInRowOrColumn.getFieldState() == Field.State.BLACK) {
                        continue;
                    }
                    final List<BlackAndWhiteSolutionDTO> solutions = getListOfPossibleSolutions(matchField, matchField.getNeighbourTo(actHintFieldInRowOrColumn, actHintFieldInRowOrColumn.getArrowDirection()), actHintFieldInRowOrColumn.getArrowDirection());
                    final int numberOfWithActFieldBlack = (int) solutions.stream()
                            .filter(solution -> solution.toBeBlackedFields.contains(matchField.getIndexOfField(actField))).count();
                    double actPartSolution = (double) solutions.size() / (double) numberOfWithActFieldBlack;
                    if (actHintFieldInRowOrColumn.getFieldState() == Field.State.WHITE) {
                        actPartSolution *= 2; //Deutlich höhere Wahrscheinlichkeit, wenn Hint-Feld das Hinweis gibt schon weiß
                    }
                    probability.put(actField, probability.get(actField) + actPartSolution);
                }
            }
        }
        return probability.keySet().stream().max(Comparator.comparingDouble(probability::get)).orElse(null);
    }

    //Statische DTO-Klasse zum zurückgeben eines Stacks an zu schwärzenden und weißen Feldern
    static class BlackAndWhiteSolutionDTO {
        final Deque<FieldIndex> toBeBlackedFields;
        final Deque<FieldIndex> toBeWhitedFields;

        private BlackAndWhiteSolutionDTO(final Deque<FieldIndex> toBeBlackedFields, final Deque<FieldIndex> toBeWhitedFields) {
            this.toBeBlackedFields = toBeBlackedFields;
            this.toBeWhitedFields = toBeWhitedFields;
        }

        private BlackAndWhiteSolutionDTO() {
            this(new ArrayDeque<>(), new ArrayDeque<>());
        }

        private static BlackAndWhiteSolutionDTO copyOf(final BlackAndWhiteSolutionDTO solution) {
            try {
                @SuppressWarnings("unchecked") //Wenn clone-Methode vorhanden und ausführbar muss der Typ passen
                        Deque<FieldIndex> clonedBlack = (Deque<FieldIndex>) solution.toBeWhitedFields.getClass().getMethod("clone").invoke(solution.toBeBlackedFields);
                @SuppressWarnings("unchecked") //Wenn clone-Methode vorhanden und ausführbar muss der Typ passen
                        Deque<FieldIndex> clonedWhite = (Deque<FieldIndex>) solution.toBeWhitedFields.getClass().getMethod("clone").invoke(solution.toBeWhitedFields);
                return new BlackAndWhiteSolutionDTO(clonedBlack, clonedWhite);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new UnsupportedOperationException("This type can not be cloned!");
            }
        }
    }
}