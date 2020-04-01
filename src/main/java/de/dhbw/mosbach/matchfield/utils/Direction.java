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
}


