package pozzo.apps.travelweather.map.ui

import android.app.AlertDialog
import android.app.ProgressDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GravityCompat
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.activity_maps.*
import pozzo.apps.tools.AndroidUtil
import pozzo.apps.travelweather.R
import pozzo.apps.travelweather.common.ShadowResByBottomRight
import pozzo.apps.travelweather.core.BaseActivity
import pozzo.apps.travelweather.core.Error
import pozzo.apps.travelweather.databinding.ActivityMapsBinding
import pozzo.apps.travelweather.forecast.model.MapPoint
import pozzo.apps.travelweather.forecast.model.Weather
import pozzo.apps.travelweather.map.AnimationCallbackTrigger
import pozzo.apps.travelweather.map.action.ActionRequest
import pozzo.apps.travelweather.map.viewmodel.MapViewModel
import pozzo.apps.travelweather.map.viewmodel.PreferencesViewModel
import pozzo.apps.travelweather.map.viewrequest.LocationPermissionRequest
import pozzo.apps.travelweather.map.viewrequest.PermissionRequest
import java.util.*

/**
 * todo add more analytics tracking, something more intelligent
 * todo ta removendo o current location listener quando da dismiss no dialog de loading?
 * todo improve layout
 */
class MapActivity : BaseActivity() {
    companion object {
        private const val REQ_PERMISSION_FOR_CURRENT_LOCATION = 0x1
    }

    private var mapMarkerToWeather = HashMap<Marker, Weather>()

    private lateinit var progressDialog: ProgressDialog

    private lateinit var mainThread: Handler
    private lateinit var animationCallback: AnimationCallbackTrigger

    private lateinit var mapFragment: MapFragment
    private lateinit var viewModel: MapViewModel
    private lateinit var preferencesViewModel: PreferencesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.mainThread = Handler()
        setupViewModel()
        setupDataBind()
        setupMapFragment()
        setupView()
        observeViewModel()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
        preferencesViewModel = ViewModelProviders.of(this).get(PreferencesViewModel::class.java)
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
        //todo replace progress dialog and add one with a text message
        progressDialog = ProgressDialog(this)
        progressDialog.isIndeterminate = true
        animationCallback = AnimationCallbackTrigger(triggerCheckedShowProgress)

        bFinishPosition.setOnTouchListener(startDraggingFinishFlag)
    }

    private val onSearchGo = TextView.OnEditorActionListener { textView, _, event ->
        if (event == null || event.action != KeyEvent.ACTION_DOWN)
            return@OnEditorActionListener false

        viewModel.searchAddress(textView.text.toString())
        return@OnEditorActionListener true
    }

    private val startDraggingFinishFlag = View.OnTouchListener { view: View, motionEvent: MotionEvent ->
        val flag = resources.getDrawable(R.drawable.finish_flag, null)
        if (motionEvent.action == MotionEvent.ACTION_DOWN) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                bFinishPosition.startDragAndDrop(null, ShadowResByBottomRight(bFinishPosition, flag), null, 0)
            } else {
                bFinishPosition.startDrag(null, ShadowResByBottomRight(bFinishPosition, flag), null, 0)
            }
        }

        return@OnTouchListener true
    }

    //todo seems like I can organise it better on a more object oriented way, with a single object holding a lot of definitions
    private fun observeViewModel() {
        preferencesViewModel.selectedDay.observe(this, Observer { refreshMarkers() })

        viewModel.startPosition.observe(this, Observer { startPositionChanged(it) })
        viewModel.finishPosition.observe(this, Observer { finishPositionChanged(it) })
        viewModel.isShowingProgress.observe(this, Observer { progressDialogStateChanged(it) })
        viewModel.directionLine.observe(this, Observer { if (it != null) mapFragment.plotRoute(it) })
        viewModel.weathers.observe(this, Observer { if (it != null) showWeathers(it) })
        viewModel.isShowingTopBar.observe(this, Observer { if (it == true) showTopBar() else hideTopBar() })
        viewModel.shouldFinish.observe(this, Observer { if (it == true) finish() })
        viewModel.error.observe(this, Observer { if (it != null) showErrorDialog(it) })
        viewModel.actionRequest.observe(this, Observer { if (it != null) showActionRequest(it) })
        viewModel.permissionRequest.observe(this, Observer { if (it != null) requestPermissions(it) })
    }

    private fun startPositionChanged(startPosition: LatLng?) {
        clearMap()
        if (startPosition != null) mapFragment.pointMapTo(startPosition)
    }

    private fun finishPositionChanged(finishPosition: LatLng?) {
        clearMap()
        val routeBounds = viewModel.getRouteBounds()
        if (routeBounds != null) {
            mapFragment.pointMapTo(routeBounds, animationCallback)
        }
    }

    private fun clearMap() {
        mapMarkerToWeather.clear()
        mapFragment.clearMapOverlay()
    }

    private fun progressDialogStateChanged(isShowingProgress: Boolean?) {
        if (isShowingProgress == true) {
            if (!animationCallback.isAnimating)
                mainThread.postDelayed(triggerCheckedShowProgress, 200)
        } else
            progressDialog.hide()
    }

    private val triggerCheckedShowProgress = Runnable {
        if (viewModel.isShowingProgress.value == true) {
            progressDialog.show()
        }
    }

    private fun showWeathers(weathers: List<Weather>) {
        weathers.forEach {
            addMark(it)
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
                val routeBounds = viewModel.getRouteBounds()
                if (routeBounds != null)
                    mapFragment.pointMapTo(routeBounds, animationCallback)
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
            viewModel.setStartPosition(savedInstanceState.getParcelable("startPosition"))
            viewModel.setFinishPosition(savedInstanceState.getParcelable("finishPosition"))
        }
    }

    override fun onBackPressed() {
        viewModel.back()
    }

    private fun showErrorDialog(error: Error) {
        AlertDialog.Builder(this)
                .setTitle(R.string.warning)
                .setMessage(error.messageId)
                .setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
                .setOnDismissListener { viewModel.errorDismissed() }
                .show()
    }

    private fun showActionRequest(actionRequest: ActionRequest) {
        AlertDialog.Builder(this)
                .setMessage(actionRequest.messageId)
                .setPositiveButton(R.string.yes) { _, _ -> viewModel.actionRequestAccepted(actionRequest) }
                .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                .setOnDismissListener { viewModel.actionRequestDismissed() }
                .show()
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

    fun onMenu(view: View) {
        drawerLayout.openDrawer(GravityCompat.START)
    }

    fun onClearSearch(view: View) {
        eSearch.setText("")
    }

    private fun refreshMarkers() {
        val markerWeathers = this.mapMarkerToWeather
        this.mapMarkerToWeather = HashMap()
        markerWeathers.forEach {
            it.key.remove()
            addMark(it.value)
        }
    }

    fun addMark(weather: Weather?) {
        if (weather?.address == null) return

        val selectedDay = preferencesViewModel.selectedDay.value
        val forecast = weather.getForecast(selectedDay!!)

        val marker = mapFragment.addMark(MapPoint(forecast.icon, forecast.text, weather.latLng, weather.url))
        if (marker != null) mapMarkerToWeather[marker] = weather
    }

    private fun requestPermissions(permissionRequest: PermissionRequest) {
        ActivityCompat.requestPermissions(this, permissionRequest.permissions, REQ_PERMISSION_FOR_CURRENT_LOCATION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQ_PERMISSION_FOR_CURRENT_LOCATION) {
            if (PackageManager.PERMISSION_GRANTED == grantResults[0])
                viewModel.onPermissionGranted(LocationPermissionRequest(viewModel), this)
            else
                viewModel.onPermissionDenied(
                        LocationPermissionRequest(viewModel))
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}
