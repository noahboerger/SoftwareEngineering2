package de.dhbw.mosbach.solve;

import de.dhbw.mosbach.matchfield.MatchField;
import de.dhbw.mosbach.matchfield.fields.Field;
import de.dhbw.mosbach.matchfield.utils.Direction;

import java.util.List;

final class SolverUtils {
    private SolverUtils() {
        throw new IllegalStateException("Uninstanzierbare Klasse");
    }

    public static int calculateMaxPossibleBlackFieldsToDirection(MatchField matchField, Field field, Direction rowDirection) {
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
                    for (Direction allDirections : Direction.values()) {
                        Field actNeighbourField = matchField.getNeighbourTo(actField, allDirections);
                        if (actNeighbourField == null) {
                            continue;
                        }
                        if (actNeighbourField.getFieldState() == Field.State.BLACK) {
                            potentialBlackField = false;
                            break;
                        }
                    }
                    if (potentialBlackField) {
                        beforePotentialBlackField = true;
                        counter++;
                    } else {
                        beforePotentialBlackField = false;
                    }
            }
        }
        return counter;
    }
}
