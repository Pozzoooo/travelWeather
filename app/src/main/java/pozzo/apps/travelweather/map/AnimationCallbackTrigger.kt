package pozzo.apps.travelweather.map

import com.google.android.gms.maps.GoogleMap

class AnimationCallbackTrigger(private val triggerProgress: Runnable) : GoogleMap.CancelableCallback {
    var isAnimating = false
        private set

    fun animationStarted() {
        isAnimating = true
    }

    override fun onFinish() {
        triggerProgress.run()
        isAnimating = false
    }

    override fun onCancel() {
        triggerProgress.run()
        isAnimating = false
    }
}
