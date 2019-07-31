package dev.jtsalva.cloudmare.api.zonesettings

import android.content.Context
import android.util.Log
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.endpointUrl
import dev.jtsalva.cloudmare.api.getAdapter
import org.json.JSONArray
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ZoneSettingsRequest(context: Context) : Request(context, "zones") {

    companion object {
        private const val TAG = "SecurityLevelRequest"
    }

    override var requestTAG: String = TAG
        set(value) {
            field = "$TAG.$value"
        }

    fun cancelAll(method: String) = cancelAll(TAG, method)

    suspend fun get(zoneId: String) = suspendCoroutine<SecurityLevelResponse> { cont ->
        requestTAG = "get"
        get(null, endpointUrl(endpoint, zoneId, "settings")) {
            Log.d(TAG, it.toString())

            cont.resume(
                getAdapter(SecurityLevelResponse::class.java).fromJson(it.toString())
                    ?: SecurityLevelResponse(success = false)
            )
        }
    }

    suspend fun update(zoneId: String, zoneSettings: List<ZoneSetting>) = suspendCoroutine<ZoneSettingsResponse> { cont ->
        cancelAll("update")

        val data = JSONArray(zoneSettings.toString())

        requestTAG = "update"
        patch(data, endpointUrl(endpoint, zoneId, "settings")) {
            Log.d(TAG, it.toString())

            cont.resume(
                getAdapter(ZoneSettingsResponse::class.java).
                    fromJson(it.toString()) ?: ZoneSettingsResponse(success = false)
            )
        }
    }

}