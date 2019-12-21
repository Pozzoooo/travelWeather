package pozzo.apps.travelweather.forecast

import android.content.Context
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.*
import org.junit.Test
import pozzo.apps.travelweather.R
import pozzo.apps.travelweather.forecast.model.Forecast

class ForecastTitleFormatterTest {
    private val forecastTitleFormatter = ForecastTitleFormatter()

    @Test fun format() {
        val context = mock<Context> {
            on { getString(R.string.min) } doReturn MIN
            on { getString(R.string.max) } doReturn MAX
            on { getString(R.string.forecast_breezy) } doReturn TITLE
        }
        val forecast = Forecast(TITLE, ForecastType.BREEZY, HIGH, LOW)

        val title = forecastTitleFormatter.createTitle(context, forecast)

        assertEquals("$TITLE - $MIN: $LOW $MAX: $HIGH", title)
    }

    private companion object {
        const val TITLE = "breezy"
        const val MIN = "min"
        const val MAX = "max"
        const val HIGH = 10.1
        const val LOW = 9.3
    }
}
