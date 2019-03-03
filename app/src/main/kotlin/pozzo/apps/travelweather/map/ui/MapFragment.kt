package pozzo.apps.travelweather.map.ui

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.view.DragEvent
import android.view.View
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import pozzo.apps.tools.AndroidUtil
import pozzo.apps.travelweather.core.bugtracker.Bug
import pozzo.apps.travelweather.forecast.adapter.ForecastInfoWindowAdapter
import pozzo.apps.travelweather.forecast.model.point.MapPoint
import pozzo.apps.travelweather.forecast.model.point.StartPoint
import pozzo.apps.travelweather.map.viewmodel.MapViewModel

class MapFragment : SupportMapFragment() {
    private var map: GoogleMap? = null
    private lateinit var viewModel: MapViewModel
    private lateinit var mainThread: Handler

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.viewModel = ViewModelProviders.of(activity!!).get(MapViewModel::class.java)
        this.mainThread = Handler()
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        initializeMaps()
    }

    private fun initializeMaps() {
        try {
            MapsInitializer.initialize(context)
        } catch (e: GooglePlayServicesNotAvailableException) {
            Bug.get().logException(e)
        }
        getMapAsync { onMapReady(it) }
    }

    fun clearMapOverlay() {
        map?.clear()
    }

    private val goToWeatherForecastWebPage = GoogleMap.OnInfoWindowClickListener { marker ->
        val mapPoint = marker.tag as MapPoint
        mapPoint.redirectUrl?.let { AndroidUtil.openUrl(it, activity) }
    }

    fun updateCamera(cameraUpdate: CameraUpdate) {
        try {
            map?.animateCamera(cameraUpdate)
        } catch (e: IllegalStateException) {
            Bug.get().logException(e)
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

        this.map = googleMap

        googleMap.setOnInfoWindowClickListener(goToWeatherForecastWebPage)
        googleMap.setInfoWindowAdapter(ForecastInfoWindowAdapter(activity))
        googleMap.setOnMarkerDragListener(markerDragListener)

        clearMapOverlay()
        addDragListener()

        mainThread.postDelayed({
            viewModel.onMapReady(this)
        }, 500)
    }

    private fun addDragListener() {
        view?.setOnDragListener(dragListener) ?: Bug.get().logException(IllegalStateException("Trying to add drag listener without view"))
    }

    private val markerDragListener = object : GoogleMap.OnMarkerDragListener {
        override fun onMarkerDragEnd(marker: Marker) {
            val tag = marker.tag
            if (tag is StartPoint) {
                viewModel.setStartPosition(marker.position)
            } else {
                viewModel.setFinishPosition(marker.position)
            }
        }

        override fun onMarkerDragStart(marker: Marker) {
            viewModel.dragStarted()
        }

        override fun onMarkerDrag(marker: Marker) { }
    }

    @SuppressLint("ObjectAnimatorBinding")
    fun addMark(mapPoint: MapPoint) : Marker? {
        if (!this.isAdded) return null

        val markerOptions = MarkerOptions()
                .position(mapPoint.position)
                .anchor(1F, 1F)
                .title(getString(mapPoint.title!!))//todo is it ok to request it all the time?
                .icon(mapPoint.icon)
                .draggable(mapPoint.isDraggable)
        return map?.addMarker(markerOptions)
                ?.apply {
                    tag = mapPoint
                    if (mapPoint.shouldFadeIn)
                        ObjectAnimator.ofFloat(this, "alpha", 0F, 1F)
                                .setDuration(500L).start()
                }
    }

    private val dragListener = View.OnDragListener { _, event ->
        return@OnDragListener when(event.action) {
            DragEvent.ACTION_DROP -> {
                getProjection()?.let {
                    viewModel.flagDragActionFinished(it.fromScreenLocation(Point(event.x.toInt(), event.y.toInt())))
                } ?: Bug.get().logException(IllegalStateException("Trying to drag to the map with map not ready yet"))
                true
            }
            DragEvent.ACTION_DRAG_STARTED -> {
                viewModel.dragStarted()
                true
            }
            else -> false
        }
    }

    fun getProjection() : Projection? = map?.projection
}
