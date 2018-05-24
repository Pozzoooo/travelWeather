package pozzo.apps.travelweather.map.ui

import android.animation.ObjectAnimator
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
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
import com.splunk.mint.Mint
import pozzo.apps.tools.AndroidUtil
import pozzo.apps.travelweather.forecast.adapter.ForecastInfoWindowAdapter
import pozzo.apps.travelweather.forecast.model.point.MapPoint
import pozzo.apps.travelweather.forecast.model.point.StartPoint
import pozzo.apps.travelweather.map.viewmodel.MapViewModel

class MapFragment : SupportMapFragment() {
    private var map: GoogleMap? = null
    private lateinit var viewModel: MapViewModel
    private lateinit var mainThread: Handler

    override fun onAttach(context: Context?) {
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
            Mint.logException(e)
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
            Mint.logException(e)
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

        googleMap.setOnMapClickListener({ latLng -> viewModel.addPoint(latLng) })
        googleMap.setOnMapLongClickListener({ viewModel.requestClear() })
        googleMap.setOnInfoWindowClickListener(goToWeatherForecastWebPage)
        googleMap.setInfoWindowAdapter(ForecastInfoWindowAdapter(activity))
        googleMap.setOnMarkerDragListener(markerDragListener)

        clearMapOverlay()
        addDragListener()
        addMapObservers()

        mainThread.postDelayed({
            viewModel.onMapReady(this)
        }, 500)
    }

    private fun addDragListener() {
        view?.apply {
            setOnDragListener(draggingFinishFlag)
        } ?: Mint.logException(IllegalStateException("Trying to add drag listener without view"))
    }

    private fun addMapObservers() {
        viewModel.cameraState.observe(this, Observer { it?.let { updateCamera(it) } })
    }

    private val markerDragListener = object : GoogleMap.OnMarkerDragListener {
        override fun onMarkerDragEnd(marker: Marker) {
            //todo maybe I can resolve it better with polymorphism
            val tag = marker.tag
            if (tag is StartPoint) {
                viewModel.setStartPosition(marker.position)
            } else {
                viewModel.setFinishPosition(marker.position)
            }
        }

        override fun onMarkerDragStart(marker: Marker) { }
        override fun onMarkerDrag(marker: Marker) { }
    }

    fun addMark(mapPoint: MapPoint) : Marker? {
        val markerOptions = MarkerOptions()
                .position(mapPoint.position)
                .anchor(1F, 1F)
                .title(mapPoint.title)
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

    private val draggingFinishFlag = View.OnDragListener { _, event ->
        return@OnDragListener when(event.action) {
            DragEvent.ACTION_DROP -> {
                getProjection()?.let {
                    viewModel.finishFlagDragActionFinished(it.fromScreenLocation(Point(event.x.toInt(), event.y.toInt())))
                } ?: Mint.logException(IllegalStateException("Trying to drag to the map with map not ready yet"))
                false
            }
            DragEvent.ACTION_DRAG_STARTED -> {
                viewModel.finishFlagDragActionStarted()
                true
            }
            else -> false
        }
    }

    private fun getProjection() : Projection? = map?.projection
}
