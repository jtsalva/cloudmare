package dev.jtsalva.cloudmare.api.zonesettings

import android.content.Context
import android.util.Log
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.endpointUrl
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DevelopmentModeRequest(context: Context) : Request(context, "zones") {

    companion object {
        private const val TAG = "DevelopmentModeRequest"
    }

    suspend fun get(zoneId: String) = suspendCoroutine<DevelopmentModeResponse> { cont ->
        super.get(null, endpointUrl(endpoint, zoneId, "settings/development_mode")) {
            Log.d(TAG, it.toString())

            cont.resume(
                getAdapter(DevelopmentModeResponse::class.java).fromJson(it.toString()) ?: DevelopmentModeResponse(
                    success = false
                )
            )
        }
    }

    suspend fun set(zoneId: String, value: DevelopmentMode.Value) = suspendCoroutine<DevelopmentModeResponse> { cont ->
        val data = JSONObject()
        data.put("value", value.toString())

        super.patch(data, endpointUrl(endpoint, zoneId, "settings/development_mode")) {
            Log.d(TAG, it.toString())

            cont.resume(
                getAdapter(DevelopmentModeResponse::class.java).
                    fromJson(it.toString()) ?: DevelopmentModeResponse(success = false)
            )
        }
    }

}