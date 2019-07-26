package dev.jtsalva.cloudmare.api

import android.content.Context
import android.util.Log
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonRequest
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.json.JSONObject
import java.nio.charset.Charset
import com.android.volley.Request as VolleyRequest
import com.android.volley.Response as JsonResponse

open class Request(
    private val context: Context,
    protected val endpoint: String
) {

    init { TAG = "Request: $endpoint" }

    companion object {
        private lateinit var TAG: String
    }

    private fun handleError(error: VolleyError, callback: (response: JSONObject?) -> Unit) {
        Log.e(TAG, error.message ?: error.toString())

        val response = error.networkResponse
        if (response != null) {
            try {
                val res = String(
                    response.data,
                    Charset.forName(HttpHeaderParser.parseCharset(response.headers, "UTF-8"))
                )
                Log.d(TAG, "Res: $res")
                callback(JSONObject(res))
            } catch (e: Exception) {
                val failedResponse = getAdapter(Response::class.java).
                    toJson(
                        Response(success = false)
                    )

                callback(JSONObject(failedResponse))
                Log.e(TAG, "Something wen't wrong decoding error response")
                e.printStackTrace()
            }
        }
    }

    private fun <T> send(request: JsonRequest<T>) =
        RequestQueueSingleton.getInstance(context).addToRequestQueue(request)

    // TODO: move to companion object or util
    protected fun <T> getAdapter(type: Class<T>): JsonAdapter<T> =
        Moshi.Builder().build().adapter<T>(type)

    fun get(data: JSONObject?, url: String, callback: (response: JSONObject?) -> Unit) =
        send(
            AuthenticatedJsonRequest(
                VolleyRequest.Method.GET,
                url,
                data,
                JsonResponse.Listener(callback),
                JsonResponse.ErrorListener { error -> handleError(error, callback) }
            )
        )

    fun get(data: JSONObject?, callback: (response: JSONObject?) -> Unit) = get(data, endpointUrl(endpoint), callback)

    fun patch(data: JSONObject?, url: String, callback: (response: JSONObject?) -> Unit) =
        send(
            AuthenticatedJsonRequest(
                VolleyRequest.Method.PATCH,
                url,
                data,
                JsonResponse.Listener(callback),
                JsonResponse.ErrorListener { error -> handleError(error, callback) }
            )
        )

    fun patch(data: JSONObject?, callback: (response: JSONObject?) -> Unit) = patch(data, endpointUrl(endpoint), callback)

    fun put(data: JSONObject?, url: String, callback: (response: JSONObject?) -> Unit) =
        send(
            AuthenticatedJsonRequest(
                VolleyRequest.Method.PUT,
                url,
                data,
                JsonResponse.Listener(callback),
                JsonResponse.ErrorListener { error -> handleError(error, callback) }
            )
        )

    fun put(data: JSONObject?, callback: (response: JSONObject?) -> Unit) = patch(data, endpointUrl(endpoint), callback)

    fun post(data: JSONObject?, url: String, callback: (response: JSONObject?) -> Unit) =
        send(
            AuthenticatedJsonRequest(
                VolleyRequest.Method.POST,
                url,
                data,
                JsonResponse.Listener(callback),
                JsonResponse.ErrorListener { error -> handleError(error, callback) }
            )
        )

    fun post(data: JSONObject?, callback: (response: JSONObject?) -> Unit) = post(data, endpointUrl(endpoint), callback)

    fun delete(data: JSONObject?, url: String, callback: (response: JSONObject?) -> Unit) =
        send(
            AuthenticatedJsonRequest(
                VolleyRequest.Method.DELETE,
                url,
                data,
                JsonResponse.Listener(callback),
                JsonResponse.ErrorListener { error -> handleError(error, callback) }
            )
        )

    fun delete(data: JSONObject?, callback: (response: JSONObject?) -> Unit) = delete(data, endpointUrl(endpoint), callback)

}
