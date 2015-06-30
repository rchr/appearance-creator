package de.gfz.citydb.appearance;

import org.citygml4j.model.citygml.generics.DoubleAttribute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GenericAttributeGetter {

    private static final String GENERIC_ATTRIBUTE_QUERY = "SELECT * FROM citydb.cityobject_genericattrib WHERE attrname=?";
    private static final String REALVAL = "realval";
    private static final String ATTRNAME = "attrname";
    private static final String CITYOBJECT_ID = "cityobject_id";
    private static final String DATATYPE = "datatype";
    private Connection connection;

    public GenericAttributeGetter(Connection connection) {
        super();
        this.connection = connection;
    }

    /**
     * Returns list of {@link GenericAttributeWithCityobject<DoubleAttribute>}s for given attrname.
     *
     * @param attrname
     * @return
     */
    public List<GenericAttributeWithCityobject<DoubleAttribute>> getGenericAttributes(String attrname) {

        List<GenericAttributeWithCityobject<DoubleAttribute>> genericAttributes = new ArrayList<>();

        try {
            PreparedStatement st = connection
                    .prepareStatement(GENERIC_ATTRIBUTE_QUERY);
            st.setString(1, attrname);
            ResultSet rs = st.executeQuery();
            genericAttributes = resultSetToGenericAttributes(rs);
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException("Cannot get generic attributes.", e);
        }
        return genericAttributes;
    }

    /**
     * Converts {@link ResultSet} to {@link List<GenericAttributeWithCityobject<DoubleAttribute>>}.
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    private List<GenericAttributeWithCityobject<DoubleAttribute>> resultSetToGenericAttributes(ResultSet rs) throws SQLException {
        List<GenericAttributeWithCityobject<DoubleAttribute>> attributes = new ArrayList<>();
        while (rs.next()) {
            int cityObjectID = rs.getInt(CITYOBJECT_ID);
            String attrname = rs.getString(ATTRNAME);
            int dataType = rs.getInt(DATATYPE);
            if (dataType == 3) { // we have a realval
                double realval = rs.getDouble(REALVAL);
                DoubleAttribute attribute = new DoubleAttribute();
                attribute.setName(attrname);
                attribute.setValue(realval);
                GenericAttributeWithCityobject<DoubleAttribute> genericAttribute = new GenericAttributeWithCityobject<>(attribute, cityObjectID);
                attributes.add(genericAttribute);
            }
        }
        return attributes;
    }

}
