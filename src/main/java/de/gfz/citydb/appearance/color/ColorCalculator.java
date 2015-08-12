package de.gfz.citydb.appearance.color;

/**
 * Created by richard on 11.08.15.
 *
 * Calculates color value in color range.
 */
public class ColorCalculator {

    private double minValForColor, maxValForColor;

    /**
     *
     * @param minValForColor Minimum value used for color coding. That means, if value is smaller than this one, minValForColor will be used.
     * @param maxValForColor Maximum value used for color coding. That means, if value is greater than this one, maxValForColor will be used.
     */
    public ColorCalculator(double minValForColor, double maxValForColor) {
        this.minValForColor = minValForColor;
        this.maxValForColor = maxValForColor;
    }

    public double getMinValForColor() {
        return minValForColor;
    }

    public double getMaxValForColor() {
        return maxValForColor;
    }

    /**
     * Calculates x3d diffuse color in color range from green to red.
     * @param value
     * @return
     */
    public RGBColor calcX3dDiffuseColor(Double value) {
        if (value < minValForColor) {
            value = minValForColor;
        } else if (value > maxValForColor) {
            value = maxValForColor;
        }
        double r = ((255 * value) / maxValForColor) / 255;
        double g = ((255 * (maxValForColor - value)) / maxValForColor) / 255;

        return new RGBColor(r, g, 0.0);
    }
}
