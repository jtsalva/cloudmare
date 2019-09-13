package dev.jtsalva.cloudmare.api.zonesettings

import android.content.Context
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.getAdapter
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DevelopmentModeRequest(context: Context) : Request(context) {

    suspend fun get(zoneId: String) = suspendCoroutine<DevelopmentModeResponse> { cont ->
        requestTAG = GET
        get("zones/$zoneId/settings/development_mode") {
            cont.resume(
                getAdapter(DevelopmentModeResponse::class).
                    fromJson(it.toString()) ?: DevelopmentModeResponse(success = false)
            )
        }
    }

    suspend fun update(zoneId: String, value: String) = suspendCoroutine<DevelopmentModeResponse> { cont ->
        val payload = JSONObject().apply {
            put("value", value)
        }

        requestTAG = UPDATE
        patch("zones/$zoneId/settings/development_mode", payload) {
            cont.resume(
                getAdapter(DevelopmentModeResponse::class).
                    fromJson(it.toString()) ?: DevelopmentModeResponse(success = false)
            )
        }
    }

}