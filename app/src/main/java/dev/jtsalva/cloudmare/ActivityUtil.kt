package dev.jtsalva.cloudmare

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import java.io.InvalidClassException
import java.security.InvalidParameterException
import kotlin.reflect.KClass

inline fun <T : Activity> Context.startActivity(activityClass: KClass<T>) =
    startActivity(Intent(this, activityClass.java))

inline fun <T : Activity> Context.startActivityWithExtras(activityClass: KClass<T>, vararg extras: Pair<String, Any>) =
    startActivity(
        Intent(this, activityClass.java).putExtras(*extras)
    )

inline fun <T : Activity> Context.startActivityWithExtrasForResult(activityClass: KClass<T>,
                                                            requestCode: Int,
                                                            vararg extras: Pair<String, Any>) =
    if (this is Activity) startActivityForResult(
        Intent(this, activityClass.java).putExtras(*extras), requestCode
    ) else throw InvalidClassException("only subclasses of Activity can call this extension function")


inline fun Intent.putExtras(vararg extras: Pair<String, Any>): Intent {
    for (pair in extras) when (pair.second) {
        is Int -> putExtra(pair.first, pair.second as Int)
        is String -> putExtra(pair.first, pair.second as String)
        is Parcelable -> putExtra(pair.first, pair.second as Parcelable)

        else -> throw InvalidParameterException("unrecognized extra type")
    }
    return this
}