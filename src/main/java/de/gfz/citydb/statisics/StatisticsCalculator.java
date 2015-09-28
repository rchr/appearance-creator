package de.gfz.citydb.statisics;

import de.gfz.citydb.appearance.GenericAttributeWithCityobject;
import de.gfz.citydb.statisics.model.Quartile;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.citygml4j.model.citygml.generics.DoubleAttribute;

import java.util.List;

/**
 * Created by richard on 11.08.15.
 *
 * Wrapper to calculate statistics.
 */
public class StatisticsCalculator {

    private DescriptiveStatistics descriptiveStatistics;

    public StatisticsCalculator(List<GenericAttributeWithCityobject<DoubleAttribute>> genericAttributes) {
        init(genericAttributes);
    }

    private void init(List<GenericAttributeWithCityobject<DoubleAttribute>> genericAttributes) {
        double[] values = extractValues(genericAttributes);
        this.descriptiveStatistics = new DescriptiveStatistics(values);
    }

    /**
     * Extracts the values from the generic attributes.
     * @param genericAttributes
     * @return
     */
    private double[] extractValues(List<GenericAttributeWithCityobject<DoubleAttribute>> genericAttributes) {
        double[] vals = new double[genericAttributes.size()];
        for (int i = 0; i < genericAttributes.size(); i++) {
            GenericAttributeWithCityobject<DoubleAttribute> attrib = genericAttributes.get(i);
            DoubleAttribute doubleAttrib = attrib.getGenericAttribute();
            vals[i] = doubleAttrib.getValue();
        }
        return vals;
    }

    /**
     * Calculates the quartiles
     * @return {@link Quartile}.
     */
    public Quartile calcQuartiles() {
        double first = descriptiveStatistics.getPercentile(25);
        double second = descriptiveStatistics.getPercentile(50);
        double third = descriptiveStatistics.getPercentile(75);
        return new Quartile(first, second,third);
    }

}
