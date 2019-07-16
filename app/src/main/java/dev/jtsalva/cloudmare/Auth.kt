package dev.jtsalva.cloudmare

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class Auth {

    companion object {
        private const val SHARED_PREFS_FILENAME = "dev.jtsalva.cloudmare.eprefs"

        var email = ""
        var apiKey = ""

        val isSet: Boolean get() = !notSet
        val notSet: Boolean get() = email == "" || apiKey == ""

        private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        private fun getPrefs(context: Context): SharedPreferences =
            EncryptedSharedPreferences.create(
                SHARED_PREFS_FILENAME,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

        fun load(context: Context) {
            val preferences = getPrefs(context)

            email = preferences.getString("email", null) ?: ""
            apiKey = preferences.getString("api_key", null) ?: ""
        }

        fun save(context: Context) =
            getPrefs(context).edit(commit = true) {
                putString("email", email)
                putString("api_key", apiKey)
            }
    }

}