package dev.jtsalva.cloudmare.api.user

import android.content.Context
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.getAdapter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserRequest(context: Context) : Request(context) {

    suspend fun getDetails() = suspendCoroutine<UserDetailsResponse> { cont ->
        requestTAG = GET
        get("user") {
            cont.resume(
                getAdapter(UserDetailsResponse::class).fromJson(it.toString())
                    ?: UserDetailsResponse(success = false)
            )
        }
    }

}