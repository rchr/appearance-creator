package de.gfz.citydb.appearance.color;

/**
 * Created by richard on 11.08.15.
 *
 * Represents an RGB color.
 *
 */
public class RGBColor {

    private double red, green, blue;

    public RGBColor(double red, double green, double blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public double getRed() {
        return red;
    }

    public double getGreen() {
        return green;
    }

    public double getBlue() {
        return blue;
    }
}
