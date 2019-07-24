package dev.jtsalva.cloudmare.api.zone

import android.content.Context
import android.util.Log
import dev.jtsalva.cloudmare.api.Request
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ZoneRequest(context: Context) : Request(context, "zones") {

    companion object {
        private const val TAG = "ZoneRequest"
    }

    suspend fun list() = suspendCoroutine<ZoneListResponse> { cont ->
        super.get(null) {
            Log.d(TAG, it.toString())

            cont.resume(
                getAdapter(ZoneListResponse::class.java).fromJson(it.toString()) ?: ZoneListResponse(success = false)
            )
        }
    }

}