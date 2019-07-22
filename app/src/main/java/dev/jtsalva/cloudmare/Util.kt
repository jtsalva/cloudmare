package dev.jtsalva.cloudmare

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import java.io.InvalidClassException
import java.security.InvalidParameterException

fun Context.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.longToast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

fun <T : Activity> Context.startActivity(activityClass: Class<T>) = startActivity(Intent(this, activityClass))

fun <T : Activity> Context.startActivityWithExtras(activityClass: Class<T>, vararg extras: Pair<String, Any>) =
    startActivity(
        Intent(this, activityClass).putExtras(*extras)
    )

fun <T : Activity> Context.startActivityWithExtrasForResult(activityClass: Class<T>,
                                                            requestCode: Int,
                                                            vararg extras: Pair<String, Any>) =
    if (this is Activity) startActivityForResult(
        Intent(this, activityClass).putExtras(*extras), requestCode
    ) else throw InvalidClassException("only subclasses of Activity can call this extension function")


fun Intent.putExtras(vararg extras: Pair<String, Any>): Intent {
    for (pair in extras) when (pair.second) {
        is Int -> putExtra(pair.first, pair.second as Int)
        is String -> putExtra(pair.first, pair.second as String)

        else -> throw InvalidParameterException("unrecognized extra type")
    }
    return this
}