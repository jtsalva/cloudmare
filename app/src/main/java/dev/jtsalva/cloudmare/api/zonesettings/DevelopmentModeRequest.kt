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

    override var requestTAG: String = javaClass.simpleName
        set(value) {
            field = "${javaClass.simpleName}.$value"
        }

    fun cancelAll(method: String) = cancelAll(javaClass.simpleName, method)

    suspend fun get(zoneId: String) = suspendCoroutine<DevelopmentModeResponse> { cont ->
        requestTAG = "get"
        get(null, endpointUrl(endpoint, zoneId, "settings/development_mode")) {
            Timber.v(it.toString())

            cont.resume(
                getAdapter(DevelopmentModeResponse::class.java).fromJson(it.toString()) ?: DevelopmentModeResponse(
                    success = false
                )
            )
        }
    }

    suspend fun set(zoneId: String, value: String) = suspendCoroutine<DevelopmentModeResponse> { cont ->
        cancelAll("set")

        val data = JSONObject().apply {
            put("value", value)
        }

        requestTAG = "set"
        patch(data, endpointUrl(endpoint, zoneId, "settings/development_mode")) {
            Timber.v(it.toString())

            cont.resume(
                getAdapter(DevelopmentModeResponse::class.java).
                    fromJson(it.toString()) ?: DevelopmentModeResponse(success = false)
            )
        }
    }

}