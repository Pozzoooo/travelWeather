package pozzo.apps.travelweather.model;

import android.provider.BaseColumns;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Represent a single point in map.
 *
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
    private double latitude;

    @Column(name = Col.LONGITUDE)
    private double longitude;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address1 = (Address) o;

        if (Double.compare(address1.latitude, latitude) != 0) return false;
        if (Double.compare(address1.longitude, longitude) != 0) return false;
        return !(address != null ? !address.equals(address1.address) : address1.address != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = address != null ? address.hashCode() : 0;
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
