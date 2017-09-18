package pozzo.apps.travelweather.map.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.splunk.mint.Mint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import pozzo.apps.tools.AndroidUtil;
import pozzo.apps.tools.NetworkUtil;
import pozzo.apps.travelweather.R;
import pozzo.apps.travelweather.databinding.ActivityMapsBinding;
import pozzo.apps.travelweather.forecast.ForecastBusiness;
import pozzo.apps.travelweather.forecast.ForecastHelper;
import pozzo.apps.travelweather.forecast.adapter.ForecastInfoWindowAdapter;
import pozzo.apps.travelweather.forecast.model.Forecast;
import pozzo.apps.travelweather.forecast.model.Weather;
import pozzo.apps.travelweather.location.LocationBusiness;
import pozzo.apps.travelweather.location.LocationLiveData;
import pozzo.apps.travelweather.map.helper.GeoCoderHelper;
import pozzo.apps.travelweather.map.model.Address;

/**
 * Atividade para exibir o mapa.
 */
public class MapActivity extends LifecycleActivity
		implements OnMapReadyCallback, SideMenuFragment.OnDaySelectionChanged {
	private static final int ANIM_ROUTE_TIME = 1200;
	private static final int REQ_PERMISSION = 0x1;

	private int daySelection;
	private int lineColor;

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
	private ProgressDialog progressDialog;
	private boolean isShowingProgress;

	private ThreadPoolExecutor executor;
	private Handler mainThread;
	private Observer locationObserver;
	private FirebaseAnalytics mFirebaseAnalytics;

	private MapViewModel viewModel;

    {
        locationBusiness = new LocationBusiness();
        forecastBusiness = new ForecastBusiness();
		geoCoderHelper = new GeoCoderHelper(this);
		daySelection = -1;
		executor = new ThreadPoolExecutor(
				7, 20, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(7), new ThreadPoolExecutor.DiscardPolicy()
		);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		ActivityMapsBinding contentView = DataBindingUtil.setContentView(this, R.layout.activity_maps);
		viewModel = ViewModelProviders.of(this).get(MapViewModel.class);
		contentView.setModelView(viewModel);

		restoreInstanceState(savedInstanceState);

		SupportMapFragment mapFragment = (SupportMapFragment)
				getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

		SideMenuFragment navigationDrawer = (SideMenuFragment)
				getSupportFragmentManager().findFragmentById(R.id.navigationDrawer);
		navigationDrawer.setOnDaySelectionChanged(this);

		eSearch = findViewById(R.id.eSearch);
		eSearch.setOnEditorActionListener(onSearchGo);
		vgTopBar = findViewById(R.id.vgTopBar);
		mainThread = new Handler();
		progressDialog = new ProgressDialog(MapActivity.this);
		progressDialog.setIndeterminate(true);
		isShowingProgress = false;
		mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

		observeData();
    }

    private void observeData() {
		viewModel.getStartPosition().observe(this, new Observer<LatLng>() {
			@Override
			public void onChanged(@Nullable LatLng latLng) {
				setStartPosition(latLng);
			}
		});
		viewModel.getFinishPosition().observe(this, new Observer<LatLng>() {
			@Override
			public void onChanged(@Nullable LatLng latLng) {
				setFinishPosition(latLng);
			}
		});
	}

	public void currentLocationFabClick(View view) {
		int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
		if (PackageManager.PERMISSION_GRANTED != permissionCheck) {
			ActivityCompat.requestPermissions(this, new String[]{
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.ACCESS_COARSE_LOCATION
			}, REQ_PERMISSION);
		} else {
			clearSelection();
			LatLng currentLocation = viewModel.getCurrentLocation();
			if (currentLocation != null) {
				setStartPosition(currentLocation);
			} else {
				showProgress();
				final LocationLiveData liveLocation = viewModel.getLiveLocation();
				locationObserver = new Observer<Location>() {
					@Override
					public void onChanged(@Nullable Location location) {
						setStartPosition(new LatLng(location.getLatitude(), location.getLongitude()));
						removeLocationObserver();
					}
				};
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						removeLocationObserver();

						if (locationObserver != null) {
							AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this)
									.setTitle(R.string.warning).setMessage(R.string.warning_currentLocationNotFound);
							builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
							builder.create().show();
						}
					}
				}, 30000);

				liveLocation.observe(this, locationObserver);
			}
		}

		Bundle bundle = new Bundle();
		bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "fab");
		bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "currentLocation");
		mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
	}

	private void removeLocationObserver() {
		if (locationObserver != null) {
			viewModel.getLiveLocation().removeObserver(locationObserver);
			hideProgress();
			locationObserver = null;
		}
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
	public void onBackPressed() {
        boolean shouldQuit = !hideTopBar();
        if(finishPosition != null) {
            clearSelection();
            setFinishPosition(null);
            setStartPosition(startPosition);
            shouldQuit &= false;
        } else if(startPosition != null) {
			clearSelection();
			setStartPosition(null);
			shouldQuit &= false;
		}

        if(shouldQuit)
		    super.onBackPressed();
	}

	@Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(placeMarkerClick);
        mMap.setOnMapLongClickListener(clearMarkerLongClick);
        mMap.setOnInfoWindowClickListener(onInfoWindowClick);
		mMap.setInfoWindowAdapter(new ForecastInfoWindowAdapter(this));

		clearSelection();
		if(startPosition == null)
            focusOnCurrentLocation();

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				setStartPosition(startPosition);
				setFinishPosition(finishPosition);
			}
		}, 500);
    }

	/**
	 * Drawer lazy loaded.
	 */
	private DrawerLayout getDrawerLayout() {
		if(drawerLayout == null)
			drawerLayout = findViewById(R.id.drawerLayout);
		return drawerLayout;
	}

	/**
     * @param startPosition Sets a ew start position.
     */
    private void setStartPosition(LatLng startPosition) {
        this.startPosition = startPosition;
        if(startPosition != null) {
            queryAndShowWeatherFor(startPosition);
            pointMapTo(startPosition);
        }
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
     * Map will be centered on given point.
     */
    private void pointMapTo(LatLng latLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8f));
    }

    /**
     * Map will fit the given bounds.
     */
    private void pointMapTo(LatLngBounds latLng) {
		try {
			mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLng, 70), ANIM_ROUTE_TIME, null);
		} catch(IllegalStateException e) {
			Mint.logException(e);
		}
    }

    /**
     * Add 1 marker to the map related to the given Weather object.
     */
    private void addMark(Weather weather) {
        if(weather == null || weather.getAddress() == null)
            return;

		Address address = weather.getAddress();
		LatLng location = new LatLng(address.getLatitude(), address.getLongitude());

		Forecast forecast = getForecastFor(weather);
        int icon = ForecastHelper.forecastIcon(forecast);
		BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(icon);

		MarkerOptions markerOptions = new MarkerOptions()
				.position(location)
				.title(forecast.getText())
				.icon(bitmapDescriptor);
        Marker marker = mMap.addMarker(markerOptions);
        markerWeathers.put(marker, weather);
    }

	/**
	 * @return Forecast that should be shown to the user from given weather.
	 */
	private Forecast getForecastFor(Weather weather) {
		int dayIndex = getDaySelection();
		Forecast[] forecasts = weather.getForecasts();
		dayIndex = forecasts.length > dayIndex ? dayIndex : forecasts.length-1;
		return forecasts[dayIndex];
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
    public void clearSelection() {
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

			@Override
			protected void onPreExecute() {
				showProgress();
			}

            @Override
            protected PolylineOptions doInBackground(Void... params) {
                final ArrayList<LatLng> directionPoint =
                        locationBusiness.getDirections(startPosition, finishPosition);
                if(directionPoint == null || directionPoint.isEmpty())
                    return null;

				if(lineColor <= 0)
					lineColor = getResources().getColor(R.color.route);
                PolylineOptions rectLine = new PolylineOptions().width(7).color(lineColor);
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
				hideProgress();
                if(rectLine != null)
                    googleMap.addPolyline(rectLine);
                else
                    Toast.makeText(MapActivity.this, R.string.warning_pathNotFound,
                            Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    private void showProgress() {
		isShowingProgress = true;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (isShowingProgress)
					progressDialog.show();
			}
		}, 700);
	}

	private void hideProgress() {
		isShowingProgress = false;
		progressDialog.hide();
	}

    /**
     * User seems to be willing to do something, let's help him!
     */
    private GoogleMap.OnMapClickListener placeMarkerClick = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
			hideTopBar();
            if(!checkNetworkAndWarn())
                return;

            if(startPosition == null) {
                setStartPosition(latLng);
            } else {
                if(finishPosition != null) {
                    LatLng startPosition = MapActivity.this.startPosition;
                    clearSelection();//Make sure there is no garbage around
                    setStartPosition(startPosition);
                }
                setFinishPosition(latLng);
            }
        }
    };

	/**
	 * Checks network state and warn user if there is no connection.
	 * @return true if seems to be connection available.
	 */
    private boolean checkNetworkAndWarn() {
        boolean isConnected = NetworkUtil.isNetworkAvailable(this);
        if(!isConnected) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
					.setTitle(R.string.warning).setMessage(R.string.warning_needsConnection);
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
        }

        return isConnected;
    }

    /**
     * Popup to clear all markers.
     */
    private GoogleMap.OnMapLongClickListener clearMarkerLongClick =
            new GoogleMap.OnMapLongClickListener() {
        @Override
        public void onMapLongClick(LatLng latLng) {
			hideTopBar();
            AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
            builder.setMessage(R.string.removeAllMarkers);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    clearSelection();
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
			//Link is not redirecting corretly

            Weather weather = markerWeathers.get(marker);
            AndroidUtil.openUrl(weather.getUrl(), MapActivity.this);
        }
    };

    /**
     * Request forecast for all over the route.
     */
    private void weatherOverDirection(ArrayList<LatLng> directionPoint) {
        if(directionPoint == null || directionPoint.isEmpty())
            return;

        LatLng lastForecast = directionPoint.get(0);
        for(int i = 1 ; i < directionPoint.size() - 1 ; i++) {
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
		executor.execute(new Runnable() {
			@Override
			public void run() {
				if(isFinishing())
					return;

				try {
					final Weather weather = forecastBusiness.from(location);
					mainThread.post(new Runnable() {
						@Override
						public void run() {
							addMark(weather);
						}
					});
				} catch (Exception e) {
					//Ignored...
				}
			}
		});
	}

	private void focusOnCurrentLocation() {
		LatLng location = viewModel.getCurrentLocation();
		if (location != null) {
			pointMapTo(location);
		}
	}

    /**
     * Defines the start position to the current user location.
     */
	private void setStartOnCurrentLocation() {
		LatLng location = viewModel.getCurrentLocation();
		if (location != null) {
			setStartPosition(location);
		}
    }

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == REQ_PERMISSION) {
			setStartOnCurrentLocation();
		} else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	/**
     * Search button click event.
     */
    public void onSearch(View view) {
        if(vgTopBar.getAlpha() == 1.f) {
			hideTopBar();
		} else {
			showTopBar();
        }
    }

    /**
     * User wants to point to his location.
     */
    public void onMyLocation(View view) {
        clearSelection();
        setStartOnCurrentLocation();
    }

	/**
	 * Hide app top bar.
     *
     * @return true if hidden, false if already hidden.
	 */
	private boolean hideTopBar() {
		if(vgTopBar.getAlpha() != 0.f) {
            vgTopBar.animate().alpha(0.f);
            eSearch.setVisibility(View.INVISIBLE);
            hideKeyboardForced(eSearch);
            return true;
        }
        return false;
	}

	/**
	 * Show app top bar.
	 */
	private void showTopBar() {
		vgTopBar.animate().alpha(1.f);
        eSearch.setVisibility(View.VISIBLE);
        eSearch.requestFocus();
        showKeyboardForced();
	}

    private void showKeyboardForced() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void hideKeyboardForced(View view) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

	/**
	 * User wants to open side menu.
	 */
	public void onMenu(View view) {
		getDrawerLayout().openDrawer(GravityCompat.START);
	}

	/**
	 * User wants to find his address.
	 */
	private TextView.OnEditorActionListener onSearchGo = new TextView.OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			String address = v.getText().toString();
			if(event == null || !(event.getAction() == KeyEvent.ACTION_DOWN) || address.isEmpty())
				return false;

			try {
				LatLng location = geoCoderHelper.getPositionFromFirst(address);
				placeMarkerClick.onMapClick(location);
			} catch (IOException e) {
				AndroidUtil.errorMessage(MapActivity.this,
						getString(R.string.error_addressNotFound), R.string.warning, R.string.ok);
				e.printStackTrace();
			}
			return true;
		}
	};

	/**
	 * Update all forecast icons on map.
	 */
	private void updateForecastsIcons() {
		if(markerWeathers == null || markerWeathers.size() <= 0)
			return;

		HashMap<Marker, Weather> markerWeathers = this.markerWeathers;
		this.markerWeathers = new HashMap<>();
		for(Map.Entry<Marker, Weather> it : markerWeathers.entrySet()) {
			it.getKey().remove();
			addMark(it.getValue());
		}
	}

	@Override
	public void daySelectionChanged(int selectedDay) {
		setDaySelection(selectedDay);
		updateForecastsIcons();
	}

	/**
	 * Forced definition of day selection, it will not update on disk.
	 */
	private void setDaySelection(int daySelection) {
		this.daySelection = daySelection;
	}

	/**
	 * A selecao do dia.
	 */
	private int getDaySelection() {
		if(daySelection < 0) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			daySelection = preferences.getInt("selectedDay", 0);
		}
		return daySelection;
	}

    public void onClearSearch(View view) {
        eSearch.setText("");
    }
}
