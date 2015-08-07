package de.gfz.citydb.appearance;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by richard on 07.08.15.
 */
public class AppearanceRemover {

    private static final String DELETE_APPEAR_TO_SURFACE = "DELETE FROM citydb.appear_to_surface_data WHERE surface_data_id IN (SELECT id FROM citydb.surface_data WHERE name=?)";
    private static final String DELETE_TEXTUREPARAM = "DELETE FROM citydb.textureparam WHERE surface_data_id IN (SELECT id FROM citydb.surface_data WHERE name=?)";
    private static final String DELETE_SURFACE_DATA = "DELETE FROM citydb.surface_data WHERE name=?";
    private static final String DELETE_APPEARANCE = "DELETE FROM citydb.appearance WHERE theme=?";

    private static final Logger LOGGER = LogManager
            .getLogger(AppearanceRemover.class);
    private Connection connection;

    public AppearanceRemover(Connection connection) {
        this.connection = connection;
    }

    public void removeAppearanceAndSurfaceData(String theme, String surfaceData) {
        LOGGER.warn("Will delete theme " + theme + " and surface-data " + surfaceData);
        try {
            deleteAppearToSurface(surfaceData);
            deleteTextureParam(surfaceData);
            deleteSurfaceData(surfaceData);
            deleteAppearance(theme);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteAppearToSurface(String surfaceData) throws SQLException {
        PreparedStatement st = connection.prepareStatement(DELETE_APPEAR_TO_SURFACE);
        st.setString(1, surfaceData);
        st.executeUpdate();
    }

    private void deleteTextureParam(String surfaceData) throws SQLException {
        PreparedStatement st = connection.prepareStatement(DELETE_TEXTUREPARAM);
        st.setString(1, surfaceData);
        st.executeUpdate();
    }

    private void deleteSurfaceData(String surfaceData) throws SQLException {
        PreparedStatement st = connection.prepareStatement(DELETE_SURFACE_DATA);
        st.setString(1, surfaceData);
        st.executeUpdate();
    }

    private void deleteAppearance(String theme) throws SQLException {
        PreparedStatement st = connection.prepareStatement(DELETE_APPEARANCE);
        st.setString(1, theme);
        st.executeUpdate();
    }

}
