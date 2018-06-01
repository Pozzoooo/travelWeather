package pozzo.apps.travelweather.map.ui

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.res.Configuration
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.activity_maps.*
import pozzo.apps.tools.AndroidUtil
import pozzo.apps.travelweather.R
import pozzo.apps.travelweather.common.ShadowResByBottomRight
import pozzo.apps.travelweather.core.BaseActivity
import pozzo.apps.travelweather.core.Error
import pozzo.apps.travelweather.core.Warning
import pozzo.apps.travelweather.databinding.ActivityMapsBinding
import pozzo.apps.travelweather.forecast.model.Route
import pozzo.apps.travelweather.forecast.model.point.MapPoint
import pozzo.apps.travelweather.forecast.model.point.WeatherPoint
import pozzo.apps.travelweather.map.action.ActionRequest
import pozzo.apps.travelweather.map.manager.PermissionManager
import pozzo.apps.travelweather.map.viewmodel.MapViewModel
import pozzo.apps.travelweather.map.viewmodel.PreferencesViewModel
import java.util.*

/*
    todo uma idea eh adicionar aquele botao extra de start activity e ao arrastar e colocar no mapa
        eu removo ele complementamente depois de utilizado
    Poderia ficar desabilitado o finish ateh que ele colocar o start no mapa
    Sera q reposicionar estes botoes na parte do lado da estea, como se fosse uma shelve?
        Nesse caso nem o botao precisaria aparecer, soh os icones
*/
class MapActivity : BaseActivity() {
    private var mapMarkerToWeather = HashMap<Marker, MapPoint>()

    private lateinit var mainThread: Handler

    private lateinit var mapFragment: MapFragment
    private lateinit var viewModel: MapViewModel
    private lateinit var preferencesViewModel: PreferencesViewModel
    private lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.mainThread = Handler()
        this.permissionManager = PermissionManager(this)
        setupViewModel()
        setupDataBind()
        setupMapFragment()
        setupView()
        observeViewModel()
        listenDrawerState()
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

        viewModel.route.observe(this, Observer { updateRoute(it as Route) })

        viewModel.isShowingProgress.observe(this, Observer { progressDialogStateChanged(it) })
        viewModel.isShowingTopBar.observe(this, Observer { if (it == true) showTopBar() else hideTopBar() })
        viewModel.shouldFinish.observe(this, Observer { if (it == true) finish() })
        viewModel.error.observe(this, Observer { if (it != null) showError(it) })
        viewModel.warning.observe(this, Observer { if (it != null) showWarning(it) })
        viewModel.actionRequest.observe(this, Observer { if (it != null) showActionRequest(it) })
        viewModel.permissionRequest.observe(this, Observer { if (it != null) permissionManager.requestPermissions(it) })
    }

    private fun listenDrawerState() {
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerOpened(drawerView: View) {
                viewModel.drawerMenuOpened()
            }
        })
    }

    private fun updateRoute(route: Route) {
        clearMap()
        route.polyline?.let { mapFragment.plotRoute(it) }
        showMapPoints(route)
        route.startPoint?.let { addMark(route.startPoint) }
        route.finishPoint?.let { addMark(route.finishPoint) }
        pointMapToRoute(route)
    }

    private fun clearMap() {
        mapMarkerToWeather.clear()
        mapFragment.clearMapOverlay()
    }

    private fun pointMapToRoute(route: Route) {
        if (route.hasStartAndFinish()) {
            mapFragment.updateCamera(
              CameraUpdateFactory.newLatLngBounds(
                  LatLngBounds.builder()
                      .include(route.startPoint!!.position)
                      .include(route.finishPoint!!.position).build(), 70))
        } else if (route.startPoint != null) {
              mapFragment.updateCamera(CameraUpdateFactory.newLatLngZoom(route.startPoint.position, 8f))
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

    private fun showMapPoints(route: Route) {
        route.mapPoints.forEach {
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
                //todo should I add a lot of lister to just listen after map is ready? why?
//                val routeBounds = viewModel.getRouteBounds()
//                if (routeBounds != null)
//                    mapFragment.updateCamera(routeBounds, animationCallback)
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    public override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putParcelable("startPosition", viewModel.route.value!!.startPoint?.position)
        outState?.putParcelable("finishPosition", viewModel.route.value!!.finishPoint?.position)

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

    private fun refreshMarkers() {
        val markerWeathers = this.mapMarkerToWeather
        this.mapMarkerToWeather = HashMap()
        markerWeathers.forEach {
            it.key.remove()
            addMark(it.value)
        }
    }

    private fun addMark(mapPoint: MapPoint) {
        if (mapPoint is WeatherPoint) {//todo how can I improve this?
            mapPoint.day = preferencesViewModel.selectedDay.value!!
        }

        val marker = mapFragment.addMark(mapPoint)
        if (marker != null) mapMarkerToWeather[marker] = mapPoint
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (!permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}
