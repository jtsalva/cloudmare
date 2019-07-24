package dev.jtsalva.cloudmare.api.zonesettings

import android.content.Context
import android.util.Log
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.endpointUrl
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SecurityLevelRequest(context: Context) : Request(context, "zones") {

    companion object {
        private const val TAG = "SecurityLevelRequest"
    }

    suspend fun get(zoneId: String) = suspendCoroutine<SecurityLevelResponse> { cont ->
        super.get(null, endpointUrl(endpoint, zoneId, "settings/security_level")) {
            Log.d(TAG, it.toString())

            cont.resume(
                getAdapter(SecurityLevelResponse::class.java).fromJson(it.toString())
                    ?: SecurityLevelResponse(success = false)
            )
        }
    }

    suspend fun set(zoneId: String, value: SecurityLevel.Value) = suspendCoroutine<SecurityLevelResponse> { cont ->
        val data = JSONObject()
        data.put("value", value.toString())

        super.patch(data, endpointUrl(endpoint, zoneId, "settings/security_level")) {
            Log.d(TAG, it.toString())

            cont.resume(
                getAdapter(SecurityLevelResponse::class.java).
                    fromJson(it.toString()) ?: SecurityLevelResponse(success = false)
            )
        }
    }

}