package dev.jtsalva.cloudmare.api.zone

import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.getAdapter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ZoneRequest(context: CloudMareActivity) : Request(context) {

    suspend fun list(pageNumber: Int = 1, perPage: Int = 20) = suspendCoroutine<ZoneListResponse> { cont ->
        val params = urlParams("page" to pageNumber, "per_page" to perPage)

        requestTAG = LIST
        get("zones$params") {
            cont.resume(
                getAdapter(ZoneListResponse::class).fromJson(it.toString()) ?: ZoneListResponse(success = false)
            )
        }
    }

}