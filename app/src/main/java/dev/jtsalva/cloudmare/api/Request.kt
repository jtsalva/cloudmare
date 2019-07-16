package dev.jtsalva.cloudmare.api

import android.content.Context
import android.util.Log
import com.android.volley.VolleyError
import com.android.volley.Request as VolleyRequest
import com.android.volley.toolbox.JsonRequest
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.json.JSONObject
import com.android.volley.Response as JsonResponse

open class Request(
    private val context: Context,
    protected val endpoint: String
) {

    init { TAG = "Request: $endpoint" }

    companion object {
        private lateinit var TAG: String
    }

    private fun handleError(error: VolleyError) = Log.e(TAG, error.message ?: error.toString())

    private fun <T> send(request: JsonRequest<T>) =
        RequestQueueSingleton.getInstance(context).addToRequestQueue(request)

    protected fun <T> getAdapter(type: Class<T>): JsonAdapter<T> =
        Moshi.Builder().build().adapter<T>(type)

    fun get(data: JSONObject?, url: String, callback: (response: JSONObject?) -> Unit) =
        send(
            AuthenticatedJsonRequest(
                VolleyRequest.Method.GET,
                url,
                data,
                JsonResponse.Listener(callback),
                JsonResponse.ErrorListener { error -> callback(null); handleError(error) }
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
                JsonResponse.ErrorListener { error -> callback(null); handleError(error) }
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
                JsonResponse.ErrorListener { error -> callback(null); handleError(error) }
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
                JsonResponse.ErrorListener { error -> callback(null); handleError(error) }
            )
        )

    fun post(data: JSONObject?, callback: (response: JSONObject?) -> Unit) = post(data, endpointUrl(endpoint), callback)

}
