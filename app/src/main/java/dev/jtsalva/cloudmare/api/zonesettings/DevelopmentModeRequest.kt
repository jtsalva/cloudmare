package dev.jtsalva.cloudmare.api.zonesettings

import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.getAdapter
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DevelopmentModeRequest(context: CloudMareActivity) : Request<DevelopmentModeRequest>(context) {

    suspend fun get(zoneId: String) = suspendCoroutine<ZoneSettingResponse> { cont ->
        requestTAG = GET
        get("zones/$zoneId/settings/development_mode") {
            cont.resume(
                getAdapter(ZoneSettingResponse::class).
                    fromJson(it.toString()) ?: ZoneSettingResponse(success = false)
            )
        }
    }

    suspend fun update(zoneId: String, value: String) = suspendCoroutine<ZoneSettingResponse> { cont ->
        val payload = JSONObject().apply {
            put("value", value)
        }

        requestTAG = UPDATE
        patch("zones/$zoneId/settings/development_mode", payload) {
            cont.resume(
                getAdapter(ZoneSettingResponse::class).
                    fromJson(it.toString()) ?: ZoneSettingResponse(success = false)
            )
        }
    }

}