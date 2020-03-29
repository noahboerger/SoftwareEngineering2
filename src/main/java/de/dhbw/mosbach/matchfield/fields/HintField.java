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
        UP, RIGHT, DOWN, LEFT
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
}
