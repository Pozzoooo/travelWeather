package pozzo.apps.travelweather.map.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.DragEvent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.group_flag_shelf.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pozzo.apps.tools.AndroidUtil
import pozzo.apps.travelweather.App
import pozzo.apps.travelweather.core.CoroutineSettings
import pozzo.apps.travelweather.core.PermissionChecker
import pozzo.apps.travelweather.core.bugtracker.Bug
import pozzo.apps.travelweather.forecast.ForecastTitleFormatter
import pozzo.apps.travelweather.forecast.adapter.ForecastInfoWindowAdapter
import pozzo.apps.travelweather.forecast.model.point.MapPoint
import pozzo.apps.travelweather.forecast.model.point.StartPoint
import pozzo.apps.travelweather.map.DaggerMapComponent
import pozzo.apps.travelweather.map.MapSettings
import pozzo.apps.travelweather.map.viewmodel.MapViewModel
import javax.inject.Inject

class MapFragment : SupportMapFragment() {
    private var map: GoogleMap? = null
    private var onMapReadyListener: OnMapReadyCallback? = null
    private lateinit var viewModel: MapViewModel
    private lateinit var mainThread: Handler

    @Inject
    protected lateinit var permissionChecker: PermissionChecker
    @Inject
    protected lateinit var forecastTitleFormatter: ForecastTitleFormatter

    init {
        DaggerMapComponent.builder()
                .appComponent(App.component())
                .build()
                .inject(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.viewModel = ViewModelProvider(activity!!).get(MapViewModel::class.java)
        this.mainThread = Handler(Looper.getMainLooper())
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        initializeMaps()
    }

    private fun initializeMaps() {
        getMapAsync { onMapReady(it) }
    }

    private val goToWeatherForecastWebPage = GoogleMap.OnInfoWindowClickListener { marker ->
        val mapPoint = marker.tag as MapPoint
        mapPoint.redirectUrl?.let { AndroidUtil.openUrl(it, activity) }
    }

    fun updateCameraQuick(cameraUpdate: CameraUpdate) {
        updateCamera(cameraUpdate, 1)
    }

    fun updateCamera(cameraUpdate: CameraUpdate, speed: Int = 1000) {
        GlobalScope.launch(CoroutineSettings.ui) {
            try {
                map?.animateCamera(cameraUpdate, speed, null)
            } catch (e: IllegalStateException) {
                Bug.get().logException(e)
            }
        }
    }

    fun plotRoute(polylineOptions: PolylineOptions) {
        map?.addPolyline(polylineOptions)
    }

    private fun onMapReady(googleMap: GoogleMap) {
        val activity = this.activity
        if (activity?.isFinishing != false) {
            return
        }

        setupMap(googleMap, activity)
        clearMapOverlay()
        addDragListener()

        onMapReadyListener?.onMapReady(googleMap)
        mainThread.postDelayed({
            viewModel.onMapReady(this)
        }, 500)
    }

    fun setOnMapReadyListener(listener: OnMapReadyCallback) {
        onMapReadyListener = listener
        map?.let { listener.onMapReady(it) }
    }

    fun removeOnMapReadyListener() {
        onMapReadyListener = null
    }

    private fun setupMap(googleMap: GoogleMap, context: Context) {
        this.map = googleMap

        googleMap.setOnInfoWindowClickListener(goToWeatherForecastWebPage)
        googleMap.setInfoWindowAdapter(ForecastInfoWindowAdapter(context))
        googleMap.setOnMarkerDragListener(markerDragListener)
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        viewModel.mapSettingsData.value?.let {
            updateMapSettings(it)
        }
    }

    @SuppressLint("MissingPermission")
    fun updateMapSettings(mapSettings: MapSettings) {
        if (permissionChecker.isGranted(ACCESS_COARSE_LOCATION)
                || permissionChecker.isGranted(ACCESS_FINE_LOCATION)) {
            map?.isMyLocationEnabled = mapSettings.isMyLocationEnabled()
        }
    }

    fun clearMapOverlay() {
        map?.clear()
    }

    private fun addDragListener() {
        view?.setOnDragListener(dragListener)
                ?: Bug.get().logException(IllegalStateException("Trying to add drag listener without view"))
    }

    private val markerDragListener = object : GoogleMap.OnMarkerDragListener {
        override fun onMarkerDragEnd(marker: Marker) {
            val tag = marker.tag

            val flagPointOnScreen = requireProjection().toScreenLocation(marker.position)
            val correctedOffsetPosition = correctFlagOffset(flagPointOnScreen)
            if (tag is StartPoint) {
                viewModel.setStartPosition(correctedOffsetPosition)
            } else {
                viewModel.setFinishPosition(correctedOffsetPosition)
            }
        }

        override fun onMarkerDragStart(marker: Marker) {
            marker.setAnchor(2F, 1F)
            viewModel.dragStarted()
        }

        override fun onMarkerDrag(marker: Marker) {
            viewModel.checkEdge(requireProjection().visibleRegion.latLngBounds, marker.position)
                    ?.let { cameraUpdate -> updateCameraQuick(cameraUpdate) }
        }
    }

    private fun correctFlagOffset(flagPointOnScreen: Point): LatLng {
        val projection = requireProjection()
        flagPointOnScreen.x -= viewModel.flagOffset
        return projection.fromScreenLocation(flagPointOnScreen)
    }

    @SuppressLint("ObjectAnimatorBinding")
    fun addMark(mapPoint: MapPoint): Marker? {
        if (!this.isAdded) return null

        val markerOptions = MarkerOptions()
                .position(mapPoint.position)
                .anchor(1F, 1F)
                .title(mapPoint.getTitle(requireContext(), forecastTitleFormatter))
                .icon(mapPoint.icon)
                .draggable(mapPoint.isDraggable)
        return map?.addMarker(markerOptions)?.apply {
            tag = mapPoint
            if (mapPoint.shouldFadeIn) ObjectAnimator.ofFloat(this, "alpha", 0F, 1F).setDuration(500L).start()
        }
    }

    private val dragListener = View.OnDragListener { _, event ->
        return@OnDragListener when (event.action) {
            DragEvent.ACTION_DROP -> {
                viewModel.flagDragActionFinished(
                        correctFlagOffset(Point(event.x.toInt(), event.y.toInt())))
                true
            }
            DragEvent.ACTION_DRAG_STARTED -> {
                viewModel.dragStarted()
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                false
            }
            else -> {
                viewModel.checkEdge(requireProjection().visibleRegion.latLngBounds,
                        requireProjection().fromScreenLocation(Point(event.x.toInt(), event.y.toInt())))
                        ?.let { cameraUpdate -> updateCameraQuick(cameraUpdate) }
                false
            }
        }
    }

    private fun requireProjection(): Projection {
        return getProjection() ?: throw Exception("Dragging without a map?")
    }

    fun getProjection(): Projection? {
        return map?.projection
    }
}
