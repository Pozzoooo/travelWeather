package pozzo.apps.travelweather.map

import android.content.res.Resources
import android.graphics.Point
import android.view.View
import android.view.animation.AccelerateInterpolator
import pozzo.apps.travelweather.R

class ReturnAnimation(resources: Resources) {
    private val yOffset = resources.getDimensionPixelSize(R.dimen.button_size) / 2
    private val animationTime = resources.getInteger(android.R.integer.config_longAnimTime).toLong()

    fun animate(view: View, position: Point) {
        view.visibility = View.VISIBLE
        val originalX = view.x
        val originalY = view.y

        view.x = position.x.toFloat() - view.width
        view.y = position.y.toFloat() - yOffset

        view.animate()
                .setDuration(animationTime)
                .x(originalX)
                .y(originalY)
                .setInterpolator(AccelerateInterpolator())
                .start()
    }
}
