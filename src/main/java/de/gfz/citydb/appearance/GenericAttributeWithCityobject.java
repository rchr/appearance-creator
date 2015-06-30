package de.gfz.citydb.appearance;

import org.citygml4j.model.citygml.generics.AbstractGenericAttribute;

/**
 * POJO holding an {@link AbstractGenericAttribute} with the id of its cityobject.
 *
 * @param <T>
 */
public class GenericAttributeWithCityobject<T extends AbstractGenericAttribute> {

    private T genericAttribute;
    private int cityobjectID;

    public GenericAttributeWithCityobject(T genericAttribute, int cityobjectID) {
        this.genericAttribute = genericAttribute;
        this.cityobjectID = cityobjectID;
    }

    public T getGenericAttribute() {
        return genericAttribute;
    }

    public int getCityobjectID() {
        return cityobjectID;
    }

}
