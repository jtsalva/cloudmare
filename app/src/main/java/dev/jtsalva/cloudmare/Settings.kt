package dev.jtsalva.cloudmare

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.core.content.edit

object Settings {
    private const val SHARED_PREFS_FILENAME = "dev.jtsalva.cloudmare.prefs.settings"
    private const val THEME_PREFS = "theme"

    var theme = MODE_NIGHT_NO

    private fun getPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_FILENAME, Context.MODE_PRIVATE)

    fun load(context: Context) = with(getPrefs(context)) {
        theme = getInt(THEME_PREFS, theme)
    }

    fun save(context: Context) =
        getPrefs(context).edit(commit = true) {
            putInt(THEME_PREFS, theme)
        }
}
