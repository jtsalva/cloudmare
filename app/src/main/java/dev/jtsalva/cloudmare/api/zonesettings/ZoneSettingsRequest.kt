package dev.jtsalva.cloudmare.api.zonesettings

import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.getAdapter
import org.json.JSONArray
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ZoneSettingsRequest(context: CloudMareActivity) : Request<ZoneSettingsRequest>(context) {

    suspend fun get(zoneId: String) = suspendCoroutine<SecurityLevelResponse> { cont ->
        requestTAG = GET
        get("zones/$zoneId/settings") {
            cont.resume(
                getAdapter(SecurityLevelResponse::class).
                    fromJson(it.toString()) ?: SecurityLevelResponse(success = false)
            )
        }
    }

    suspend fun update(zoneId: String, zoneSettings: List<ZoneSetting>) = suspendCoroutine<ZoneSettingsResponse> { cont ->
        val payload = JSONArray(zoneSettings.toString())

        requestTAG = UPDATE
        patch("zones/$zoneId/settings", payload) {
            cont.resume(
                getAdapter(ZoneSettingsResponse::class).
                    fromJson(it.toString()) ?: ZoneSettingsResponse(success = false)
            )
        }
    }

}