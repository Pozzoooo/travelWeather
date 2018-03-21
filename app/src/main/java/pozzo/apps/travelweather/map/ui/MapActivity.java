package pozzo.apps.travelweather.map.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.splunk.mint.Mint;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import pozzo.apps.tools.AndroidUtil;
import pozzo.apps.tools.NetworkUtil;
import pozzo.apps.travelweather.R;
import pozzo.apps.travelweather.databinding.ActivityMapsBinding;
import pozzo.apps.travelweather.forecast.ForecastBusiness;
import pozzo.apps.travelweather.forecast.adapter.ForecastInfoWindowAdapter;
import pozzo.apps.travelweather.forecast.model.Day;
import pozzo.apps.travelweather.forecast.model.Forecast;
import pozzo.apps.travelweather.forecast.model.Weather;
import pozzo.apps.travelweather.location.LocationLiveData;
import pozzo.apps.travelweather.map.helper.GeoCoderHelper;
import pozzo.apps.travelweather.map.viewmodel.MapViewModel;
import pozzo.apps.travelweather.map.viewmodel.PreferencesViewModel;

/**
 * Atividade para exibir o mapa.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
	private static final int ANIM_ROUTE_TIME = 1200;
	private static final int REQ_PERMISSION = 0x1;

	private int lineColor;

	private ForecastBusiness forecastBusiness;
	private GeoCoderHelper geoCoderHelper;

	private LatLng startPosition;
	private LatLng finishPosition;
	private HashMap<Marker, Weather> markerWeathers = new HashMap<>();

	private DrawerLayout drawerLayout;
	private GoogleMap mMap;
	private EditText eSearch;
	private View vgTopBar;
	private ProgressDialog progressDialog;

	private ThreadPoolExecutor executor;
	private Handler mainThread;
	private Observer<Location> locationObserver;
	private FirebaseAnalytics mFirebaseAnalytics;

	private MapViewModel viewModel;
	private PreferencesViewModel preferencesViewModel;

    {
        forecastBusiness = new ForecastBusiness();
		geoCoderHelper = new GeoCoderHelper(this);
		executor = new ThreadPoolExecutor(
				7, 20, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(7), new ThreadPoolExecutor.DiscardPolicy()
		);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDataBind();
		restoreInstanceState(savedInstanceState);
		setupMapFragment();

		mainThread = new Handler();
		mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

		vgTopBar = findViewById(R.id.vgTopBar);
		eSearch = findViewById(R.id.eSearch);
		eSearch.setOnEditorActionListener(onSearchGo);
		progressDialog = new ProgressDialog(MapActivity.this);
		progressDialog.setIndeterminate(true);

		observeData();
    }

    private void setupDataBind() {
		ActivityMapsBinding contentView = DataBindingUtil.setContentView(this, R.layout.activity_maps);
		viewModel = ViewModelProviders.of(this).get(MapViewModel.class);
		preferencesViewModel = ViewModelProviders.of(this).get(PreferencesViewModel.class);
		contentView.setModelView(viewModel);
	}

	private void setupMapFragment() {
		SupportMapFragment mapFragment = (SupportMapFragment)
				getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

    private void observeData() {
		viewModel.getStartPosition().observe(this, new Observer<LatLng>() {
			@Override
			public void onChanged(@Nullable LatLng latLng) {
				if(startPosition != null) {
					queryAndShowWeatherFor(startPosition);
					pointMapTo(startPosition);
				}
			}
		});
		viewModel.getFinishPosition().observe(this, new Observer<LatLng>() {
			@Override
			public void onChanged(@Nullable LatLng latLng) {
				if(finishPosition != null) {
					queryAndShowWeatherFor(finishPosition);
					fitCurrentRouteOnScreen();
					viewModel.updateRoute();
				}
			}
		});
		viewModel.isShowingProgress().observe(this, new Observer<Boolean>() {
			@Override
			public void onChanged(Boolean isShowingProgress) {
				if (isShowingProgress) {
					showProgressDelayed();
				} else {
					progressDialog.hide();
				}
			}
		});
		preferencesViewModel.getSelectedDay().observe(this, new Observer<Day>() {
			@Override
			public void onChanged(@Nullable Day day) {
				updateForecastsIcons();
			}
		});
		viewModel.getDirectionLine().observe(this, new Observer<PolylineOptions>() {
			@Override
			public void onChanged(PolylineOptions rectLine) {
				if(mMap == null)
					return;

				if(rectLine != null)
					mMap.addPolyline(rectLine);
				else
					Toast.makeText(MapActivity.this, R.string.warning_pathNotFound,
							Toast.LENGTH_SHORT).show();
			}
		});

		viewModel.getWeatherPoints().observe(this, new Observer<List<LatLng>>() {
			@Override
			public void onChanged(List<LatLng> latLngs) {
				for (LatLng it : latLngs) {
					queryAndShowWeatherFor(it);
				}
			}
		});
	}

	/**
	 * Given a few delay, so we avoid showing the loading dialog in case of a quick process
	 */
	private void showProgressDelayed() {
		mainThread.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (viewModel.isShowingProgress().getValue()) {
					progressDialog.show();
				}
			}
		}, 700);
	}

	public void currentLocationFabClick(View view) {
		setCurrentLocationAsStartPositionRequestingPermission();
		sendFirebaseFabEvent();
	}

	private void sendFirebaseFabEvent() {
		Bundle bundle = new Bundle();
		bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "fab");
		bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "currentLocation");
		mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
	}

	public void setCurrentLocationAsStartPositionRequestingPermission() {
		boolean hasPermission = ContextCompat.checkSelfPermission(
				this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
		if (hasPermission) {
			setCurrentLocationAsStartPosition();
		} else {
			ActivityCompat.requestPermissions(this, new String[]{
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.ACCESS_COARSE_LOCATION
			}, REQ_PERMISSION);
		}
	}

	public void setCurrentLocationAsStartPosition() {
		clearSelection();
		LatLng currentLocation = viewModel.getCurrentLocation();
		if (currentLocation != null) {
			setStartPosition(currentLocation);
		} else {
			requestLiveLocation();
		}
	}

	private void requestLiveLocation() {
		viewModel.showProgress();
		final LocationLiveData liveLocation = viewModel.getLiveLocation();
		locationObserver = new Observer<Location>() {
			@Override
			public void onChanged(Location location) {
				setStartPosition(new LatLng(location.getLatitude(), location.getLongitude()));
				removeLocationObserver();
			}
		};
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (locationObserver != null) {
					removeLocationObserver();
					showCantFindLocationDialog();
				}
			}
		}, 30000);

		liveLocation.observe(this, locationObserver);
	}

	private void removeLocationObserver() {
		if (locationObserver != null) {
			viewModel.getLiveLocation().removeObserver(locationObserver);
			viewModel.hideProgress();
			locationObserver = null;
		}
	}

	private void showCantFindLocationDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this)
				.setTitle(R.string.warning).setMessage(R.string.warning_currentLocationNotFound);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		observeLayoutToUpdate();
    }

    private void observeLayoutToUpdate() {
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
			removeFinishPosition();
            shouldQuit &= false;
        } else if(startPosition != null) {
			removeStartPosition();
			shouldQuit &= false;
		}

        if(shouldQuit)
		    super.onBackPressed();
	}

	private void removeFinishPosition() {
		clearSelection();
		setFinishPosition(null);
		setStartPosition(startPosition);
	}

	private void removeStartPosition() {
		clearSelection();
		setStartPosition(null);
	}

	@Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(placeMarkerClick);
        mMap.setOnMapLongClickListener(clearMarkerLongClick);
        mMap.setOnInfoWindowClickListener(onInfoWindowClick);
		mMap.setInfoWindowAdapter(new ForecastInfoWindowAdapter(this));

		clearSelection();

		mainThread.postDelayed(new Runnable() {
			@Override
			public void run() {
				setStartPosition(startPosition);
				setFinishPosition(finishPosition);
				if(startPosition == null)
					setCurrentLocationAsStartPositionRequestingPermission();
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
        viewModel.setStartPosition(startPosition);
    }

	/**
	 * Sets the new finish point, or clear it.
	 */
	private void setFinishPosition(LatLng finishPosition) {
		this.finishPosition = finishPosition;
		viewModel.setFinishPosition(finishPosition);
	}

	/**
	 * Map will be centered on given point.
	 */
	private void pointMapTo(LatLng latLng) {
		if (mMap != null && latLng != null) {
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8f));
		}
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

        Day selectedDay = preferencesViewModel.getSelectedDay().getValue();
		Forecast forecast = weather.getForecast(selectedDay);

		MarkerOptions markerOptions = new MarkerOptions()
				.position(weather.getLatLng())
				.title(forecast.getText())
				.icon(forecast.getIcon());
        Marker marker = mMap.addMarker(markerOptions);
        markerWeathers.put(marker, weather);
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
		if (mMap != null) {
			mMap.clear();
		}
		markerWeathers.clear();
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

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == REQ_PERMISSION) {
			setCurrentLocationAsStartPosition();
		} else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	/**
     * Search button click event.
     */
    public void toggleSearch(View view) {
        if(isSearchOpen()) {
			hideTopBar();
		} else {
			showTopBar();
        }
    }

    private boolean isSearchOpen() {
    	return vgTopBar.getAlpha() == 1.f;
	}

	/**
	 * Hide app top bar.
     *
     * @return true if hidden, false if already hidden.
	 */
	private boolean hideTopBar() {
		if(isSearchOpen())
			return false;

		vgTopBar.animate().alpha(0.f);
		eSearch.setVisibility(View.INVISIBLE);
		AndroidUtil.hideKeyboard(this, eSearch);
		return true;
	}

	/**
	 * Show app top bar.
	 */
	private void showTopBar() {
		vgTopBar.animate().alpha(1.f);
        eSearch.setVisibility(View.VISIBLE);
        eSearch.requestFocus();
        AndroidUtil.showKeyboard(this, eSearch);
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
			if(event == null || !(event.getAction() == KeyEvent.ACTION_DOWN))
				return false;

			searchAddress(v.getText().toString());
			return true;
		}
	};

	private void searchAddress(String search) {
		try {
			LatLng location = geoCoderHelper.getPositionFromFirst(search);
			placeMarkerClick.onMapClick(location);
		} catch (IOException e) {
			AndroidUtil.errorMessage(MapActivity.this,
					getString(R.string.error_addressNotFound), R.string.warning, R.string.ok);
			e.printStackTrace();
		}
	}

	/**
	 * Update all forecast icons on map.
	 */
	private void updateForecastsIcons() {
		if(markerWeathers.isEmpty())
			return;

		HashMap<Marker, Weather> markerWeathers = this.markerWeathers;
		this.markerWeathers = new HashMap<>();
		for(Map.Entry<Marker, Weather> it : markerWeathers.entrySet()) {
			it.getKey().remove();
			addMark(it.getValue());
		}
	}

    public void onClearSearch(View view) {
        eSearch.setText("");
    }
}
