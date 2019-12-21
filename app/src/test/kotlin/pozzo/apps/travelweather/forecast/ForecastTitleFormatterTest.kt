package pozzo.apps.travelweather.forecast

import android.content.Context
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import pozzo.apps.travelweather.R
import pozzo.apps.travelweather.forecast.model.Forecast
import java.util.*

class ForecastTitleFormatterTest {
    private val forecastTitleFormatter = ForecastTitleFormatter()

    private val context = mock<Context> {
        on { getString(R.string.min) } doReturn MIN
        on { getString(R.string.max) } doReturn MAX
        on { getString(R.string.forecast_breezy) } doReturn TITLE
    }

    @Test fun shouldParseToCelsiusTitle() {
        Locale.setDefault(Locale.UK)
        val forecast = Forecast(TITLE, ForecastType.BREEZY, HIGH_F, LOW_F)

        val title = forecastTitleFormatter.createTitle(context, forecast)

        assertEquals("$TITLE - $MIN: $LOW_C°C $MAX: $HIGH_C°C", title)
    }

    @Test fun shouldParseToFarenhitTitle() {
        Locale.setDefault(Locale.US)
        val forecast = Forecast(TITLE, ForecastType.BREEZY, HIGH_F, LOW_F)

        val title = forecastTitleFormatter.createTitle(context, forecast)

        assertEquals("$TITLE - $MIN: $LOW_F°F $MAX: $HIGH_F°F", title)
    }

    private companion object {
        const val TITLE = "breezy"
        const val MIN = "min"
        const val MAX = "max"
        const val HIGH_F = 50.1
        const val HIGH_C = 10.1
        const val LOW_F = 20.3
        const val LOW_C = -6.5
    }
}
