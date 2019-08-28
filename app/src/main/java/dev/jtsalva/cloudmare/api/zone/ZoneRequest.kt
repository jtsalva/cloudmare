package dev.jtsalva.cloudmare.api.zone

import android.content.Context
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.getAdapter
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ZoneRequest(context: Context) : Request(context, "zones") {

    suspend fun list() = suspendCoroutine<ZoneListResponse> { cont ->
        get(null) {
            Timber.d(it.toString())

            cont.resume(
                getAdapter(ZoneListResponse::class.java).fromJson(it.toString()) ?: ZoneListResponse(success = false)
            )
        }
    }

}