package dev.jtsalva.cloudmare.api.user

import android.content.Context
import android.util.Log
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.ResponseListener

class UserRequest(context: Context) : Request(context, "user") {

    companion object {
        private const val TAG = "UserRequest"
    }

    fun getDetails(callback: ResponseListener<UserDetailsResponse>) =
        super.get(null) {
            Log.d(TAG, it.toString())

            callback(
                getAdapter(UserDetailsResponse::class.java).
                    fromJson(it.toString())
            )
        }

}