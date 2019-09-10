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
            Timber.v(it.toString())

            cont.resume(
                getAdapter(UserDetailsResponse::class).fromJson(it.toString())
                    ?: UserDetailsResponse(success = false)
            )
        }
    }

}