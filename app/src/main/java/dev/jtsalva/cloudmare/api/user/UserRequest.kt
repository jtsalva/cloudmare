package dev.jtsalva.cloudmare.api.user

import android.content.Context
import android.util.Log
import dev.jtsalva.cloudmare.api.Request
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserRequest(context: Context) : Request(context, "user") {

    companion object {
        private const val TAG = "UserRequest"
    }

    suspend fun getDetails() = suspendCoroutine<UserDetailsResponse> { cont ->
        get(null) {
            Log.d(TAG, it.toString())

            cont.resume(
                getAdapter(UserDetailsResponse::class.java).fromJson(it.toString())
                    ?: UserDetailsResponse(success = false)
            )
        }
    }

}