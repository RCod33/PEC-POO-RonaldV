package Components;

import java.util.Objects;

public class Upholstery {

    private final UpholsteryType type;

    private final String color;
    private final double squareMeters;

    public Upholstery(UpholsteryType type, String color, double squareMeters) {
        if (squareMeters <= 0) {
            throw new IllegalArgumentException("El tamaño de la tapiceria debe ser positivo");
        } if (color == null || color.isBlank()) {
            throw new IllegalArgumentException("Color no puede estar vacío");
        }

        this.type = type;
        this.color = color;
        this.squareMeters = squareMeters;
    }

    public UpholsteryType getType() {
        return type;
    }

    public String getColor() {
        return color;
    }

    public double getSquareMeters() {
        return squareMeters;
    }

    @Override
    public String toString() {
        return type + " " + color + " " + squareMeters + "m²";
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Upholstery)) {
            return false;
        }

        Upholstery other = (Upholstery) obj;

        return Objects.equals(color, other.color)
                && type == other.type
                && squareMeters == other.squareMeters;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color, squareMeters);
    }

}

