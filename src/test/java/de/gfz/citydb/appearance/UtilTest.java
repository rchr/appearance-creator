package de.gfz.citydb.appearance;

import junit.framework.TestCase;

/**
 * Created by richard on 07.08.15.
 */
public class UtilTest extends TestCase {

    public void testCalcX3dDiffuseColorValGreaterMax() throws Exception {
        Double value = 100.0;
        double max = 99.0;
        Double[] color = Util.calcX3dDiffuseColor(value, max);

        assertNotNull(color);

        assertEquals(1.0, color[0]);
        assertEquals(0.0, color[1]);
        assertEquals(0.0, color[2]);
    }

    public void testCalcX3DiffuseColorValZero() throws Exception {
        Double value = 0.0;
        double max = 99.0;
        Double[] color = Util.calcX3dDiffuseColor(value, max);

        assertNotNull(color);
        assertEquals(0.0, color[0]);
        assertEquals(1.0, color[1]);
        assertEquals(0.0, color[2]);
    }

    public void testCalcX3DiffuseColorValMax() throws Exception {
        Double value = 99.0;
        double max = 99.0;
        Double[] color = Util.calcX3dDiffuseColor(value, max);

        assertNotNull(color);
        assertEquals(1.0, color[0]);
        assertEquals(0.0, color[1]);
        assertEquals(0.0, color[2]);
    }

}