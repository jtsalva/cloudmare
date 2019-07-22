package dev.jtsalva.cloudmare.api.zonesettings

import android.content.Context
import android.util.Log
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.ResponseListener
import dev.jtsalva.cloudmare.api.endpointUrl
import org.json.JSONObject

class DevelopmentModeRequest(context: Context) : Request(context, "zones") {

    companion object {
        private const val TAG = "DevelopmentModeRequest"
    }

    fun get(zoneId: String, callback: ResponseListener<DevelopmentModeResponse>) =
        super.get(null, endpointUrl(endpoint, zoneId, "settings/development_mode")) {
            Log.d(TAG, it.toString())

            callback(
                getAdapter(DevelopmentModeResponse::class.java).
                    fromJson(it.toString()) ?: DevelopmentModeResponse(success = false)
            )
        }

    fun set(zoneId: String, value: DevelopmentMode.Value, callback: ResponseListener<DevelopmentModeResponse>) {
        val data = JSONObject()
        data.put("value", value.toString())

        super.patch(data, endpointUrl(endpoint, zoneId, "settings/development_mode")) {
            Log.d(TAG, it.toString())

            callback(
                getAdapter(DevelopmentModeResponse::class.java).
                    fromJson(it.toString()) ?: DevelopmentModeResponse(success = false)
            )
        }
    }

}