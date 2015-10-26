package pozzo.apps.travelweather.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;

import pozzo.apps.travelweather.R;
import pozzo.apps.travelweather.business.ForecastBusiness;
import pozzo.apps.travelweather.business.LocationBusiness;
import pozzo.apps.travelweather.helper.ForecastHelper;
import pozzo.apps.travelweather.model.Address;
import pozzo.apps.travelweather.model.Forecast;
import pozzo.apps.travelweather.model.Weather;
import pozzo.apps.travelweather.util.AndroidUtil;

/**
 * Atividade para exibir o mapa.
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private LocationBusiness locationBusiness;
    private ForecastBusiness forecastBusiness;

    private GoogleMap mMap;
    private LatLng startPosition;
    private LatLng finishPosition;
    private HashMap<Marker, Weather> markerWeathers;

    {
        locationBusiness = new LocationBusiness();
        forecastBusiness = new ForecastBusiness();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        pointToCurrentLocation();

        mMap.setOnMapClickListener(placeMarkerClick);
        mMap.setOnMapLongClickListener(clearMarkerLongClick);
        mMap.setOnInfoWindowClickListener(onInfoWindowClick);
        clear();
    }

    /**
     * Gera o ponto incial.
     */
    private void pointToCurrentLocation() {
        Location location = locationBusiness.getCurrentLocation(this);
        if(location != null) {
            setStartPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }

    /**
     * @param startPosition Nova posicao inicial.
     */
    private void setStartPosition(LatLng startPosition) {
        this.startPosition = startPosition;
        if(startPosition != null) {
            queryAndShowWeatherFor(startPosition);
            pointMapTo(startPosition);
        }
    }

    /**
     * O dado mapa sera apontado para a dada posicao.
     */
    private void pointMapTo(LatLng latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f));
    }

    /**
     * O dado mapa sera apontado para a dada posicao.
     */
    private void pointMapTo(LatLngBounds latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLng, 70));
    }

    /**
     * Adiciona marcacao no mapa.
     */
    private void addMark(Weather weather) {
        if(weather == null)
            return;

        Forecast firstForecast = weather.getForecasts()[0];
        String message = firstForecast.getText();
        int icon = ForecastHelper.forecastIcon(firstForecast);
        Address address = weather.getAddress();
        LatLng location = new LatLng(address.getLatitude(), address.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions().position(location).title(message)
                .icon(BitmapDescriptorFactory.fromResource(icon));
        Marker marker = mMap.addMarker(markerOptions);
        markerWeathers.put(marker, weather);
    }

    /**
     * Define um novo ponto final.
     */
    private void setFinish(LatLng finishPosition) {
        this.finishPosition = finishPosition;
        if(finishPosition != null) {
            queryAndShowWeatherFor(finishPosition);
            pointMapTo(LatLngBounds.builder()
                    .include(startPosition).include(finishPosition).build());
            updateTrack(mMap);
        }
    }

    /**
     * Limpa tudo que estiver no mapa.
     */
    private void clear() {
        mMap.clear();
        markerWeathers = new HashMap<>();
        setStartPosition(null);
        setFinish(null);
    }

    /**
     * Atualiza tragetoria.
     */
    private void updateTrack(final GoogleMap googleMap) {
        if(startPosition == null || finishPosition == null)
            return;

        new AsyncTask<Void, Void, PolylineOptions>() {
            @Override
            protected PolylineOptions doInBackground(Void... params) {
                final ArrayList<LatLng> directionPoint =
                        locationBusiness.getDirections(startPosition, finishPosition);

                PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);
                for(int i = 0 ; i < directionPoint.size() ; i++) {
                    LatLng latLng = directionPoint.get(i);
                    rectLine.add(latLng);
                }

                new Thread() {
                    @Override
                    public void run() {
                        weatherOverDirection(directionPoint);
                    }
                }.start();
                return rectLine;
            }

            @Override
            protected void onPostExecute(PolylineOptions rectLine) {
                googleMap.addPolyline(rectLine);
            }
        }.execute();
    }

    /**
     * Usuario quer nos indicar alguma coisa \o/.
     */
    private GoogleMap.OnMapClickListener placeMarkerClick = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            if(startPosition == null) {
                setStartPosition(latLng);
            } else {
                LatLng startPosition = MapsActivity.this.startPosition;
                clear();//Make sure there is no garbage around
                setStartPosition(startPosition);
                setFinish(latLng);
            }
        }
    };

    /**
     * Popup to clear all markers.
     */
    private GoogleMap.OnMapLongClickListener clearMarkerLongClick =
            new GoogleMap.OnMapLongClickListener() {
        @Override
        public void onMapLongClick(LatLng latLng) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
            builder.setMessage(R.string.removeAllMarkers);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    clear();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    };

    /**
     * Click on popup.
     */
    private GoogleMap.OnInfoWindowClickListener onInfoWindowClick =
            new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            Weather weather = markerWeathers.get(marker);
            AndroidUtil.openUrl(weather.getUrl(), MapsActivity.this);
        }
    };

    /**
     * Request forecast for all over the route.
     */
    private void weatherOverDirection(ArrayList<LatLng> directionPoint) {
        //Start jah possui
        LatLng lastForecast = directionPoint.get(0);
        for(int i = 0 ; i < directionPoint.size() ; i++) {
            LatLng latLng = directionPoint.get(i);
            if(i % 250 == 1 //Um mod para nao checar em todos os pontos, sao muitos
                    && ForecastHelper.isMinDistanceToForecast(latLng, lastForecast)) {
                queryAndShowWeatherFor(latLng);
                lastForecast = latLng;
            }
        }
    }

    /**
     * Query and shows weather for the given location.
     */
	private void queryAndShowWeatherFor(final LatLng location) {
		new AsyncTask<Void, Void, Weather>() {
			@Override
			protected Weather doInBackground(Void... params) {
                if(!isFinishing())
                    return forecastBusiness.from(location, MapsActivity.this);
                return null;
			}

            @Override
            protected void onPostExecute(Weather weather) {
                addMark(weather);
            }
        }.execute();
	}
}
