package pozzo.apps.travelweather.forecast.model;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import pozzo.apps.travelweather.forecast.ForecastHelper;

public class Forecast {
    private String date;
    private String text;
    private int high;
    private int low;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }

    public BitmapDescriptor getIcon() {
		int icon = ForecastHelper.forecastIcon(this);
		return BitmapDescriptorFactory.fromResource(icon);
	}

    /*
    Exemplo de retorno do Yahoo
    {
   "query": {
      "count": 1,
      "created": "2015-10-19T23:57:04Z",
      "lang": "en-US",
      "results": {
         "channel": {
            "title": "Yahoo! Weather - Florianopolis, BR",
            "link": "http://us.rd.yahoo.com/dailynews/rss/weather/Florianopolis__BR/*http://weather.yahoo.com/forecast/BRXX0091_f.html",
            "description": "Yahoo! Weather for Florianopolis, BR",
            "language": "en-us",
            "lastBuildDate": "Mon, 19 Oct 2015 9:06 pm BRST",
            "ttl": "60",
            "location": {
               "city": "Florianopolis",
               "country": "Brazil",
               "region": "SC"
            },
            "units": {
               "distance": "mi",
               "pressure": "in",
               "speed": "mph",
               "temperature": "F"
            },
            "wind": {
               "chill": "66",
               "direction": "350",
               "speed": "13"
            },
            "atmosphere": {
               "humidity": "88",
               "pressure": "30.03",
               "rising": "0",
               "visibility": "2.49"
            },
            "astronomy": {
               "sunrise": "6:33 am",
               "sunset": "7:23 pm"
            },
            "image": {
               "title": "Yahoo! Weather",
               "width": "142",
               "height": "18",
               "link": "http://weather.yahoo.com",
               "url": "http://l.yimg.com/a/i/brand/purplelogo//uh/us/news-wea.gif"
            },
            "item": {
               "title": "Conditions for Florianopolis, BR at 9:06 pm BRST",
               "lat": "-27.62",
               "long": "-48.51",
               "link": "http://us.rd.yahoo.com/dailynews/rss/weather/Florianopolis__BR/*http://weather.yahoo.com/forecast/BRXX0091_f.html",
               "pubDate": "Mon, 19 Oct 2015 9:06 pm BRST",
               "condition": {
                  "code": "11",
                  "date": "Mon, 19 Oct 2015 9:06 pm BRST",
                  "temp": "66",
                  "text": "Light Rain"
               },
               "description": "\n<img src=\"http://l.yimg.com/a/i/us/we/52/11.gif\"/><br />\n<b>Current Conditions:<\/b><br />\nLight Rain, 66 F<BR />\n<BR /><b>Forecast:<\/b><BR />\nMon - Showers Early. High: 72 Low: 66<br />\nTue - Mostly Cloudy. High: 77 Low: 68<br />\nWed - Thunderstorms. High: 72 Low: 65<br />\nThu - Thunderstorms. High: 71 Low: 66<br />\nFri - Showers. High: 71 Low: 65<br />\n<br />\n<a href=\"http://us.rd.yahoo.com/dailynews/rss/weather/Florianopolis__BR/*http://weather.yahoo.com/forecast/BRXX0091_f.html\">Full Forecast at Yahoo! Weather<\/a><BR/><BR/>\n(provided by <a href=\"http://www.weather.com\" >The Weather Channel<\/a>)<br/>\n",
               "forecast": [
                  {
                     "code": "45",
                     "date": "19 Oct 2015",
                     "day": "Mon",
                     "high": "72",
                     "low": "66",
                     "text": "Showers Early"
                  },
                  {
                     "code": "28",
                     "date": "20 Oct 2015",
                     "day": "Tue",
                     "high": "77",
                     "low": "68",
                     "text": "Mostly Cloudy"
                  },
                  {
                     "code": "4",
                     "date": "21 Oct 2015",
                     "day": "Wed",
                     "high": "72",
                     "low": "65",
                     "text": "Thunderstorms"
                  },
                  {
                     "code": "4",
                     "date": "22 Oct 2015",
                     "day": "Thu",
                     "high": "71",
                     "low": "66",
                     "text": "Thunderstorms"
                  },
                  {
                     "code": "11",
                     "date": "23 Oct 2015",
                     "day": "Fri",
                     "high": "71",
                     "low": "65",
                     "text": "Showers"
                  }
               ],
               "guid": {
                  "isPermaLink": "false",
                  "content": "BRXX0091_2015_10_23_7_00_BRST"
               }
            }
         }
      }
   }
}
     */
}
