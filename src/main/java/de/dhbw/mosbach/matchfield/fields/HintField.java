package de.dhbw.mosbach.matchfield.fields;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.dhbw.mosbach.matchfield.utils.Direction;

import java.util.Objects;

public final class HintField extends AbstractField {

    private final Direction arrowDirection;
    private final int amount;

    @JsonCreator
    public HintField(@JsonProperty("arrowDirection") final Direction arrowDirection, @JsonProperty("amount") final int amount) {
        super();
        this.arrowDirection = arrowDirection;
        this.amount = amount;
    }

    public Direction getArrowDirection() {
        return arrowDirection;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "HintField [" + super.toString() + " ArrowDirection: " + arrowDirection + " Amount: " + amount + "]";
    }

    @Override
    public Field deepCopy() {
        final HintField copy = new HintField(this.arrowDirection, this.amount);
        copy.setFieldState(this.fieldState);
        return copy;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final HintField hintField = (HintField) o;
        return amount == hintField.amount &&
                arrowDirection == hintField.arrowDirection &&
                fieldState == hintField.fieldState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(arrowDirection, amount, fieldState);
    }
}
