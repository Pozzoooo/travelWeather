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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pozzo.apps.tools.AndroidUtil;
import pozzo.apps.travelweather.R;
import pozzo.apps.travelweather.core.Error;
import pozzo.apps.travelweather.databinding.ActivityMapsBinding;
import pozzo.apps.travelweather.forecast.adapter.ForecastInfoWindowAdapter;
import pozzo.apps.travelweather.forecast.model.Day;
import pozzo.apps.travelweather.forecast.model.Forecast;
import pozzo.apps.travelweather.forecast.model.Weather;
import pozzo.apps.travelweather.location.LocationLiveData;
import pozzo.apps.travelweather.map.ActionRequest;
import pozzo.apps.travelweather.map.viewmodel.MapViewModel;
import pozzo.apps.travelweather.map.viewmodel.PreferencesViewModel;

/**
 * A viewmodel nao pode definir como alguma coisa exibida, apenas deinir o que vai ser exibida... ?
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
	private static final int ANIM_ROUTE_TIME = 1200;
	private static final int REQ_PERMISSION_FOR_CURRENT_LOCATION = 0x1;

	private LatLng startPosition;
	private LatLng finishPosition;
	private HashMap<Marker, Weather> mapMarkerToWeather = new HashMap<>();

	private DrawerLayout drawerLayout;
	private GoogleMap mMap;
	private EditText eSearch;
	private View vgTopBar;
	private ProgressDialog progressDialog;

	private Handler mainThread;
	private Observer<Location> locationObserver;
	private FirebaseAnalytics mFirebaseAnalytics;

	private MapViewModel viewModel;
	private PreferencesViewModel preferencesViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDataBind();
		setupMapFragment();

		mainThread = new Handler();
		mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

		drawerLayout = findViewById(R.id.drawerLayout);
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
				startPosition = latLng;
				if(latLng != null) {
					pointMapTo(startPosition);
				} else {
					clearMap();
				}
			}
		});
		viewModel.getFinishPosition().observe(this, new Observer<LatLng>() {
			@Override
			public void onChanged(@Nullable LatLng latLng) {
				finishPosition = latLng;
				clearMap();
				if(latLng != null) {
					fitCurrentRouteOnScreen();
					viewModel.updateRoute();
				} else {
					setStartPosition(startPosition);
				}
			}
		});
		viewModel.isShowingProgress().observe(this, new Observer<Boolean>() {
			@Override
			public void onChanged(Boolean isShowingProgress) {
				if (isShowingProgress) {
					mainThread.postDelayed(showProgress, 700);
				} else {
					progressDialog.hide();
				}
			}
		});
		preferencesViewModel.getSelectedDay().observe(this, new Observer<Day>() {
			@Override
			public void onChanged(@Nullable Day day) {
				refreshMarkers();
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
		viewModel.getWeathers().observe(this, new Observer<List<Weather>>() {
			@Override
			public void onChanged(List<Weather> weathers) {
				for (Weather it : weathers) {
					addMark(it);
				}
			}
		});
		viewModel.isShowingTopBar().observe(this, new Observer<Boolean>() {
			@Override
			public void onChanged(Boolean aBoolean) {
				if (aBoolean)
					showTopBar();
				else
					hideTopBar();
			}
		});
		viewModel.getShouldFinish().observe(this, new Observer<Boolean>() {
			@Override
			public void onChanged(Boolean aBoolean) {
				if (aBoolean)
					finish();
			}
		});
		viewModel.getError().observe(this, new Observer<Error>() {
			@Override
			public void onChanged(@Nullable Error error) {
				if (error != null)
					showErrorDialog(error);
			}
		});
		viewModel.getActionRequest().observe(this, new Observer<ActionRequest>() {
			@Override
			public void onChanged(@Nullable ActionRequest actionRequest) {
				if (actionRequest != null)
					showActionRequest(actionRequest);
			}
		});
	}

	private Runnable showProgress = new Runnable() {
		@Override
		public void run() {
			if (viewModel.isShowingProgress().getValue()) {
				progressDialog.show();
			}
		}
	};

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
			}, REQ_PERMISSION_FOR_CURRENT_LOCATION);
		}
	}

	public void setCurrentLocationAsStartPosition() {
		clearMap();
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
		fitToScreenWhenLayoutIsReady();
    }

    private void fitToScreenWhenLayoutIsReady() {
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

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if(savedInstanceState != null) {
			startPosition = savedInstanceState.getParcelable("startPosition");
			finishPosition = savedInstanceState.getParcelable("finishPosition");
		}
	}

	@Override
	public void onBackPressed() {
		viewModel.back();
	}

	@Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(placeMarkerClick);
        mMap.setOnMapLongClickListener(clearMarkerLongClick);
        mMap.setOnInfoWindowClickListener(onInfoWindowClick);
		mMap.setInfoWindowAdapter(new ForecastInfoWindowAdapter(this));

		clearMap();

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
    	if (latLng == null)
    		return;

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
        mapMarkerToWeather.put(marker, weather);
    }

    /**
     * Should try to fit entire route on screen.
     */
    private void fitCurrentRouteOnScreen() {
		pointMapTo(viewModel.getRouteBounds());
    }

	/**
	 * Clear anything drawn on map.
	 */
	public void clearMap() {
		if (mMap != null) {
			mMap.clear();
		}
		mapMarkerToWeather.clear();
	}

    /**
     * User seems to be willing to do something, let's help him!
     */
    private GoogleMap.OnMapClickListener placeMarkerClick = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
        	viewModel.addPoint(latLng);
        }
    };

    private void showErrorDialog(Error error) {
		new AlertDialog.Builder(this)
				.setTitle(R.string.warning)
				.setMessage(error.getMessageId())
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						viewModel.dismissError();
						dialog.dismiss();
					}
				}).show();
    }

    private void showActionRequest(final ActionRequest actionRequest) {
		new AlertDialog.Builder(this)
			.setMessage(actionRequest.getMessageId())
			.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					viewModel.actionRequestAccepted(actionRequest);
				}
			}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).show();
	}

    /**
     * Popup to clear all markers.
     */
    private GoogleMap.OnMapLongClickListener clearMarkerLongClick =
            new GoogleMap.OnMapLongClickListener() {
        @Override
        public void onMapLongClick(LatLng latLng) {
			viewModel.requestClear();
        }
    };

    /**
     * Click on popup.
     */
    private GoogleMap.OnInfoWindowClickListener onInfoWindowClick =
            new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            Weather weather = mapMarkerToWeather.get(marker);
            AndroidUtil.openUrl(weather.getUrl(), MapActivity.this);
        }
    };

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == REQ_PERMISSION_FOR_CURRENT_LOCATION) {
			setCurrentLocationAsStartPosition();
		} else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	/**
     * Search button click event.
     */
    public void toggleSearch(View view) {
        viewModel.toggleTopBar();
    }

	private void hideTopBar() {
		vgTopBar.animate().alpha(0.f);
		eSearch.setVisibility(View.INVISIBLE);
		AndroidUtil.hideKeyboard(this, eSearch);
	}

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
		drawerLayout.openDrawer(GravityCompat.START);
	}

	/**
	 * User wants to find his address.
	 */
	private TextView.OnEditorActionListener onSearchGo = new TextView.OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
			if(event == null || !(event.getAction() == KeyEvent.ACTION_DOWN))
				return false;

			viewModel.searchAddress(textView.getText().toString());
			return true;
		}
	};

	private void refreshMarkers() {
		if(mapMarkerToWeather.isEmpty())
			return;

		HashMap<Marker, Weather> markerWeathers = this.mapMarkerToWeather;
		this.mapMarkerToWeather = new HashMap<>();
		for(Map.Entry<Marker, Weather> it : markerWeathers.entrySet()) {
			it.getKey().remove();
			addMark(it.getValue());
		}
	}

    public void onClearSearch(View view) {
        eSearch.setText("");
    }
}
