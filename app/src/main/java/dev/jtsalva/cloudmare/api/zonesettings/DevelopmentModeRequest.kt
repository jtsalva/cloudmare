package dev.jtsalva.cloudmare.api.zonesettings

import android.content.Context
import android.util.Log
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.endpointUrl
import dev.jtsalva.cloudmare.api.getAdapter
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DevelopmentModeRequest(context: Context) : Request(context, "zones") {

    companion object {
        private const val TAG = "DevelopmentModeRequest"
    }

    override var requestTAG: String = TAG
        set(value) {
            field = "$TAG.$value"
        }

    fun cancelAll(method: String) = cancelAll(TAG, method)

    suspend fun get(zoneId: String) = suspendCoroutine<DevelopmentModeResponse> { cont ->
        requestTAG = "get"
        get(null, endpointUrl(endpoint, zoneId, "settings/development_mode")) {
            Log.d(TAG, it.toString())

            cont.resume(
                getAdapter(DevelopmentModeResponse::class.java).fromJson(it.toString()) ?: DevelopmentModeResponse(
                    success = false
                )
            )
        }
    }

    suspend fun set(zoneId: String, value: String) = suspendCoroutine<DevelopmentModeResponse> { cont ->
        cancelAll("set")

        val data = JSONObject()
        data.put("value", value)

        requestTAG = "set"
        patch(data, endpointUrl(endpoint, zoneId, "settings/development_mode")) {
            Log.d(TAG, it.toString())

            cont.resume(
                getAdapter(DevelopmentModeResponse::class.java).
                    fromJson(it.toString()) ?: DevelopmentModeResponse(success = false)
            )
        }
    }

}