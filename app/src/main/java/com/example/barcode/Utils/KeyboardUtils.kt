import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.view.View

object KeyboardUtils {

    // Function to hide the keyboard
    fun hideKeyboard(context: Context, view: View?) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        } else {
            // If no specific view is passed, hide from the root view
            inputMethodManager.hideSoftInputFromWindow((context as Activity).window.decorView.windowToken, 0)
        }
    }
}
