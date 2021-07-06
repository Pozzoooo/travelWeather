package pozzo.apps.travelweather.map.ui

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.group_flag_shelf.*
import kotlinx.android.synthetic.main.group_top_bar.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import pozzo.apps.tools.AndroidUtil
import pozzo.apps.travelweather.R
import pozzo.apps.travelweather.core.BaseActivity
import pozzo.apps.travelweather.core.CoroutineSettings.ui
import pozzo.apps.travelweather.core.Error
import pozzo.apps.travelweather.core.Warning
import pozzo.apps.travelweather.core.action.ActionRequest
import pozzo.apps.travelweather.core.bugtracker.Bug
import pozzo.apps.travelweather.databinding.ActivityMapsBinding
import pozzo.apps.travelweather.forecast.model.DayTime
import pozzo.apps.travelweather.forecast.model.Route
import pozzo.apps.travelweather.forecast.model.Time
import pozzo.apps.travelweather.forecast.model.point.MapPoint
import pozzo.apps.travelweather.forecast.model.point.WeatherPoint
import pozzo.apps.travelweather.map.factory.AdapterFactory
import pozzo.apps.travelweather.map.manager.DaySelectionListManager
import pozzo.apps.travelweather.map.manager.PermissionManager
import pozzo.apps.travelweather.map.manager.TimeSelectionListManager
import pozzo.apps.travelweather.map.overlay.LastRunKey
import pozzo.apps.travelweather.map.overlay.MapTutorial
import pozzo.apps.travelweather.map.viewmodel.MapViewModel
import java.util.*

class MapActivity : BaseActivity() {
    private lateinit var mainThread: Handler

    private lateinit var mapFragment: MapFragment
    private lateinit var viewModel: MapViewModel
    private lateinit var permissionManager: PermissionManager
    private lateinit var daySelectionListManager: DaySelectionListManager
    private lateinit var timeSelectionListManager: TimeSelectionListManager

    private var lastDisplayedRoute = Route()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        this.mainThread = Handler()
        this.permissionManager = PermissionManager(this, viewModel)
        setupMaps()
        setupDataBind()
        setupMapFragment()
        setupView()
        observeViewModel()
        observeFlagSizeChange()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(MapViewModel::class.java)
    }

    private fun setupMaps() {
        try {
            MapsInitializer.initialize(this)
        } catch (e: GooglePlayServicesNotAvailableException) {
            Bug.get().logException(e)
        }
    }

    private fun setupDataBind() {
        val contentView = DataBindingUtil.setContentView<ActivityMapsBinding>(this, R.layout.activity_maps)
        contentView.viewModel = viewModel
    }

    private fun setupMapFragment() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as MapFragment
    }

    private fun setupView() {
        eSearch.setOnEditorActionListener(onSearchGo)
        setupDaySelection()
    }

    private fun setupDaySelection() {
        daySelectionListManager = DaySelectionListManager(spinnerDaySelection, AdapterFactory(),
                object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setSelectedDay(position)
            }
        })
        timeSelectionListManager = TimeSelectionListManager(spinnerTimeSelection, AdapterFactory(),
                object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setSelectedTime(Time(position))
            }
        })
    }

    private val onSearchGo = TextView.OnEditorActionListener { textView, _, event ->
        if (event == null || event.action != KeyEvent.ACTION_DOWN)
            return@OnEditorActionListener false

        viewModel.toggleSearch(textView.text.toString())
        return@OnEditorActionListener true
    }

    private fun observeViewModel() {
        viewModel.selectedDayTime.observe(this, { updateDayTime(it) })
        viewModel.isShowingProgress.observe(this, { progressDialogStateChanged(it) })
        viewModel.isShowingSearch.observe(this, { if (it == true) showSearch() else hideSearch() })
        viewModel.shouldFinish.observe(this, { if (it == true) finish() })
        viewModel.error.observe(this, { if (it != null) showError(it) })
        viewModel.warning.observe(this, { if (it != null) showWarning(it) })
        viewModel.actionRequest.observe(this, { if (it != null) showActionRequest(it) })
        viewModel.permissionRequest.observe(this, { if (it != null) permissionManager.requestPermissions(it) })
        viewModel.overlay.observe(this, { it?.let{ showOverlay(it) } })

        mapFragment.setOnMapReadyListener {
            mapFragment.removeOnMapReadyListener()
            observersBoundToMap()
        }
    }

    private fun observersBoundToMap() {
        viewModel.routeData.observe(this, { updateRoute(it) })
        viewModel.weatherPointsData.observe(this, { updateWeatherPoints(it) })
        viewModel.pointMapToRoute.observe(this, { if (it != null) pointMapToRoute(it) })
        viewModel.mapSettingsData.observe(this, { it?.let { mapFragment.updateMapSettings(it) } })
    }

    private fun observeFlagSizeChange() {//TODO can I improve this?
        startFlag.viewTreeObserver.addOnGlobalLayoutListener {
            viewModel.flagOffset = startFlag.width
        }
    }

    private fun updateDayTime(dayTime: DayTime) {
        daySelectionListManager.safeSelection(dayTime.day.index)
        timeSelectionListManager.setSelection(dayTime.time)
    }

    private fun showOverlay(overlay: LastRunKey) {
        val mapTutorial = MapTutorial()
        when(overlay) {
            LastRunKey.DRAG_THE_FLAG -> mapTutorial.playDragTheFlag(this)
            LastRunKey.DRAG_AGAIN -> mapTutorial.playDragAgain(this)
            LastRunKey.DAY_SELECTION -> mapTutorial.playDaySelectionTutorial(this)
            LastRunKey.FORECAST_DETAILS -> mapTutorial.playOpenForecastDetails(this)
            else -> Bug.get().logException("Missing show overlay $overlay")
        }
    }

    private fun updateRoute(route: Route) {
        clearMap()
        if (route.isEmpty()) {
            mapFragment.getProjection()?.let { flagShelf.moveFlagsBackToShelf(lastDisplayedRoute, it) }
        } else {
            route.polyline?.let { mapFragment.plotRoute(it) }
            flagShelf.updateFlagsVisibility(route)
            route.getAllPoints().forEach(::addMark)
            pointMapToRoute(route)
        }

        lastDisplayedRoute = route
    }

    private fun pointMapToRoute(route: Route) {
        if (route.isComplete()) {
            mapFragment.updateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    LatLngBounds.builder()
                        .include(route.startPoint!!.position)
                        .include(route.finishPoint!!.position).build(), 400))
        } else if (route.startPoint != null) {
            try {
                mapFragment.updateCamera(CameraUpdateFactory.newLatLngZoom(route.startPoint.position, 8f))
            } catch (e: NullPointerException) {
                Bug.get().logException(e)//seems to be some weird TT mars error... gonna keep an eye
            }
        }
        viewModel.mapIsCentered()
    }

    private fun clearMap() {
        mapFragment.clearMapOverlay()
    }

    private fun progressDialogStateChanged(isShowingProgress: Boolean?) {
        if (isShowingProgress == true) {
            mainThread.postDelayed(triggerCheckedShowProgress, 200)
        } else
            progressBar.visibility = View.GONE
    }

    private val triggerCheckedShowProgress = Runnable {
        if (viewModel.isShowingProgress.value == true) {
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun updateWeatherPoints(weatherPoints: Channel<WeatherPoint>) {
        GlobalScope.launch(ui) {
            for (it in weatherPoints) {
                if (isFinishing) break
                daySelectionListManager.updateDaySelections(it.forecastSize)
                it.marker?.remove()
                addMark(it)
            }
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable("route", viewModel.routeData.value)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        savedInstanceState.getParcelable<Route?>("route")?.let { viewModel.updateRoute(it) }
    }

    override fun onBackPressed() {
        viewModel.back()
    }

    private fun showError(error: Error) {
        AlertDialog.Builder(this)
            .setTitle(R.string.warning)
            .setMessage(error.messageId)
            .setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
            .setOnDismissListener { viewModel.errorDismissed() }
            .show()
    }

    private fun showWarning(warning: Warning) {
        Toast.makeText(this, getString(warning.messageId), Toast.LENGTH_LONG).show()
    }

    private fun showActionRequest(actionRequest: ActionRequest) {
        AlertDialog.Builder(this)
            .setMessage(actionRequest.messageId)
            .setPositiveButton(R.string.yes) { _, _ -> viewModel.actionRequestAccepted(actionRequest) }
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .setOnDismissListener { viewModel.actionRequestDismissed() }
            .show()
    }

    private fun hideSearch() {
        eSearch.visibility = View.GONE
        eSearch.setText("")
        lDaySelection.visibility = View.VISIBLE
        spinnerDaySelection.visibility = View.VISIBLE
        spinnerTimeSelection.visibility = View.VISIBLE
        AndroidUtil.hideKeyboard(this, eSearch)
        bSearch.setImageResource(android.R.drawable.ic_menu_search)
    }

    private fun showSearch() {
        eSearch.visibility = View.VISIBLE
        lDaySelection.visibility = View.GONE
        spinnerDaySelection.visibility = View.GONE
        spinnerTimeSelection.visibility = View.GONE
        eSearch.requestFocus()
        AndroidUtil.showKeyboard(this, eSearch)
        bSearch.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
    }

    private fun addMark(mapPoint: MapPoint) {
        val marker = mapFragment.addMark(mapPoint)
        mapPoint.marker = marker
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (!permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}
