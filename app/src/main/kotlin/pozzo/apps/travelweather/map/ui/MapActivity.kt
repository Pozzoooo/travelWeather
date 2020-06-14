package pozzo.apps.travelweather.map.ui

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.group_flag_shelf.*
import kotlinx.android.synthetic.main.group_top_bar.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import pozzo.apps.tools.AndroidUtil
import pozzo.apps.travelweather.BuildConfig
import pozzo.apps.travelweather.R
import pozzo.apps.travelweather.common.ShadowResByBottomRight
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
import pozzo.apps.travelweather.forecast.model.point.StartPoint
import pozzo.apps.travelweather.forecast.model.point.WeatherPoint
import pozzo.apps.travelweather.map.ReturnAnimation
import pozzo.apps.travelweather.map.factory.AdapterFactory
import pozzo.apps.travelweather.map.manager.DaySelectionListManager
import pozzo.apps.travelweather.map.manager.PermissionManager
import pozzo.apps.travelweather.map.manager.TimeSelectionListManager
import pozzo.apps.travelweather.map.overlay.LastRunKey
import pozzo.apps.travelweather.map.overlay.MapTutorial
import pozzo.apps.travelweather.map.viewmodel.MapViewModel
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MapActivity : BaseActivity() {
    private lateinit var mainThread: Handler
    private lateinit var returnAnimation: ReturnAnimation

    private lateinit var mapFragment: MapFragment
    @Inject lateinit var viewModel: MapViewModel
    private lateinit var permissionManager: PermissionManager
    private lateinit var daySelectionListManager: DaySelectionListManager
    private lateinit var timeSelectionListManager: TimeSelectionListManager

    private var lastDisplayedRoute = Route()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.mainThread = Handler()
        this.permissionManager = PermissionManager(this, viewModel)
        this.returnAnimation = ReturnAnimation(resources)
        setupDataBind()
        setupMapFragment()
        setupView()
        observeViewModel()
        showAdd()
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

        startFlag.setOnTouchListener(startDraggingFlag)
        finishFlag.setOnTouchListener(startDraggingFlag)
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

    private val startDraggingFlag = View.OnTouchListener { view: View, motionEvent: MotionEvent ->
        view.visibility = View.INVISIBLE
        val flagResource = if (view.id == R.id.startFlag) R.drawable.start_flag else R.drawable.finish_flag
        val flag = resources.getDrawable(flagResource, null)
        if (motionEvent.action == MotionEvent.ACTION_DOWN) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                startFlag.startDragAndDrop(null, ShadowResByBottomRight(startFlag, flag), null, 0)
            } else {
                @Suppress("DEPRECATION")
                startFlag.startDrag(null, ShadowResByBottomRight(startFlag, flag), null, 0)
            }
        }

        return@OnTouchListener true
    }

    private fun observeViewModel() {
        viewModel.routeData.observe(this, Observer { updateRoute(it) })

        viewModel.weatherPointsData.observe(this, Observer { updateWeatherPoints(it) })
        viewModel.selectedDayTime.observe(this, Observer { updateDayTime(it) })
        viewModel.isShowingProgress.observe(this, Observer { progressDialogStateChanged(it) })
        viewModel.isShowingSearch.observe(this, Observer { if (it == true) showSearch() else hideSearch() })
        viewModel.shouldFinish.observe(this, Observer { if (it == true) finish() })
        viewModel.error.observe(this, Observer { if (it != null) showError(it) })
        viewModel.warning.observe(this, Observer { if (it != null) showWarning(it) })
        viewModel.actionRequest.observe(this, Observer { if (it != null) showActionRequest(it) })
        viewModel.permissionRequest.observe(this, Observer { if (it != null) permissionManager.requestPermissions(it) })
        viewModel.overlay.observe(this, Observer { it?.let{ showOverlay(it) } })
        viewModel.mapSettingsData.observe(this, Observer { it?.let { mapFragment.updateMapSettings(it) } })
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
            moveFlagsBackToShelf()
        } else {
            route.polyline?.let { mapFragment.plotRoute(it) }
            setStartPoint(route.startPoint)
            setFinishPoint(route)
            pointMapToRoute(route)
        }

        lastDisplayedRoute = route
    }

    private fun setStartPoint(startPoint: StartPoint?) {
        if (startPoint != null) {
            addMark(startPoint)
            startFlag.visibility = View.INVISIBLE
        } else {
            startFlag.visibility = View.VISIBLE
        }
    }

    private fun setFinishPoint(route: Route) {
        val finishPoint = route.finishPoint
        if (finishPoint != null) {
            addMark(finishPoint)
            finishFlag.visibility = View.INVISIBLE
            lDragTheFlag.visibility = View.INVISIBLE
        } else {
            finishFlag.visibility = View.VISIBLE
            finishFlag.alpha = if (route.startPoint == null) .4F else 1F
            finishFlag.isEnabled = route.startPoint != null
        }
    }

    private fun pointMapToRoute(route: Route) {
        if (route.isComplete()) {
            mapFragment.updateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    LatLngBounds.builder()
                        .include(route.startPoint!!.position)
                        .include(route.finishPoint!!.position).build(), 70))
        } else if (route.startPoint != null) {
            try {
                mapFragment.updateCamera(CameraUpdateFactory.newLatLngZoom(route.startPoint.position, 8f))
            } catch (e: NullPointerException) {
                Bug.get().logException(e)//seems to be some weird TT mars error... gonna keep an eye
            }
        }
    }

    private fun clearMap() {
        mapFragment.clearMapOverlay()
    }

    private fun moveFlagsBackToShelf() {
        //todo after animation state
        val projection = mapFragment.getProjection()
        if (projection != null) {
            lastDisplayedRoute.startPoint?.marker?.let { returnAnimation.animate(startFlag, projection.toScreenLocation(it.position)) }
            lastDisplayedRoute.finishPoint?.marker?.let { returnAnimation.animate(finishFlag, projection.toScreenLocation(it.position)) }
        }
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
        outState.putParcelable("startPosition", viewModel.routeData.value!!.startPoint?.position)
        outState.putParcelable("finishPosition", viewModel.routeData.value!!.finishPoint?.position)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.getParcelable<LatLng?>("startPosition")?.let { viewModel.setStartPosition(it) }
        savedInstanceState.getParcelable<LatLng?>("finishPosition")?.let { viewModel.setFinishPosition(it) }
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
        lDaySelection.visibility = View.VISIBLE
        spinnerDaySelection.visibility = View.VISIBLE
        spinnerTimeSelection.visibility = View.VISIBLE
        AndroidUtil.hideKeyboard(this, eSearch)
    }

    private fun showSearch() {
        eSearch.visibility = View.VISIBLE
        lDaySelection.visibility = View.GONE
        spinnerDaySelection.visibility = View.GONE
        spinnerTimeSelection.visibility = View.GONE
        eSearch.requestFocus()
        AndroidUtil.showKeyboard(this, eSearch)
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

    private fun showAdd() {
        if (Random().nextInt(100) == 7) {
            setupTestDevices()
            MobileAds.initialize(this) {
                val adRequest = AdRequest.Builder().build()
                adView.loadAd(adRequest)
            }
        }
    }

    private fun setupTestDevices() {
        if (BuildConfig.DEBUG) {
            //search for "Use RequestConfiguration.Builder"
            val testDeviceIds = listOf("9E8F0A1C9CCFBB1C6000A97B644FBE47")
            val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
            MobileAds.setRequestConfiguration(configuration)
        }
    }
}
