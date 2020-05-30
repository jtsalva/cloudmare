package dev.jtsalva.cloudmare.api

import com.android.volley.toolbox.JsonObjectRequest
import dev.jtsalva.cloudmare.Auth
import org.json.JSONObject
import com.android.volley.Response as JsonResponse

class AuthenticatedJsonObjectRequest(
    method: Int,
    url: String,
    jsonRequest: JSONObject?,
    listener: JsonResponse.Listener<JSONObject>,
    errorListener: JsonResponse.ErrorListener?
) : JsonObjectRequest(method, url, jsonRequest, listener, errorListener) {
    override fun getHeaders(): MutableMap<String, String> = Auth.headers
}
