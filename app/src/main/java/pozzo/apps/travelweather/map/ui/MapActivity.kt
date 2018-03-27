package pozzo.apps.travelweather.map.ui

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.splunk.mint.Mint
import pozzo.apps.tools.AndroidUtil
import pozzo.apps.travelweather.R
import pozzo.apps.travelweather.core.Error
import pozzo.apps.travelweather.databinding.ActivityMapsBinding
import pozzo.apps.travelweather.forecast.adapter.ForecastInfoWindowAdapter
import pozzo.apps.travelweather.forecast.model.Weather
import pozzo.apps.travelweather.map.ActionRequest
import pozzo.apps.travelweather.map.viewmodel.MapViewModel
import pozzo.apps.travelweather.map.viewmodel.PreferencesViewModel
import java.util.*

/**
 * todo A viewmodel nao pode definir como alguma coisa exibida, apenas deinir o que vai ser exibida... ?
 */
class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    //todo maybe I can create an improved abstraction of this map
    private var mapMarkerToWeather = HashMap<Marker, Weather>()

    private lateinit var drawerLayout: DrawerLayout
    private var mMap: GoogleMap? = null
    private lateinit var eSearch: EditText
    private lateinit var vgTopBar: View
    private lateinit var progressDialog: ProgressDialog

    private lateinit var mainThread: Handler

    private lateinit var viewModel: MapViewModel
    private lateinit var preferencesViewModel: PreferencesViewModel

    private val showProgress = Runnable {
        if (viewModel.isShowingProgress.value == true) {
            progressDialog.show()
        }
    }

    /**
     * User wants to find his address.
     */
    private val onSearchGo = TextView.OnEditorActionListener { textView, _, event ->
        if (event == null || event.action != KeyEvent.ACTION_DOWN)
            return@OnEditorActionListener false

        viewModel.searchAddress(textView.text.toString())
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupDataBind()
        setupMapFragment()

        mainThread = Handler()

        drawerLayout = findViewById(R.id.drawerLayout)
        vgTopBar = findViewById(R.id.vgTopBar)
        eSearch = findViewById(R.id.eSearch)
        eSearch.setOnEditorActionListener(onSearchGo)
        progressDialog = ProgressDialog(this@MapActivity)
        progressDialog.isIndeterminate = true

        observeData()
    }

    private fun setupDataBind() {
        val contentView = DataBindingUtil.setContentView<ActivityMapsBinding>(this, R.layout.activity_maps)
        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
        preferencesViewModel = ViewModelProviders.of(this).get(PreferencesViewModel::class.java)
        contentView.modelView = viewModel
    }

    private fun setupMapFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun observeData() {
        viewModel.startPosition.observe(this, Observer { latLng ->
            if (latLng != null) {
                pointMapTo(latLng)
            } else {
                clearMapOverlay()
            }
        })
        viewModel.finishPosition.observe(this, Observer { latLng ->
            clearMapOverlay()
            if (latLng != null) {
                fitCurrentRouteOnScreen()
                viewModel.updateRoute()
            } else {
                viewModel.setStartPosition(viewModel.startPosition.value)
            }
        })
        viewModel.isShowingProgress.observe(this, Observer { isShowingProgress ->
            if (isShowingProgress == true) {
                mainThread.postDelayed(showProgress, 700)
            } else {
                progressDialog.hide()
            }
        })
        preferencesViewModel.selectedDay.observe(this, Observer { refreshMarkers() })
        viewModel.directionLine.observe(this, Observer { rectLine ->
            if (mMap == null)
                return@Observer

            if (rectLine != null)
                mMap?.addPolyline(rectLine)
            else
                Toast.makeText(this@MapActivity, R.string.warning_pathNotFound,
                        Toast.LENGTH_SHORT).show()
        })
        viewModel.weathers.observe(this, Observer { weathers ->
            weathers?.forEach {
                addMark(it)
            }
        })
        viewModel.isShowingTopBar.observe(this, Observer { aBoolean ->
            if (aBoolean == true)
                showTopBar()
            else
                hideTopBar()
        })
        viewModel.shouldFinish.observe(this, Observer { aBoolean ->
            if (aBoolean == true)
                finish()
        })
        viewModel.error.observe(this, Observer { error ->
            if (error != null)
                showErrorDialog(error)
        })
        viewModel.actionRequest.observe(this, Observer { actionRequest ->
            if (actionRequest != null)
                showActionRequest(actionRequest)
        })
    }

    fun currentLocationFabClick(view: View) {
        setCurrentLocationAsStartPositionRequestingPermission()
        viewModel.sendFirebaseFabEvent()
    }

    fun setCurrentLocationAsStartPositionRequestingPermission() {
        val hasPermission = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            setCurrentLocationAsStartPosition()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQ_PERMISSION_FOR_CURRENT_LOCATION)
        }
    }

    fun setCurrentLocationAsStartPosition() {
        clearMapOverlay()
        val currentLocation = viewModel.getCurrentLocation()
        if (currentLocation != null) {
            viewModel.setStartPosition(currentLocation)
        } else {
            viewModel.updateCurrentLocation(this)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        fitToScreenWhenLayoutIsReady()
    }

    private fun fitToScreenWhenLayoutIsReady() {
        val view = findViewById<View>(R.id.vgMain)
        val observer = view.viewTreeObserver
        observer.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                fitCurrentRouteOnScreen()
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    public override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putParcelable("startPosition", viewModel.startPosition.value)
        outState?.putParcelable("finishPosition", viewModel.finishPosition.value)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            viewModel.setStartPosition(savedInstanceState.getParcelable<LatLng>("startPosition"))
            viewModel.setFinishPosition(savedInstanceState.getParcelable<LatLng>("finishPosition"))
        }
    }

    override fun onBackPressed() {
        viewModel.back()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        googleMap.setOnMapClickListener({ latLng -> viewModel.addPoint(latLng) })
        googleMap.setOnMapLongClickListener({ viewModel.requestClear() })
        googleMap.setOnInfoWindowClickListener(goToWeatherForecastWebPage)
        googleMap.setInfoWindowAdapter(ForecastInfoWindowAdapter(this))

        clearMapOverlay()

        mainThread.postDelayed({
            if (viewModel.startPosition.value == null)
                setCurrentLocationAsStartPositionRequestingPermission()
        }, 500)
    }

    private val goToWeatherForecastWebPage = GoogleMap.OnInfoWindowClickListener { marker ->
        val weather = mapMarkerToWeather[marker]
        val url = if (weather?.url == null) "" else weather.url
        AndroidUtil.openUrl(url, this@MapActivity)
    }

    private fun pointMapTo(center: LatLng?) {
        if (center != null) mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 8f))
    }

    private fun fitCurrentRouteOnScreen() {
        pointMapTo(viewModel.getRouteBounds())
    }

    private fun pointMapTo(latLng: LatLngBounds?) {
        if (latLng == null) return

        try {
            mMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(latLng, 70), ANIM_ROUTE_TIME, null)
        } catch (e: IllegalStateException) {
            Mint.logException(e)
        }
    }

    private fun addMark(weather: Weather?) {
        if (weather?.address == null) return

        val selectedDay = preferencesViewModel.selectedDay.value
        val forecast = weather.getForecast(selectedDay)

        val markerOptions = MarkerOptions()
                .position(weather.latLng)
                .title(forecast.text)
                .icon(forecast.icon)
        val marker = mMap?.addMarker(markerOptions)
        if (marker != null) mapMarkerToWeather.put(marker, weather)
    }

    fun clearMapOverlay() {
        mMap?.clear()
        mapMarkerToWeather.clear()
    }

    private fun showErrorDialog(error: Error) {
        AlertDialog.Builder(this)
                .setTitle(R.string.warning)
                .setMessage(error.messageId)
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    viewModel.dismissError()
                    dialog.dismiss()
                }.show()
    }

    private fun showActionRequest(actionRequest: ActionRequest) {
        AlertDialog.Builder(this)
                .setMessage(actionRequest.messageId)
                .setPositiveButton(R.string.yes) { dialog, which -> viewModel.actionRequestAccepted(actionRequest) }.setNegativeButton(R.string.cancel) { dialog, which -> dialog.dismiss() }.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQ_PERMISSION_FOR_CURRENT_LOCATION) {
            setCurrentLocationAsStartPosition()
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /**
     * Search button click event.
     */
    fun toggleSearch(view: View) {
        viewModel.toggleTopBar()
    }

    private fun hideTopBar() {
        vgTopBar.animate().alpha(0f)
        eSearch.visibility = View.INVISIBLE
        AndroidUtil.hideKeyboard(this, eSearch)
    }

    private fun showTopBar() {
        vgTopBar.animate().alpha(1f)
        eSearch.visibility = View.VISIBLE
        eSearch.requestFocus()
        AndroidUtil.showKeyboard(this, eSearch)
    }

    /**
     * User wants to open side menu.
     */
    fun onMenu(view: View) {
        drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun refreshMarkers() {
        if (mapMarkerToWeather.isEmpty())
            return

        val markerWeathers = this.mapMarkerToWeather
        this.mapMarkerToWeather = HashMap()
        for ((key, value) in markerWeathers) {
            key.remove()
            addMark(value)
        }
    }

    fun onClearSearch(view: View) {
        eSearch.setText("")
    }

    companion object {
        private val ANIM_ROUTE_TIME = 1200
        private val REQ_PERMISSION_FOR_CURRENT_LOCATION = 0x1
    }
}
