import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent

class TapAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Handle accessibility events if necessary
    }

    override fun onInterrupt() {
        // Handle interruptions to the service
    }

    fun performTap(x: Float, y: Float) {
        // Create a path for the tap gesture
        val path = Path().apply {
            moveTo(x, y)
        }

        // Build the gesture description
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()

        // Dispatch the gesture
        dispatchGesture(gesture, null, null)
    }
}
