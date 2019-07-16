package dev.jtsalva.cloudmare.api.zone

import android.content.Context
import android.util.Log
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.ResponseListener

class ZoneRequest(context: Context) : Request(context, "zones") {

    companion object {
        private const val TAG = "ZoneRequest"
    }

    fun list(callback: ResponseListener<ZoneListResponse>) =
        super.get(null) {
            Log.d(TAG, it.toString())

            callback(
                getAdapter(ZoneListResponse::class.java).
                    fromJson(it.toString())
            )
        }

}