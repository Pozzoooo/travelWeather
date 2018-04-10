package pozzo.apps.travelweather.common

import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.view.View

class ShadowResByBottomRight(private val parentView: View, private val shadow: Drawable) : View.DragShadowBuilder(parentView) {
    override fun onProvideShadowMetrics(size: Point, touch: Point) {
        val width = parentView.width / 2
        val height = parentView.height / 2

        shadow.setBounds(0, 0, width, height)

        size.set(width, height)
        touch.set(width, height)
    }

    override fun onDrawShadow(canvas: Canvas) {
        shadow.draw(canvas)
    }
}
