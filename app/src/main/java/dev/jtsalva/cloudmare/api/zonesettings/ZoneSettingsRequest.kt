package dev.jtsalva.cloudmare.api.zonesettings

import android.content.Context
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.endpointUrl
import dev.jtsalva.cloudmare.api.getAdapter
import org.json.JSONArray
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ZoneSettingsRequest(context: Context) : Request(context, "zones") {

    suspend fun get(zoneId: String) = suspendCoroutine<SecurityLevelResponse> { cont ->
        requestTAG = Request.GET
        get(null, endpointUrl(endpoint, zoneId, "settings")) {
            Timber.v(it.toString())

            cont.resume(
                getAdapter(SecurityLevelResponse::class).fromJson(it.toString())
                    ?: SecurityLevelResponse(success = false)
            )
        }
    }

    suspend fun update(zoneId: String, zoneSettings: List<ZoneSetting>) = suspendCoroutine<ZoneSettingsResponse> { cont ->
        val data = JSONArray(zoneSettings.toString())

        requestTAG = Request.UPDATE
        patch(data, endpointUrl(endpoint, zoneId, "settings")) {
            Timber.v(it.toString())

            cont.resume(
                getAdapter(ZoneSettingsResponse::class).
                    fromJson(it.toString()) ?: ZoneSettingsResponse(success = false)
            )
        }
    }

}