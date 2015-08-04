package de.gfz.citydb.appearance;

import org.citygml4j.model.citygml.generics.DoubleAttribute;

import java.util.List;

/**
 * Created by richard on 30.06.15.
 */
public class Util {

    /**
     * Calculates x3dDiffuseColor for given value and given max value.
     *
     * @param value
     * @param max
     * @return
     */
    public static Double[] calcX3dDiffuseColor(Double value, double max) {
        Double r = ((255 * value) / max) / 255;
        Double g = ((255 * (max - value)) / max) / 255;
        Double[] rgb = {r, g, 0.0};
        return rgb;
    }

    /**
     * Calculates the mean of the attribute values of all given {@link GenericAttributeWithCityobject< DoubleAttribute >}'s.
     *
     * @param genericAttributes
     * @return
     */
    public static double calcMean(
            List<GenericAttributeWithCityobject<DoubleAttribute>> genericAttributes) {
        double sum = 0.0;
        for (GenericAttributeWithCityobject<DoubleAttribute> attrib : genericAttributes) {
            DoubleAttribute doubleAttribute = attrib.getGenericAttribute();
            sum += doubleAttribute.getValue();
        }
        return sum / genericAttributes.size();
    }

}
