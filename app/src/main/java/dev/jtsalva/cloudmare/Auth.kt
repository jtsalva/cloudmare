package dev.jtsalva.cloudmare

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dev.jtsalva.cloudmare.api.tokens.TokenRequest
import dev.jtsalva.cloudmare.api.user.UserRequest
import timber.log.Timber

object Auth {
    private const val SHARED_PREFS_FILENAME = "dev.jtsalva.cloudmare.eprefs"
    private const val EMAIL_PREFS = "email"
    private const val API_KEY_PREFS = "api_key"
    private const val API_TOKEN_PREFS = "api_token"

    private val CONTENT_TYPE_HEADER = "Content-Type" to "application/json"

    var email = ""
        private set
    var apiKey = ""
        private set
    var apiToken = ""
        private set

    val isNotSet get() = !(isUsingApiKey || isUsingApiToken)

    inline val isUsingApiKey get() = apiKey.isNotEmpty()
    inline val isUsingApiToken get() = apiToken.isNotEmpty()

    val headers: MutableMap<String, String> get() =
        mutableMapOf(CONTENT_TYPE_HEADER).apply {
            when {
                isUsingApiKey -> {
                    put("X-Auth-Email", email)
                    put("X-Auth-Key", apiKey)
                }
                isUsingApiToken -> {
                    put("Authorization", "Bearer $apiToken")
                }
                else ->
                    Timber.e("Neither apiKey or apiToken authorization methods are set!")
            }
        }

    private fun getPrefs(context: Context): SharedPreferences =
        EncryptedSharedPreferences.create(
            context,
            SHARED_PREFS_FILENAME,
            MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    fun load(context: Context) = with(getPrefs(context)) {
        email = getString(EMAIL_PREFS, null) ?: ""
        apiKey = getString(API_KEY_PREFS, null) ?: ""
        apiToken = getString(API_TOKEN_PREFS, null) ?: ""
    }

    suspend fun testValidity(activity: CloudMareActivity) =
        if (isUsingApiKey) UserRequest(activity).getDetails()
        else TokenRequest(activity).verify()

    class Editor(private val context: Context) {
        fun set(
            email: String = "",
            apiKey: String = "",
            apiToken: String = ""
        ) {
            getPrefs(context).edit(commit = true) {
                putString(EMAIL_PREFS, email)
                putString(API_KEY_PREFS, apiKey)
                putString(API_TOKEN_PREFS, apiToken)
            }
            load(context)
        }
    }
}
