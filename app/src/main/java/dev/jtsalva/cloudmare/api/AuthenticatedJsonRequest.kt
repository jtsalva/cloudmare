package dev.jtsalva.cloudmare.api

import com.android.volley.toolbox.JsonObjectRequest
import dev.jtsalva.cloudmare.Auth
import org.json.JSONObject
import com.android.volley.Response as JsonResponse

class AuthenticatedJsonRequest(
    method: Int,
    url: String,
    jsonRequest: JSONObject?,
    listener: JsonResponse.Listener<JSONObject>,
    errorListener: JsonResponse.ErrorListener?
) : JsonObjectRequest(method, url, jsonRequest, listener, errorListener) {

    override fun getHeaders(): MutableMap<String, String> = mutableMapOf(
        Pair("Content-Type", "application/json"),
        Pair("X-Auth-Email", Auth.email),
        Pair("X-Auth-Key", Auth.apiKey)
    )

}