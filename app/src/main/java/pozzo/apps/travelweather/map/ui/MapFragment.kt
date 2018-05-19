package pozzo.apps.travelweather.map.ui

import android.animation.ObjectAnimator
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.view.DragEvent
import android.view.View
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.splunk.mint.Mint
import pozzo.apps.tools.AndroidUtil
import pozzo.apps.travelweather.forecast.adapter.ForecastInfoWindowAdapter
import pozzo.apps.travelweather.forecast.model.MapPoint
import pozzo.apps.travelweather.map.AnimationCallbackTrigger
import pozzo.apps.travelweather.map.viewmodel.MapViewModel

class MapFragment : SupportMapFragment() {
    companion object {
        private const val ANIM_ROUTE_TIME = 1200
    }

    private var map: GoogleMap? = null
    private lateinit var viewModel: MapViewModel
    private lateinit var mainThread: Handler
    private val mapPointByMarkerId = HashMap<String, MapPoint>()

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
        val mapPoint = mapPointByMarkerId[marker.id]
        mapPoint?.onClickLoadUrl?.let { AndroidUtil.openUrl(it, activity) }
    }

    fun pointMapTo(center: LatLng, animationCallback: AnimationCallbackTrigger? = null) {
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 8f), animationCallback)
    }

    fun pointMapTo(latLng: LatLngBounds, animationCallback: AnimationCallbackTrigger? = null) {
        try {
            animationCallback?.animationStarted()
            map?.animateCamera(CameraUpdateFactory.newLatLngBounds(latLng, 70), ANIM_ROUTE_TIME, animationCallback)
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

        mainThread.postDelayed({
            viewModel.onMapReady(this)
        }, 500)
    }

    private fun addDragListener() {
        view?.apply {
            setOnDragListener(draggingFinishFlag)
        } ?: Mint.logException(IllegalStateException("Trying to add drag listener without view"))
    }

    private val markerDragListener = object : GoogleMap.OnMarkerDragListener {
        override fun onMarkerDragEnd(marker: Marker) {
            println("drag ended $marker")
        }

        override fun onMarkerDragStart(marker: Marker) {
            println("drag started $marker")
        }

        override fun onMarkerDrag(marker: Marker) {
            println("drag $marker")
        }
    }

    fun addMark(mapPoint: MapPoint) : Marker? {
        val markerOptions = MarkerOptions()
                .position(mapPoint.position)
                .title(mapPoint.title)
                .icon(mapPoint.icon)
                .draggable(mapPoint.isDraggable)
        return map?.addMarker(markerOptions)
                ?.apply {
                    mapPointByMarkerId[id] = mapPoint
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
