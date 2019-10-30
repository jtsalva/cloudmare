package dev.jtsalva.cloudmare.api.zonesettings

import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.getAdapter
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PurgeCacheRequest(context: CloudMareActivity) : Request<PurgeCacheRequest>(context) {

    suspend fun purgeAll(zoneId: String) = suspendCoroutine<PurgeCacheResponse> { cont ->
        val payload = JSONObject()
        payload.put("purge_everything", true)

        requestTAG = POST
        post("zones/$zoneId/purge_cache", payload) {
            cont.resume(
                getAdapter(PurgeCacheResponse::class).
                    fromJson(it.toString()) ?: PurgeCacheResponse(success = false)
            )
        }
    }

}