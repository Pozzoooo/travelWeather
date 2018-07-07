package pozzo.apps.travelweather.forecast

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import pozzo.apps.travelweather.common.android.BitmapCreator
import pozzo.apps.travelweather.common.android.BitmapCreatorTest

class ForecastTypeTest {

    @Test fun shouldBeCreatingAndReusingTheIcon() {
        BitmapCreator.setInstance(BitmapCreatorTest())

        val icon1 = ForecastType.SUNNY.getIcon()
        val icon2 = ForecastType.SUNNY.getIcon()
        val icon3 = ForecastType.MOSTLY_SUNNY.getIcon()

        assertEquals(icon1, icon2)
        assertNotEquals(icon1, icon3)
    }
}
