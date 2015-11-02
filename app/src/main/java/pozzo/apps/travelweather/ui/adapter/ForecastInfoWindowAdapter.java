package pozzo.apps.travelweather.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import pozzo.apps.travelweather.R;

/**
 * Create every single InfoWindow to show on maps.
 *
 * Created by sarge on 11/2/15.
 */
public class ForecastInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
	private Context context;
	private LayoutInflater inflater;

	public ForecastInfoWindowAdapter(Context context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public View getInfoWindow(Marker marker) {
		return null;
	}

	@Override
	public View getInfoContents(Marker marker) {
		View contentView = inflater.inflate(R.layout.adapter_forecast, null);
		TextView lTitle = (TextView) contentView.findViewById(R.id.lTitle);
		lTitle.setText(marker.getTitle());
		return contentView;
	}
}
