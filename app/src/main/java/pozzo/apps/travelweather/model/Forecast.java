package pozzo.apps.travelweather.model;

import android.provider.BaseColumns;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by soldier on 10/4/15.
 */
@Table(name = "forecast", id = BaseColumns._ID)
public class Forecast extends BaseModel {
    public interface Col {
        String ADDRESS = "address";
        String WEATHER = "weather";
        String RAINFALL = "rainfall";
        String TEMPERATURE = "temperature";
    }

    @Column(name = Col.ADDRESS)
    private Address address;

    @Column(name = Col.WEATHER)
    private int weather;

    @Column(name = Col.RAINFALL)
    private int rainfall;

    @Column(name = Col.TEMPERATURE)
    private int temperature;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public int getWeather() {
        return weather;
    }

    public void setWeather(int weather) {
        this.weather = weather;
    }

    public int getRainfall() {
        return rainfall;
    }

    public void setRainfall(int rainfall) {
        this.rainfall = rainfall;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
}
