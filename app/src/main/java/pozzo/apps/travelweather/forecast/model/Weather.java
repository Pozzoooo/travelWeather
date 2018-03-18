package pozzo.apps.travelweather.forecast.model;

import java.util.Arrays;
import java.util.List;

import pozzo.apps.travelweather.map.model.Address;

/**
 * This will represent the weather for a location in a period of time.
 */
public class Weather {
    public interface Col {
        String ADDRESS = "address";
        String FORECASTS = "forecasts";
        String URL = "url";
    }

    private Address address;
    private Forecast[] forecasts;
    private String url;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Forecast[] getForecasts() {
        return forecasts;
    }

    public void setForecasts(Forecast[] forecasts) {
        this.forecasts = forecasts;
    }

    public void setForecasts(List<Forecast> forecasts) {
        this.forecasts = forecasts.toArray(new Forecast[0]);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Weather weather = (Weather) o;

        if (address != null ? !address.equals(weather.address) : weather.address != null)
            return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(forecasts, weather.forecasts)) return false;
        return !(url != null ? !url.equals(weather.url) : weather.url != null);

    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (forecasts != null ? Arrays.hashCode(forecasts) : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }
}
