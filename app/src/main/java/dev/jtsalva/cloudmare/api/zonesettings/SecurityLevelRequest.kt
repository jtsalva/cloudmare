package dev.jtsalva.cloudmare.api.zonesettings

import android.content.Context
import android.util.Log
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.ResponseListener
import dev.jtsalva.cloudmare.api.endpointUrl
import org.json.JSONObject

class SecurityLevelRequest(context: Context) : Request(context, "zones") {

    companion object {
        private const val TAG = "SecurityLevelRequest"
    }

    fun get(zoneId: String, callback: ResponseListener<SecurityLevelResponse>) =
        super.get(null, endpointUrl(endpoint, zoneId, "settings/security_level")) {
            Log.d(TAG, it.toString())

            callback(
                getAdapter(SecurityLevelResponse::class.java).
                    fromJson(it.toString()) ?: SecurityLevelResponse(success = false)
            )
        }

    fun set(zoneId: String, value: SecurityLevel.Value, callback: ResponseListener<SecurityLevelResponse>) {
        val data = JSONObject()
        data.put("value", value.toString())

        super.patch(data, endpointUrl(endpoint, zoneId, "settings/security_level")) {
            Log.d(TAG, it.toString())

            callback(
                getAdapter(SecurityLevelResponse::class.java).
                    fromJson(it.toString()) ?: SecurityLevelResponse(success = false)
            )
        }
    }

}