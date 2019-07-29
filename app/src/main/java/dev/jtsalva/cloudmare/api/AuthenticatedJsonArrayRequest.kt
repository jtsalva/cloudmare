package dev.jtsalva.cloudmare.api

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonRequest
import dev.jtsalva.cloudmare.Auth
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import com.android.volley.Response as JsonResponse

class AuthenticatedJsonArrayRequest(
    method: Int,
    url: String,
    jsonRequest: JSONArray?,
    listener: JsonResponse.Listener<JSONObject>,
    errorListener: JsonResponse.ErrorListener?
) : JsonRequest<JSONObject>(method, url, jsonRequest?.toString() ?: "", listener, errorListener) {

    override fun getHeaders(): MutableMap<String, String> = Auth.headers

    override fun parseNetworkResponse(response: NetworkResponse): com.android.volley.Response<JSONObject> {
        try {
            val jsonString = String(
                response.data,
                Charset.forName(HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET))
            )
            return com.android.volley.Response.success(
                JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response)
            )
        } catch (e: UnsupportedEncodingException) {
            return com.android.volley.Response.error(ParseError(e))
        } catch (je: JSONException) {
            return com.android.volley.Response.error(ParseError(je))
        }

    }

}