package pozzo.apps.travelweather.map.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.LinearLayout
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.group_flag_shelf.*
import kotlinx.android.synthetic.main.group_flag_shelf.view.*
import pozzo.apps.travelweather.BuildConfig
import pozzo.apps.travelweather.R
import pozzo.apps.travelweather.common.ShadowResByBottomRight
import pozzo.apps.travelweather.forecast.model.Route
import pozzo.apps.travelweather.map.ReturnAnimation

//TODO animate flag when getting current location
class FlagShelf: LinearLayout {
    private val returnAnimation = ReturnAnimation(resources)

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.group_flag_shelf, this, true)

        startFlag.setOnTouchListener(startDraggingFlag)
        finishFlag.setOnTouchListener(startDraggingFlag)

        if (BuildConfig.DEBUG) {
            waypoint.visibility = View.VISIBLE
            waypoint.setOnTouchListener(startDraggingFlag)
        }

        updateFlagsVisibility(Route())
    }

    fun moveFlagsBackToShelf(route: Route, projection: Projection) {
        route.startPoint?.marker?.let {
            returnAnimation.animate(startFlag, correctRelativePoint(it.position, projection))
        }
        route.finishPoint?.marker?.let {
            returnAnimation.animate(finishFlag, correctRelativePoint(it.position, projection))
        }
        if (BuildConfig.DEBUG) {
            route.waypoints?.getOrNull(0)?.marker?.let {//TODO all flags
                returnAnimation.animate(waypoint, correctRelativePoint(it.position, projection))
            }
        }
    }

    private fun correctRelativePoint(position: LatLng, projection: Projection): Point {
        val point = projection.toScreenLocation(position)
        point.x -= x.toInt()
        point.y -= y.toInt()
        return point
    }

    fun updateFlagsVisibility(route: Route) {
        startFlag.visibility = if (route.startPoint == null) View.VISIBLE else View.INVISIBLE
        finishFlag.visibility = if (route.finishPoint == null) View.VISIBLE else View.INVISIBLE

        updateWaypointVisibilityState(route)
        updateFinishFlagEnabledState(route)
        updateDragTheFlagMessageVisibility(route)
    }

    private fun updateWaypointVisibilityState(route: Route) {
        //TODO global definition of max waypoints
        waypoint.visibility = if(BuildConfig.DEBUG && route.waypoints?.size ?: 0 < 5)
            View.VISIBLE else View.INVISIBLE

        val isRouteComplete = route.isComplete()
        waypoint.isEnabled = isRouteComplete
        waypoint.alpha = if (!isRouteComplete) .4F else 1F//TODO define standard alpha config for flags

    }

    private fun updateFinishFlagEnabledState(route: Route) {
        val enableFinishFlag = route.startPoint != null
        finishFlag.alpha = if (!enableFinishFlag) .4F else 1F
        finishFlag.isEnabled = enableFinishFlag
    }

    private fun updateDragTheFlagMessageVisibility(route: Route) {
        if (lDragTheFlag.visibility == View.VISIBLE && route.finishPoint == null) {
            lDragTheFlag.visibility = View.GONE
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private val startDraggingFlag = OnTouchListener { view: View, motionEvent: MotionEvent ->
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
}
