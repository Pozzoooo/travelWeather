package pozzo.apps.travelweather.model;

import android.provider.BaseColumns;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * This will represent the weather for a location in a period of time.
 *
 * Created by sarge on 10/25/15.
 */
@Table(name = "weather", id = BaseColumns._ID)
public class Weather extends BaseModel {
    public interface Col {
        String ADDRESS = "address";
        String FORECASTS = "forecasts";
    }

    @Column(name = Col.ADDRESS)
    private Address address;

    @Column(name = Col.FORECASTS, onDelete = Column.ForeignKeyAction.CASCADE)
    private Forecast[] forecasts;

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
}
