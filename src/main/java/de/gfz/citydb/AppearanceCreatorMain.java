package de.gfz.citydb;

import de.gfz.citydb.appearance.AppearanceCreator;
import de.gfz.citydb.appearance.GenericAttributeGetter;
import de.gfz.citydb.appearance.GenericAttributeWithCityobject;
import de.gfz.citydb.io.DBConnector;
import org.apache.commons.cli.*;
import org.citygml4j.model.citygml.generics.DoubleAttribute;

import java.sql.Connection;
import java.util.List;
import java.util.Set;

public class AppearanceCreatorMain {

    public static void main(String[] args) {

        CommandLineParser parser = new DefaultParser();

        Option username = Option.builder("u")
                .desc("Database Username")
                .hasArg()
                .build();

        Option password = Option.builder("pw")
                .desc("Database Password")
                .hasArg()
                .build();

        Option host = Option.builder("host")
                .required(false)
                .desc("Database host")
                .hasArg()
                .build();

        Option port = Option.builder("p")
                .required(false)
                .desc("Database port")
                .hasArg()
                .build();

        Option name = Option.builder("d")
                .required(false)
                .desc("Database name")
                .hasArg()
                .build();

        Option genAttr = Option.builder("g")
                .required(false)
                .desc("Name of generic attribute")
                .hasArg()
                .build();

        Option theme = Option.builder("t")
                .required(false)
                .desc("Name of theme to be created")
                .hasArg()
                .build();

        Option surfData = Option.builder("s")
                .required(false)
                .desc("Name of surface data to be created")
                .hasArg()
                .build();

        Option help = new Option("h", "print this message");

        Options options = new Options();
        options.addOption(username);
        options.addOption(password);
        options.addOption(host);
        options.addOption(port);
        options.addOption(name);
        options.addOption(genAttr);
        options.addOption(theme);
        options.addOption(surfData);
        options.addOption(help);

        String dbUsername = null;
        String dbPassword = null;
        String dbHost = null;
        String dbPort = null;
        String dbName = null;
        String genericAttribute = null;
        String themeName = null;
        String surfaceDateName = null;

        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("is24Importer", options);
                System.exit(-1);
            }
            if (line.hasOption("u")) {
                dbUsername = line.getOptionValue("u");
            } else {
                throw new RuntimeException("Username is required!");
            }
            if (line.hasOption("pw")) {
                dbPassword = line.getOptionValue("pw");
            } else {
                throw new RuntimeException("Password is required!");
            }
            if (line.hasOption("host")) {
                dbHost = line.getOptionValue("host");
            } else {
                throw new RuntimeException("Host is required!");
            }
            if (line.hasOption("p")) {
                dbPort = line.getOptionValue("p");
            } else {
                throw new RuntimeException("Port is required!");
            }
            if (line.hasOption("d")) {
                dbName = line.getOptionValue("d");
            }
            if (line.hasOption("g")) {
                genericAttribute = line.getOptionValue("g");
            }
            if (line.hasOption("t")) {
                themeName = line.getOptionValue("t");
            }
            if (line.hasOption("s")) {
                surfaceDateName = line.getOptionValue("s");
            } else {
                throw new RuntimeException("Database name is required!");
            }

        } catch (ParseException exp) {
            throw new RuntimeException("Unexpected exception:" + exp.getMessage());
        }
        AppearanceCreatorMain.run(dbUsername, dbPassword, dbHost, dbPort, dbName, genericAttribute, themeName, surfaceDateName);
    }

    public static void run(String dbUsername, String dbPassword, String dbHost, String dbPort, String dbName, String genericAttribute, String themeName, String surfaceDateName) {
        DBConnector dbConnector = new DBConnector(dbUsername, dbPassword, dbHost, dbPort, dbName);
        Connection connection = dbConnector.openDbConnection();

        GenericAttributeGetter genericAttributeGetter = new GenericAttributeGetter(connection);
        List<GenericAttributeWithCityobject<DoubleAttribute>> genericAttributes = genericAttributeGetter.getGenericAttributes(genericAttribute);
        Set<Integer> cityObjectsWOGenericAttrib = genericAttributeGetter.getCityObjectsWOGenericAttributes(genericAttribute);

        AppearanceCreator appearanceCreator = new AppearanceCreator(connection);
        appearanceCreator.createAppearances(genericAttributes, cityObjectsWOGenericAttrib, themeName, surfaceDateName);
        dbConnector.commit();
        dbConnector.closeDBConnection();
    }
}
