package pozzo.apps.travelweather.helper;

import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

import pozzo.apps.travelweather.R;
import pozzo.apps.travelweather.model.Forecast;

/**
 * To help us on reusability, as I am not sure exactly where to place it =/.
 *
 * Created by sarge on 10/25/15.
 */
public class ForecastHelper {
    private static final String SUN = "sun";
    private static final String THUNDERSTORMS = "thunderstorms";
    private static final String RAIN = "rain";
    private static final String SHOWERS = "showers";
    private static final String PARTLY_CLOUDY = "partly cloudy";
    private static final String CLOUDY = "cloudy";
    private static final String SNOW = "snow";

    public static int forecastIcon(Forecast forecast) {
        int icon;
        String text = forecast.getText().toLowerCase(Locale.US);
        if(text.contains(SUN)) {
            icon = R.drawable.sun;
        } else if(text.contains(THUNDERSTORMS)) {
            icon = R.drawable.thunderstorm;
        } else if(text.contains(RAIN)) {
            icon = R.drawable.heavy_rain;
        } else if(text.contains(SHOWERS)) {
            icon = R.drawable.rain;
        } else if(text.contains(PARTLY_CLOUDY)) {
            icon = R.drawable.partly_cloudy;
        } else if(text.contains(CLOUDY)) {
            icon = R.drawable.cloudy;
        } else if(text.contains(SNOW)) {
            icon = R.drawable.snow;
        } else {
            icon = R.drawable.cloudy_moon;
        }
        return icon;
    }

    /**
     * @return true if distance is enough for a new forecast.
     */
    public static boolean isMinDistanceToForecast(LatLng from, LatLng to) {
        double distance = Math.abs(from.latitude - to.latitude)
                + Math.abs(from.longitude - to.longitude);
        return distance > 0.6;
    }
}
