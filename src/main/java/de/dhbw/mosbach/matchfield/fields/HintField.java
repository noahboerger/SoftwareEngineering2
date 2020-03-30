package de.dhbw.mosbach.matchfield.fields;

public class HintField extends AbstractField {

    private final ArrowDirection arrowDirection;
    private final int amount;

    public HintField(ArrowDirection arrowDirection, int amount) {
        super();
        this.arrowDirection = arrowDirection;
        this.amount = amount;
    }

    public enum ArrowDirection {
        UP('↑'), RIGHT('→'), DOWN('↓'), LEFT('←');

        private final char arrowSymbol;

        ArrowDirection(char arrowSymbol) {

            this.arrowSymbol = arrowSymbol;
        }

        public char getArrowSymbol() {
            return arrowSymbol;
        }
    }

    public ArrowDirection getArrowDirection() {
        return arrowDirection;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "HintField [" + super.toString() +" ArrowDirection: " + arrowDirection + " Amount: " + amount +"]";
    }

    @Override
    public Field deepCopy() {
        HintField copy = new HintField(this.arrowDirection, this.amount);
        copy.setFieldState(this.fieldState);
        return copy;
    }
}
