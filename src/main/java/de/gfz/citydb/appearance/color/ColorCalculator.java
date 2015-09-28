package de.gfz.citydb.appearance.color;

import java.awt.*;

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
    public Color calcX3dDiffuseColor(Double value) {
        if (value < minValForColor) {
            value = minValForColor;
        } else if (value > maxValForColor) {
            value = maxValForColor;
        }
        float r = (float) ((255 * value) / maxValForColor) / 255;
        float g = (float) ((255 * (maxValForColor - value)) / maxValForColor) / 255;
        float b = (float) 0.0;
//        return new RGBColor(r, g, 0.0);
        return new Color(r, g, b);
    }

//    public Color calcX3dDiffuseColor(Double value) {
//        if (value > maxValForColor) {
//            value = maxValForColor;
//        }
//
//        float h = 0;
//        float v = 1;
//
//        double saturation = (value - minValForColor) / (maxValForColor - minValForColor);
//        float s = (float) saturation;
//
//        int rgb = Color.HSBtoRGB(h, s, v);
//        return new Color(rgb);
//    }
}
