package dev.jtsalva.cloudmare

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import timber.log.Timber

object Auth {
    private const val SHARED_PREFS_FILENAME = "dev.jtsalva.cloudmare.eprefs"
    private const val EMAIL_PREFS = "email"
    private const val API_KEY_PREFS = "api_key"
    private const val API_TOKEN_PREFS = "api_token"

    private val CONTENT_TYPE_HEADER = "Content-Type" to "application/json"

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    var email = ""
    var apiKey = ""

    var apiToken = ""

    val notSet: Boolean get() = !(usingApiKey || usingApiToken)

    inline val usingApiKey: Boolean get() = email != "" && apiKey != ""
    private inline val usingApiToken: Boolean get() = apiToken != ""

    val headers: MutableMap<String, String> get() =
        mutableMapOf(CONTENT_TYPE_HEADER).apply {
            when {
                usingApiKey -> {
                    put("X-Auth-Email", email)
                    put("X-Auth-Key", apiKey)
                }
                usingApiToken -> {
                    put("Authorization", "Bearer $apiToken")
                }

                else ->
                    Timber.e("Neither apiKey or apiToken authorization methods are set!")
            }
        }

    private fun getPrefs(context: Context): SharedPreferences =
        EncryptedSharedPreferences.create(
            SHARED_PREFS_FILENAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    fun load(context: Context) = with (getPrefs(context)) {
        email = getString(EMAIL_PREFS, null) ?: ""
        apiKey = getString(API_KEY_PREFS, null) ?: ""
        apiToken = getString(API_TOKEN_PREFS, null) ?: ""
    }

    fun save(context: Context) =
        getPrefs(context).edit(commit = true) {
            putString(EMAIL_PREFS, email)
            putString(API_KEY_PREFS, apiKey)
            putString(API_TOKEN_PREFS, apiToken)
        }
}