package pozzo.apps.travelweather.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import pozzo.apps.travelweather.R;
import pozzo.apps.travelweather.business.ForecastBusiness;
import pozzo.apps.travelweather.business.LocationBusiness;
import pozzo.apps.travelweather.exception.AddressNotFoundException;
import pozzo.apps.travelweather.helper.ForecastHelper;
import pozzo.apps.travelweather.helper.GeoCoderHelper;
import pozzo.apps.travelweather.model.Address;
import pozzo.apps.travelweather.model.Forecast;
import pozzo.apps.travelweather.model.Weather;
import pozzo.apps.travelweather.util.AndroidUtil;

/**
 * Atividade para exibir o mapa.
 */
public class MapsActivity extends FragmentActivity
		implements OnMapReadyCallback {
	private static final int ANIM_ROUTE_TIME = 1200;

    private LocationBusiness locationBusiness;
    private ForecastBusiness forecastBusiness;
	private GeoCoderHelper geoCoderHelper;

    private LatLng startPosition;
    private LatLng finishPosition;
    private HashMap<Marker, Weather> markerWeathers;

	private DrawerLayout drawerLayout;
	private GoogleMap mMap;
	private EditText eSearch;
	private View vgTopBar;

    {
        locationBusiness = new LocationBusiness();
        forecastBusiness = new ForecastBusiness();
		geoCoderHelper = new GeoCoderHelper(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
		restoreInstanceState(savedInstanceState);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		final View view = findViewById(R.id.vgMain);
		ViewTreeObserver observer = view.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				fitCurrentRouteOnScreen();
				view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}
		});
    }

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelable("startPosition", startPosition);
		outState.putParcelable("finishPosition", finishPosition);

		super.onSaveInstanceState(outState);
	}

	private void restoreInstanceState(Bundle savedInstanceState) {
		if(savedInstanceState != null) {
			startPosition = savedInstanceState.getParcelable("startPosition");
			finishPosition = savedInstanceState.getParcelable("finishPosition");
		}
	}

	@Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(placeMarkerClick);
        mMap.setOnMapLongClickListener(clearMarkerLongClick);
        mMap.setOnInfoWindowClickListener(onInfoWindowClick);

		clear();
		if(startPosition == null) {
			Location location = locationBusiness.getCurrentLocation(this);
			if(location != null)
				startPosition = new LatLng(location.getLatitude(), location.getLongitude());
		}

		setStartPosition(startPosition);
		setFinishPosition(finishPosition);
    }

	@Override
	public void onBackPressed() {
//		if(eSearch != null && eSearch.hasFocus())
//			closeSearch();
//		else
			super.onBackPressed();
	}

	private DrawerLayout getDrawerLayout() {
		if(drawerLayout == null)
			drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
		return drawerLayout;
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
     * Map will be centered on given point.
     */
    private void pointMapTo(LatLng latLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8f));
    }

    /**
     * Map will fit the given bounds.
     */
    private void pointMapTo(LatLngBounds latLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLng, 70), ANIM_ROUTE_TIME, null);
    }

    /**
     * Add 1 marker to the map related to the given Weather object.
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
     * Sets the new finish point, or clear it.
     */
    private void setFinishPosition(LatLng finishPosition) {
        this.finishPosition = finishPosition;
        if(finishPosition != null) {
            queryAndShowWeatherFor(finishPosition);
            fitCurrentRouteOnScreen();
            updateRoute(mMap);
        }
    }

    /**
     * Should try to fit entire route on screen.
     */
    private void fitCurrentRouteOnScreen() {
		if(startPosition != null && finishPosition != null)
			pointMapTo(LatLngBounds.builder()
					.include(startPosition).include(finishPosition).build());
    }

    /**
     * Clear anything drawn on map.
     */
    private void clear() {
        mMap.clear();
        markerWeathers = new HashMap<>();
    }

    /**
     * Update route.
     */
    private void updateRoute(final GoogleMap googleMap) {
        if(startPosition == null || finishPosition == null)
            return;

        new AsyncTask<Void, Void, PolylineOptions>() {
			private ProgressDialog progressDialog;

			@Override
			protected void onPreExecute() {
				progressDialog = new ProgressDialog(MapsActivity.this);
				progressDialog.setIndeterminate(true);
				//Soh mostramos se demorar consideravelmente
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if (progressDialog != null)
							progressDialog.show();
					}
				}, ANIM_ROUTE_TIME);
			}

            @Override
            protected PolylineOptions doInBackground(Void... params) {
                final ArrayList<LatLng> directionPoint =
                        locationBusiness.getDirections(startPosition, finishPosition);
                if(directionPoint == null || directionPoint.isEmpty())
                    return null;

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
				ProgressDialog progress = progressDialog;
				progressDialog = null;
				if(progress.isShowing())
					progress.hide();
                if(rectLine != null)
                    googleMap.addPolyline(rectLine);
            }
        }.execute();
    }

    /**
     * User seems to be willing to do something, let's help him!
     */
    private GoogleMap.OnMapClickListener placeMarkerClick = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            if(startPosition == null) {
                setStartPosition(latLng);
            } else {
                if(finishPosition != null) {
                    LatLng startPosition = MapsActivity.this.startPosition;
                    clear();//Make sure there is no garbage around
                    setStartPosition(startPosition);
                }
                setFinishPosition(latLng);
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
			closeSearch();
            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
            builder.setMessage(R.string.removeAllMarkers);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    clear();
                    setStartPosition(null);
                    setFinishPosition(null);
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
        if(directionPoint == null || directionPoint.isEmpty())
            return;

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
            private Exception error;

            @Override
			protected Weather doInBackground(Void... params) {
                if(!isFinishing())
                    try {
                        return forecastBusiness.from(location, MapsActivity.this);
                    } catch (AddressNotFoundException e) {
                        error = e;
                    }
                return null;
			}

            @Override
            protected void onPostExecute(Weather weather) {
                if(weather != null) {
                    addMark(weather);
                } else if(error instanceof AddressNotFoundException) {
                    AndroidUtil.errorMessage(MapsActivity.this,
                            getString(R.string.error_addressNotFound),
                            R.string.warning, R.string.ok).create().show();
                }
            }
        }.execute();
	}

    /**
     * Search button click event.
     */
    public void onSearch(View view) {
        if(eSearch == null) {
			eSearch = (EditText) findViewById(R.id.eSearch);
			vgTopBar = findViewById(R.id.vgTopBar);
		}

		vgTopBar.setVisibility(View.VISIBLE);
		vgTopBar.setAlpha(0.f);
		vgTopBar.animate().alpha(1.f);
		eSearch.setVisibility(View.VISIBLE);
		eSearch.requestFocus();
		eSearch.setOnEditorActionListener(onSearchGo);
    }

	/**
	 * User wants to open side menu.
	 */
	public void onMenu(View view) {
		getDrawerLayout().openDrawer(GravityCompat.START);
	}

	/**
	 * Closes "search bar".
	 */
	private void closeSearch() {
		vgTopBar.setVisibility(View.GONE);
	}

	/**
	 * User wants to find his address.
	 */
	private TextView.OnEditorActionListener onSearchGo = new TextView.OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			String address = v.getText().toString();
			if(!(event.getAction() == KeyEvent.ACTION_DOWN) || address.isEmpty())
				return false;

			try {
				LatLng location = geoCoderHelper.getPositionFromFirst(address);
				placeMarkerClick.onMapClick(location);
			} catch (IOException e) {
				AndroidUtil.errorMessage(MapsActivity.this,
						getString(R.string.error_addressNotFound), R.string.warning, R.string.ok);
				e.printStackTrace();
			}
			return true;
		}
	};
}
