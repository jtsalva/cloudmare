package dev.jtsalva.cloudmare.api.zonesettings

import com.squareup.moshi.Json
import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.R

// TODO: security level and development mode should use this model
data class ZoneSetting(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "value") val value: Any,
    @field:Json(name = "editable") val editable: Boolean? = null,
    @field:Json(name = "modified_on") val modifiedOn: String? = null
) {

    companion object {
        const val SSL_MODE_OFF = "off"
        const val SSL_MODE_FLEXIBLE = "flexible"
        const val SSL_MODE_FULL = "full"
        const val SSL_MODE_STRICT = "strict"

        const val ID_SSL = "ssl"
    }

    class SSLModeTranslator(private val activity: CloudMareActivity) {

        val idToReadable = activity.run {
            mapOf(
                SSL_MODE_OFF to getString(R.string.ssl_off),
                SSL_MODE_FLEXIBLE to getString(R.string.ssl_flexible),
                SSL_MODE_FULL to getString(R.string.ssl_full),
                SSL_MODE_STRICT to getString(R.string.ssl_full_strict)
            )
        }

        inline fun getReadable(id: String): String =
            idToReadable.getValue(id)

        inline fun getId(readable: String): String =
            idToReadable.filterValues { it == readable }.keys.first()

    }

}