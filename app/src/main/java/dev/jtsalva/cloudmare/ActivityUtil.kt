package dev.jtsalva.cloudmare

import android.app.Activity
import android.content.Intent
import android.os.Parcelable
import java.security.InvalidParameterException
import kotlin.reflect.KClass

inline fun <T : Activity> Activity.startActivity(activityClass: KClass<T>) =
    startActivity(Intent(this, activityClass.java))

inline fun <T : Activity> Activity.startActivityWithExtras(activityClass: KClass<T>, vararg extras: Pair<String, Any>) =
    startActivity(
        Intent(this, activityClass.java).putExtras(*extras)
    )

inline fun <T : Activity> Activity.startActivityWithExtrasForResult(activityClass: KClass<T>,
                                                            requestCode: Int,
                                                            vararg extras: Pair<String, Any>) =
    startActivityForResult(
        Intent(this, activityClass.java).putExtras(*extras), requestCode
    )


inline fun Intent.putExtras(vararg extras: Pair<String, Any>): Intent {
    for (pair in extras) when (pair.second) {
        is Int -> putExtra(pair.first, pair.second as Int)
        is String -> putExtra(pair.first, pair.second as String)
        is Parcelable -> putExtra(pair.first, pair.second as Parcelable)

        else -> throw InvalidParameterException("unrecognized extra type")
    }
    return this
}