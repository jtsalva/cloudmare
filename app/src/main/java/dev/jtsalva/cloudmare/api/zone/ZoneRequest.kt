package dev.jtsalva.cloudmare.api.zone

import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.api.Request

class ZoneRequest(context: CloudMareActivity) : Request<ZoneRequest>(context) {

    suspend fun list(pageNumber: Int = 1, perPage: Int = 20): ZoneListResponse {
        val params = urlParams("page" to pageNumber, "per_page" to perPage)

        requestTAG = "list"
        return httpGet("zones$params")
    }

}