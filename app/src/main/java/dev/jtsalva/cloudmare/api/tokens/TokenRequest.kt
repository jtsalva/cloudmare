package dev.jtsalva.cloudmare.api.tokens

import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.Response
import dev.jtsalva.cloudmare.api.getAdapter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TokenRequest(context: CloudMareActivity) : Request<TokenRequest>(context) {

    suspend fun verify() =
        suspendCoroutine<Response> { cont ->
            requestTAG = GET
            get("/user/tokens/verify") {
                cont.resume(
                    getAdapter(Response::class).
                        fromJson(it.toString()) ?: Response(success = false)
                )
            }
        }

}