package dev.jtsalva.cloudmare.api.zone

import android.content.Context
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.endpointUrl
import dev.jtsalva.cloudmare.api.getAdapter
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ZoneRequest(context: Context) : Request(context, "zones") {

    suspend fun list(pageNumber: Int = 1, perPage: Int = 20) = suspendCoroutine<ZoneListResponse> { cont ->
        val params = urlParams("page" to pageNumber, "per_page" to perPage)
        val url = endpointUrl(endpoint, params)

        get(null, url) {
            Timber.v(it.toString())

            cont.resume(
                getAdapter(ZoneListResponse::class.java).fromJson(it.toString()) ?: ZoneListResponse(success = false)
            )
        }
    }

}