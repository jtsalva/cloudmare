package dev.jtsalva.cloudmare.api.zonesettings

import android.content.Context
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.endpointUrl
import dev.jtsalva.cloudmare.api.getAdapter
import org.json.JSONObject
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DevelopmentModeRequest(context: Context) : Request(context, "zones") {

    suspend fun get(zoneId: String) = suspendCoroutine<DevelopmentModeResponse> { cont ->
        requestTAG = Request.GET
        get(null, endpointUrl(endpoint, zoneId, "settings/development_mode")) {
            Timber.v(it.toString())

            cont.resume(
                getAdapter(DevelopmentModeResponse::class).fromJson(it.toString()) ?: DevelopmentModeResponse(
                    success = false
                )
            )
        }
    }

    suspend fun update(zoneId: String, value: String) = suspendCoroutine<DevelopmentModeResponse> { cont ->
        val data = JSONObject().apply {
            put("value", value)
        }

        requestTAG = Request.UPDATE
        patch(data, endpointUrl(endpoint, zoneId, "settings/development_mode")) {
            Timber.v(it.toString())

            cont.resume(
                getAdapter(DevelopmentModeResponse::class).
                    fromJson(it.toString()) ?: DevelopmentModeResponse(success = false)
            )
        }
    }

}