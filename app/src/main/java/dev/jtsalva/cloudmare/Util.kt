package dev.jtsalva.cloudmare

import android.content.Context
import android.content.Intent
import android.widget.Toast
import java.security.InvalidParameterException

fun Context.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.longToast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

fun Intent.putStringExtras(vararg keyValuePairs: String): Intent {
    if (keyValuePairs.size % 2 != 0) throw InvalidParameterException("Invalid number of key value pair extras")

    for (i in 0 until keyValuePairs.size - 1 step 2) {
        putExtra(keyValuePairs[i], keyValuePairs[i + 1])
    }

    return this
}