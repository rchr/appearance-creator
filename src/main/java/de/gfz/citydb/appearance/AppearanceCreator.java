package de.gfz.citydb.appearance;

import de.gfz.citydb.appearance.color.ColorCalculator;
import de.gfz.citydb.appearance.color.RGBColor;
import de.gfz.citydb.statisics.StatisticsCalculator;
import de.gfz.citydb.statisics.model.Quartile;
import org.citygml4j.model.citygml.generics.DoubleAttribute;
import org.citygml4j.util.gmlid.DefaultGMLIdManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class AppearanceCreator {

    private static final String INSERT_INTO_APPEAR_TO_SURFACE = "INSERT INTO citydb.appear_to_surface_data (surface_data_id, appearance_id) VALUES (?, ?)";
    private static final String SELECT_FROM_THEMATIC_SURFACE = "SELECT * FROM thematic_surface WHERE building_id = ?";
    private static final String INSERT_INTO_TEXTUREPARAM = "INSERT INTO citydb.textureparam (surface_geometry_id, is_texture_parametrization, surface_data_id) VALUES (?, ?, ?)";
    private static final String INSERT_INTO_APPEARANCE = "INSERT INTO citydb.appearance (gmlid, theme, cityobject_id) VALUES (?, ?, ?)";
    private static final String SELECT_FROM_APPEARANCE = "SELECT id FROM citydb.appearance WHERE cityobject_id = ? AND theme = ?";
    private static final String INSERT_INTO_SURFACE_DATA = "INSERT INTO citydb.surface_data (gmlid, name, is_front, objectclass_id, x3d_shininess, x3d_transparency, x3d_ambient_intensity, x3d_specular_color, x3d_diffuse_color, x3d_emissive_color, x3d_is_smooth) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_FROM_SURFACE_DATA = "SELECT id FROM citydb.surface_data WHERE gmlid = ?";
    private static final String INSERT_GENERIC_ATTRIB = "WITH \n" +
            "cityobj_id AS (\n" +
            "\tSELECT id, objectclass_id\n" +
            "\tFROM citydb.cityobject\n" +
            "\tWHERE gmlid = 'BLDG_GLOBAL_ATTRIBS'\n" +
            ")\n" +
            "INSERT INTO citydb.cityobject_genericattrib (cityobject_id, attrname, datatype, realval)\n" +
            "SELECT id, ?, 3, ? FROM cityobj_id";
    private static final String DELETE_GENERIC_ATTRIB = "DELETE FROM citydb.cityobject_genericattrib WHERE attrname = ?";
    private static final String MAX_ATTRIB_VAL_4_COLOR = "MAX_ATTRIB_VAL_4_COLOR";
    private static final String MIN_ATTRIB_VAL_4_COLOR = "MIN_ATTRIB_VAL_4_COLOR";


    private Connection connection;
    private ColorCalculator colorCalculator;

    public AppearanceCreator(Connection connection) {
        super();
        this.connection = connection;
    }

    /**
     * Creates appearances for given {@link List<GenericAttributeWithCityobject<Double>>}.
     *
     * @param genericAttributes
     */
    public void createAppearances(List<GenericAttributeWithCityobject<DoubleAttribute>> genericAttributes, Set<Integer> cityobjectsWOAttrib, String themeName, String surfaceDataName) {
        StatisticsCalculator statisticsCalculator = new StatisticsCalculator(genericAttributes);
        Quartile quartile = statisticsCalculator.calcQuartiles();
        // ColorCalculator to calculate colors in range from first to third quartile
        this.colorCalculator = new ColorCalculator(quartile.getFirst(), quartile.getThird());

        try {
            for (GenericAttributeWithCityobject<DoubleAttribute> attribute : genericAttributes) {
                createAppearanceForCityobject(attribute, themeName, surfaceDataName);
            }
            for (Integer id : cityobjectsWOAttrib) {
                createAppearanceForCityObjectWOAttrib(id, themeName, surfaceDataName);
            }
            insertAttribValuesForColor(quartile);
        } catch (SQLException e) {
            throw new RuntimeException("Cannot create appearance.", e);
        }
    }

    private void createAppearanceForCityObjectWOAttrib(Integer id, String themeName, String surfaceDataName) throws SQLException {
        int appearanceID = insertIntoAppearance(themeName, id);

        ResultSet rs = selectThematicSurfaces(id);
        while (rs.next()) {
            int lod2MultiSurfaceID = rs.getInt("lod2_multi_surface_id");
            RGBColor x3dDiffuseColor = new RGBColor(0.75, 0.75, 0.75);
            double x3dTransparency = 0.8;
            int surfaceDataID = insertIntoSurfaceData(surfaceDataName, x3dDiffuseColor, x3dTransparency);
            insertIntoAppearToSurface(surfaceDataID, appearanceID);
            insertIntoTextureparam(lod2MultiSurfaceID, surfaceDataID);
        }
    }

    /**
     * Creates appearance for one {@link GenericAttributeWithCityobject<Double>}.
     *
     * @param attribute
     * @param themeName
     * @param surfaceDataName
     * @throws SQLException
     */
    private void createAppearanceForCityobject(GenericAttributeWithCityobject<DoubleAttribute> attribute, String themeName, String surfaceDataName) throws SQLException {
        DoubleAttribute genericAttribute = attribute.getGenericAttribute();
        int appearanceID = insertIntoAppearance(themeName, attribute.getCityobjectID());
        ResultSet rs = selectThematicSurfaces(attribute.getCityobjectID());
        while (rs.next()) {
            int lod2MultiSurfaceID = rs.getInt("lod2_multi_surface_id");

            Double value = 0.0;
            if (genericAttribute instanceof DoubleAttribute) {
                value = ((DoubleAttribute) genericAttribute).getValue();
            }
            RGBColor x3dDiffuseColor = colorCalculator.calcX3dDiffuseColor(value);
            double x3dTransparency = 0.15;
            int surfaceDataID = insertIntoSurfaceData(surfaceDataName, x3dDiffuseColor, x3dTransparency);
            insertIntoAppearToSurface(surfaceDataID, appearanceID);
            insertIntoTextureparam(lod2MultiSurfaceID, surfaceDataID);
        }
    }

    /**
     * Inserts values into table citydb.textureparam.
     *
     * @param lod2MultiSurfaceID ID of thematic LOD2 surface.
     * @param surfaceDataID      ID of surface data.
     * @throws SQLException
     */
    private void insertIntoTextureparam(int lod2MultiSurfaceID, int surfaceDataID) throws SQLException {
        PreparedStatement psTextureParam = connection
                .prepareStatement(INSERT_INTO_TEXTUREPARAM);
        psTextureParam.setInt(1, lod2MultiSurfaceID);
        psTextureParam.setInt(2, 0);
        psTextureParam.setInt(3, surfaceDataID);
        psTextureParam.executeUpdate();
    }

    /**
     * Inserts values into citydb.appear_to_surface_data.
     *
     * @param surfaceDataID ID of surface data.
     * @param appearanceID  ID of appearance.
     * @throws SQLException
     */
    private void insertIntoAppearToSurface(int surfaceDataID, int appearanceID) throws SQLException {
        PreparedStatement st = connection.prepareStatement(INSERT_INTO_APPEAR_TO_SURFACE);
        st.setInt(1, surfaceDataID);
        st.setInt(2, appearanceID);
        st.executeUpdate();
    }

    /**
     * Select all thematic surfaces for a given building.
     *
     * @param buildingID
     * @return
     * @throws SQLException
     */
    private ResultSet selectThematicSurfaces(int buildingID) throws SQLException {
        PreparedStatement stThematicSurfaces = connection
                .prepareStatement(SELECT_FROM_THEMATIC_SURFACE);
        stThematicSurfaces.setInt(1, buildingID);
        ResultSet rs = stThematicSurfaces.executeQuery();
        return rs;
    }

    /**
     * Inserts values into appearance and returns id of created appearance.
     *
     * @param theme
     * @param cityobjectID
     * @return
     * @throws SQLException
     */
    private int insertIntoAppearance(String theme, int cityobjectID) throws SQLException {
        String uuid = DefaultGMLIdManager.getInstance().generateUUID();
        int appearanceID = -1;
        PreparedStatement st = connection
                .prepareStatement(INSERT_INTO_APPEARANCE);
        st.setString(1, uuid);
        st.setString(2, theme);
        st.setInt(3, cityobjectID);
        st.executeUpdate();
        st = connection.prepareStatement(SELECT_FROM_APPEARANCE);
        st.setInt(1, cityobjectID);
        st.setString(2, theme);
        ResultSet rs = st.executeQuery();
        rs.next();
        appearanceID = rs.getInt("id");
        return appearanceID;
    }

    /**
     * Inserts values into surface_data and returns id of created entry.
     *
     * @param name
     * @param x3dDiffuseColor
     * @return
     * @throws SQLException
     */
    private int insertIntoSurfaceData(String name, RGBColor x3dDiffuseColor, double x3d_transparency) throws SQLException {
        String uuid = DefaultGMLIdManager.getInstance().generateUUID();
        int isFront = 1;
        int objectclassID = 53;
        int x3dShininess = 0;
        double x3dAmbientIntensity = 0.2;
        String x3dSpecularColor = "0.0 0.0 0.0";
        String x3dDifColor = x3dDiffuseColor.getRed() + " " + x3dDiffuseColor.getGreen()
                + " " + x3dDiffuseColor.getBlue();
        String x3dEmissiveColor = "0.0 0.0 0.0";
        int x3dIsSmooth = 0;

        PreparedStatement st = connection
                .prepareStatement(INSERT_INTO_SURFACE_DATA);

        st.setString(1, uuid);
        st.setString(2, name);
        st.setInt(3, isFront);
        st.setInt(4, objectclassID);
        st.setInt(5, x3dShininess);
        st.setDouble(6, x3d_transparency);
        st.setDouble(7, x3dAmbientIntensity);
        st.setString(8, x3dSpecularColor);
        st.setString(9, x3dDifColor);
        st.setString(10, x3dEmissiveColor);
        st.setInt(11, x3dIsSmooth);
        st.executeUpdate();

        st = connection.prepareStatement(SELECT_FROM_SURFACE_DATA);
        st.setString(1, uuid);
        ResultSet rs = st.executeQuery();
        rs.next();
        int surfaceDataID = rs.getInt("id");
        return surfaceDataID;
    }

    /**
     * Inserts value considered as max value into database.
     * Useful for visualizations.
     * @param max
     * @throws SQLException
     */
    private void insertMaxAttribValueForColor(double max) throws SQLException {
        PreparedStatement st = connection.prepareStatement(INSERT_GENERIC_ATTRIB);
        st.setDouble(1, max);
        st.executeUpdate();
    }

    private void insertAttribValuesForColor(Quartile quartile) throws SQLException {
        deleteMinMaxAttribs4Color();

        PreparedStatement st = connection.prepareStatement(INSERT_GENERIC_ATTRIB);
        st.setString(1, MIN_ATTRIB_VAL_4_COLOR);
        st.setDouble(2, quartile.getFirst());
        st.executeUpdate();

        st = connection.prepareStatement(INSERT_GENERIC_ATTRIB);
        st.setString(1, MAX_ATTRIB_VAL_4_COLOR);
        st.setDouble(2, quartile.getThird());
        st.executeUpdate();
        st.close();
    }

    private void deleteMinMaxAttribs4Color() throws SQLException {
        PreparedStatement st = connection.prepareStatement(DELETE_GENERIC_ATTRIB);
        st.setString(1, MIN_ATTRIB_VAL_4_COLOR);
        st.executeUpdate();

        st.setString(1, MAX_ATTRIB_VAL_4_COLOR);
        st.executeUpdate();
        st.close();
    }

}
