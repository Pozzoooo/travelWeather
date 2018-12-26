package pozzo.apps.travelweather.forecast.yahoo

import com.google.android.gms.maps.model.LatLng
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import pozzo.apps.travelweather.core.JsonParser
import pozzo.apps.travelweather.core.TestInjector
import pozzo.apps.travelweather.core.TestSettings
import pozzo.apps.travelweather.forecast.model.Weather

class ForecastClientYahooTest {
    private val coordinates = LatLng(40.781579, -74.358705)

    private lateinit var forecastClientYahoo: ForecastClientYahoo
    private var mockWebServer: MockWebServer? = null

    @Before fun setup() {
        val baseUrl = if (TestSettings.IS_INTEGRATION_TEST) {
            ForecastModuleYahoo().yahooBaseUrl()
        } else {
            getUrlFromMockedWebServer()
        }

        val appComponent = TestInjector.getAppComponent().build()
        val module = ForecastModuleYahoo()
        val yahooWeather = module.yahooWeather(appComponent.retrofitBuilder(), baseUrl)
        forecastClientYahoo = ForecastClientYahoo(yahooWeather, module.forecastTypeMapper())
    }

    private fun getUrlFromMockedWebServer() : String {
        val mockWebServer = MockWebServer()
        mockWebServer.enqueue(MockResponse().setBody(fakeResponse()))
        mockWebServer.start()
        this.mockWebServer = mockWebServer
        return mockWebServer.url("").toString()
    }

    @After fun tearDown() {
        mockWebServer?.shutdown()
    }

    @Test fun handleWeatherRequest() {
        val weather = forecastClientYahoo.fromCoordinates(coordinates)!!
        assertEquals(weatherExpectation(), weather)
    }

    private fun weatherExpectation() : Weather {
        return JsonParser.fromJson(Weather::class.java, """
{
  "address": {
    "latLng": {
      "latitude": ${coordinates.latitude},
      "longitude": ${coordinates.longitude}
    }
  },
  "forecasts": [
	{
	  "date": "03 Jul 2018",
	  "text": "Thunderstorms",
	  "forecastType": "THUNDERSTORMS",
	  "high": 31,
	  "low": 23
	},
	{
	  "date": "04 Jul 2018",
	  "text": "Mostly Cloudy",
	  "forecastType": "MOSTLY_CLOUDY",
	  "high": 31,
	  "low": 21
	},
	{
	  "date": "05 Jul 2018",
	  "text": "Thunderstorms",
	  "forecastType": "THUNDERSTORMS",
	  "high": 26,
	  "low": 20
	},
	{
	  "date": "06 Jul 2018",
	  "text": "Sunny",
	  "forecastType": "SUNNY",
	  "high": 25,
	  "low": 15
	},
	{
	  "date": "07 Jul 2018",
	  "text": "Mostly Sunny",
	  "forecastType": "MOSTLY_SUNNY",
	  "high": 27,
	  "low": 13
	},
	{
	  "date": "08 Jul 2018",
	  "text": "Mostly Sunny",
	  "forecastType": "MOSTLY_SUNNY",
	  "high": 30,
	  "low": 15
	},
	{
	  "date": "09 Jul 2018",
	  "text": "Rain",
	  "forecastType": "RAIN",
	  "high": 31,
	  "low": 18
	},
	{
	  "date": "10 Jul 2018",
	  "text": "Partly Cloudy",
	  "forecastType": "PARTLY_CLOUDY",
	  "high": 31,
	  "low": 19
	},
	{
	  "date": "11 Jul 2018",
	  "text": "Partly Cloudy",
	  "forecastType": "PARTLY_CLOUDY",
	  "high": 32,
	  "low": 18
	},
	{
	  "date": "12 Jul 2018",
	  "text": "Thunderstorms",
	  "forecastType": "THUNDERSTORMS",
	  "high": 31,
	  "low": 19
	}
  ],
  "url": "https://weather.yahoo.com/country/state/city-91558663/"
}
        """.trimIndent())
    }

    private fun fakeResponse() : String {
        return """
{
  "query": {
	"count": 1,
	"created": "2018-07-04T21:14:25Z",
	"lang": "en-US",
	"results": {
	  "channel": {
		"item": {
		  "title": "Conditions for Livingston, NJ, US at 04:00 PM EDT",
		  "lat": "${coordinates.latitude}",
		  "long": "${coordinates.longitude}",
		  "link": "http://us.rd.yahoo.com/dailynews/rss/weather/Country__Country/*https://weather.yahoo.com/country/state/city-91558663/",
		  "pubDate": "Wed, 04 Jul 2018 04:00 PM EDT",
		  "condition": {
			"code": "4",
			"date": "Wed, 04 Jul 2018 04:00 PM EDT",
			"temp": "26",
			"text": "Thunderstorms"
		  },
		  "forecast": [
			{
			  "code": "4",
			  "date": "04 Jul 2018",
			  "day": "Wed",
			  "high": "31",
			  "low": "23",
			  "text": "Thunderstorms"
			},
			{
			  "code": "28",
			  "date": "05 Jul 2018",
			  "day": "Thu",
			  "high": "31",
			  "low": "21",
			  "text": "Mostly Cloudy"
			},
			{
			  "code": "4",
			  "date": "06 Jul 2018",
			  "day": "Fri",
			  "high": "26",
			  "low": "20",
			  "text": "Thunderstorms"
			},
			{
			  "code": "32",
			  "date": "07 Jul 2018",
			  "day": "Sat",
			  "high": "25",
			  "low": "15",
			  "text": "Sunny"
			},
			{
			  "code": "34",
			  "date": "08 Jul 2018",
			  "day": "Sun",
			  "high": "27",
			  "low": "13",
			  "text": "Mostly Sunny"
			},
			{
			  "code": "34",
			  "date": "09 Jul 2018",
			  "day": "Mon",
			  "high": "30",
			  "low": "15",
			  "text": "Mostly Sunny"
			},
			{
			  "code": "12",
			  "date": "10 Jul 2018",
			  "day": "Tue",
			  "high": "31",
			  "low": "18",
			  "text": "Rain"
			},
			{
			  "code": "30",
			  "date": "11 Jul 2018",
			  "day": "Wed",
			  "high": "31",
			  "low": "19",
			  "text": "Partly Cloudy"
			},
			{
			  "code": "30",
			  "date": "12 Jul 2018",
			  "day": "Thu",
			  "high": "32",
			  "low": "18",
			  "text": "Partly Cloudy"
			},
			{
			  "code": "4",
			  "date": "13 Jul 2018",
			  "day": "Fri",
			  "high": "31",
			  "low": "19",
			  "text": "Thunderstorms"
			}
		  ],
		  "description": "<![CDATA[<img src=\"http://l.yimg.com/a/i/us/we/52/4.gif\"/>\n<BR />\n<b>Current Conditions:</b>\n<BR />Thunderstorms\n<BR />\n<BR />\n<b>Forecast:</b>\n<BR /> Wed - Thunderstorms. High: 31Low: 23\n<BR /> Thu - Mostly Cloudy. High: 31Low: 21\n<BR /> Fri - Thunderstorms. High: 26Low: 20\n<BR /> Sat - Sunny. High: 25Low: 15\n<BR /> Sun - Mostly Sunny. High: 27Low: 13\n<BR />\n<BR />\n<a href=\"http://us.rd.yahoo.com/dailynews/rss/weather/Country__Country/*https://weather.yahoo.com/country/state/city-91558663/\">Full Forecast at Yahoo! Weather</a>\n<BR />\n<BR />\n<BR />\n]]>",
		  "guid": {
			"isPermaLink": "false"
		  }
		}
	  }
	}
  }
}
                """.trimIndent()
    }
}
