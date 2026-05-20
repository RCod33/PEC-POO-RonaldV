package Components;

import java.util.Objects;

public class Wheel {

    private final  WheelType type;

    private final int widthMm;
    private final int rimDiameterInches;
    private final int loadIndexKg;
    private final int speedCode;

    public Wheel(WheelType type, int widthMm, int rimDiameterInches, int loadIndexKg, int speedCode) {
        if (widthMm <= 0) {
            throw new IllegalArgumentException("El ancho de la rueda debe ser positivo");
        } if (rimDiameterInches <= 0) {
            throw new IllegalArgumentException("El diámetro de la rueda debe ser positivo");
        } if (loadIndexKg <= 0) {
            throw new IllegalArgumentException("El índice de carga debe ser positivo");
        } if (speedCode <= 0) {
            throw new IllegalArgumentException("El código de velocidad debe ser positivo");
        }
        this.type = type;
        this.widthMm = widthMm;
        this.rimDiameterInches = rimDiameterInches;
        this.loadIndexKg = loadIndexKg;
        this.speedCode = speedCode;
    }

    public WheelType getType() {
        return type;
    }

    public int getWidthMm() {
        return widthMm;
    }

    public int getRimDiameterInches() {
        return rimDiameterInches;
    }

    public int getLoadIndexKg() {
        return loadIndexKg;
    }

    public int getSpeedCode() {
        return speedCode;
    }

    @Override
    public String toString() {
        return type + " " + widthMm + "mm " + rimDiameterInches + "in " +
                loadIndexKg + "kg " + speedCode;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Wheel)) {
            return false;
        }

        Wheel other = (Wheel) obj;

        return  type == other.type
                && widthMm == other.widthMm
                && rimDiameterInches == other.rimDiameterInches
                && loadIndexKg == other.loadIndexKg
                && speedCode == other.speedCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, widthMm, rimDiameterInches, loadIndexKg, speedCode);
    }

}
