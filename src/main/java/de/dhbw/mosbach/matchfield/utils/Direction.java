package de.dhbw.mosbach.matchfield.utils;

public enum Direction {

    UP('↑'), RIGHT('→'), DOWN('↓'), LEFT('←');

    private final char arrowSymbol;

    Direction(char arrowSymbol) {
        this.arrowSymbol = arrowSymbol;
    }

    public char toCharacter() {
        return arrowSymbol;
    }

    public Direction getOppositeDirection() {
        switch (this) {
            case UP:
                return DOWN;
            case RIGHT:
                return LEFT;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
        }
        return null;
    }
}


