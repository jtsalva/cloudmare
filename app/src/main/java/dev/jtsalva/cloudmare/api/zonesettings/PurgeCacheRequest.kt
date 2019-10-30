package dev.jtsalva.cloudmare.api.zonesettings

import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.getAdapter
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PurgeCacheRequest(context: CloudMareActivity) : Request<PurgeCacheRequest>(context) {

    suspend fun purgeAll(zoneId: String) = suspendCoroutine<PurgeCacheResponse> { cont ->
        val data = JSONObject()
        data.put("purge_everything", true)

        requestTAG = GET
        get("zones/$zoneId/purge_cache", data) {
            cont.resume(
                getAdapter(PurgeCacheResponse::class).
                    fromJson(it.toString()) ?: PurgeCacheResponse(success = false)
            )
        }
    }

}