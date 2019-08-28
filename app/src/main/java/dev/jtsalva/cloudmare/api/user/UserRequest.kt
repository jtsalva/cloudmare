package dev.jtsalva.cloudmare.api.user

import android.content.Context
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.getAdapter
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserRequest(context: Context) : Request(context, "user") {

    suspend fun getDetails() = suspendCoroutine<UserDetailsResponse> { cont ->
        get(null) {
            Timber.d(it.toString())

            cont.resume(
                getAdapter(UserDetailsResponse::class.java).fromJson(it.toString())
                    ?: UserDetailsResponse(success = false)
            )
        }
    }

}