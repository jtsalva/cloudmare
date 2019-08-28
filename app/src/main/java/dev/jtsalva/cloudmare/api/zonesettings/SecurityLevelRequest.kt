package dev.jtsalva.cloudmare.api.zonesettings

import android.content.Context
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.endpointUrl
import dev.jtsalva.cloudmare.api.getAdapter
import org.json.JSONObject
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SecurityLevelRequest(context: Context) : Request(context, "zones") {

    override var requestTAG: String = javaClass.simpleName
        set(value) {
            field = "${javaClass.simpleName}.$value"
        }

    fun cancelAll(method: String) = cancelAll(javaClass.simpleName, method)

    suspend fun get(zoneId: String) = suspendCoroutine<SecurityLevelResponse> { cont ->
        requestTAG = "get"
        get(null, endpointUrl(endpoint, zoneId, "settings/security_level")) {
            Timber.d(it.toString())

            cont.resume(
                getAdapter(SecurityLevelResponse::class.java).fromJson(it.toString())
                    ?: SecurityLevelResponse(success = false)
            )
        }
    }

    suspend fun set(zoneId: String, value: String) = suspendCoroutine<SecurityLevelResponse> { cont ->
        cancelAll("set")

        val data = JSONObject()
        data.put("value", value)

        requestTAG = "set"
        patch(data, endpointUrl(endpoint, zoneId, "settings/security_level")) {
            Timber.d(it.toString())

            cont.resume(
                getAdapter(SecurityLevelResponse::class.java).
                    fromJson(it.toString()) ?: SecurityLevelResponse(success = false)
            )
        }
    }

}