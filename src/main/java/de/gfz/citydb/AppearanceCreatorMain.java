package de.gfz.citydb;

import de.gfz.citydb.appearance.AppearanceCreator;
import de.gfz.citydb.appearance.GenericAttributeGetter;
import de.gfz.citydb.appearance.GenericAttributeWithCityobject;
import de.gfz.citydb.io.DBConnector;
import org.citygml4j.model.citygml.generics.DoubleAttribute;

import java.sql.Connection;
import java.util.List;

public class AppearanceCreatorMain {

    private static final String DB_USERNAME = "";
    private static final String DB_PASSWORD = "";
    private static final String DB_HOST = "";
    private static final String DB_PORT = "";
    private static final String DB_NAME = "";
    private static final String GENERIC_ATTRIBUTE_NAME = "";

    public static void main(String[] args) {

        DBConnector dbConnector = new DBConnector(DB_USERNAME, DB_PASSWORD, DB_HOST, DB_PORT, DB_NAME);
        Connection connection = dbConnector.openDbConnection();

        GenericAttributeGetter genericAttributeGetter = new GenericAttributeGetter(connection);
        List<GenericAttributeWithCityobject<DoubleAttribute>> genericAttributes = genericAttributeGetter.getGenericAttributes(GENERIC_ATTRIBUTE_NAME);

        AppearanceCreator appearanceCreator = new AppearanceCreator(connection);
        appearanceCreator.createAppearances(genericAttributes);
        dbConnector.commit();
        dbConnector.closeDBConnection();

    }
}
