package pozzo.apps.travelweather.model;

import android.provider.BaseColumns;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by soldier on 10/4/15.
 */
@Table(name = "address", id = BaseColumns._ID)
public class Address extends BaseModel {
    public interface Col {
        String ADDRESS = "address";
        String LATITUDE = "latitude";
        String LONGITUDE = "longitude";
    }

    @Column(name = Col.ADDRESS)
    private String address;

    @Column(name = Col.LATITUDE)
    private long latitude;

    @Column(name = Col.LONGITUDE)
    private long longitude;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }
}
