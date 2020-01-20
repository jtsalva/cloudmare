package dev.jtsalva.cloudmare.api.zonesettings

import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.api.Request
import org.json.JSONObject

class PurgeCacheRequest(context: CloudMareActivity) : Request<PurgeCacheRequest>(context) {

    suspend fun purgeAll(zoneId: String): PurgeCacheResponse {
        val payload = JSONObject()
        payload.put("purge_everything", true)

        requestTAG = "purgeAll"
        return httpPost("zones/$zoneId/purge_cache", payload)
    }

}