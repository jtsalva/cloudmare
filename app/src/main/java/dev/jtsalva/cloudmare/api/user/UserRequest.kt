package dev.jtsalva.cloudmare.api.user

import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.api.Request

class UserRequest(context: CloudMareActivity) : Request<UserRequest>(context) {

    suspend fun getDetails(): UserDetailsResponse {
        requestTAG = "getDetails"
        return super.httpGet("user")
    }

}