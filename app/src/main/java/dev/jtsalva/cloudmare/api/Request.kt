package dev.jtsalva.cloudmare.api

import android.content.Context
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.nio.charset.Charset
import com.android.volley.Request as VolleyRequest
import com.android.volley.Response as JsonResponse

open class Request(
    private val context: Context,
    protected val endpoint: String
) {

    protected open var requestTAG: String = "*"

    protected fun cancelAll(tag: String, method: String) =
        RequestQueueSingleton(context).requestQueue.cancelAll("$tag.$method")

    private fun handleError(error: VolleyError, callback: (response: JSONObject?) -> Unit) {
        Timber.e(error.message ?: error.toString())

        val response = error.networkResponse
        if (response != null) {
            try {
                val res = String(
                    response.data,
                    Charset.forName(HttpHeaderParser.parseCharset(response.headers, "UTF-8"))
                )
                Timber.d("Res: $res")
                callback(JSONObject(res))
            } catch (e: Throwable) {
                val failedResponse = Response.createWithErrors(
                    Response.Error(
                        code = 0,
                        message = "Something wen't wrong decoding error response"
                    )
                )

                callback(JSONObject(failedResponse))
                Timber.e("Something wen't wrong decoding error response")
                e.printStackTrace()
            }
        } else {
            Timber.e(error.localizedMessage ?: "null error response")

            val failedResponse = Response.createWithErrors(
                Response.Error(
                    code = 0,
                    message = "Are you connected to the internet?"
                )
            )

            callback(JSONObject(failedResponse))
        }
    }

    private fun <T> send(method: Int, data: T?, url: String, callback: (response: JSONObject?) -> Unit) =
        RequestQueueSingleton.getInstance(context).addToRequestQueue(
            when (data) {
                null -> AuthenticatedJsonObjectRequest(
                    method,
                    url,
                    null,
                    JsonResponse.Listener(callback),
                    JsonResponse.ErrorListener { error -> handleError(error, callback) }
                )

                is JSONObject -> AuthenticatedJsonObjectRequest(
                    method,
                    url,
                    data,
                    JsonResponse.Listener(callback),
                    JsonResponse.ErrorListener { error -> handleError(error, callback) }
                )

                is JSONArray -> AuthenticatedJsonArrayRequest(
                    method,
                    url,
                    data,
                    JsonResponse.Listener(callback),
                    JsonResponse.ErrorListener { error -> handleError(error, callback) }
                )

                else -> throw Exception("invalid request data type")

            }
        )

    fun <T> get(data: T?, url: String, callback: (response: JSONObject?) -> Unit) =
        send<T>(
            VolleyRequest.Method.GET,
            data,
            url,
            callback
        )

    fun <T> get(data: T?, callback: (response: JSONObject?) -> Unit) = get(data, endpointUrl(endpoint), callback)

    fun <T> patch(data: T?, url: String, callback: (response: JSONObject?) -> Unit) =
        send<T>(
            VolleyRequest.Method.PATCH,
            data,
            url,
            callback
        )

    fun <T> patch(data: T?, callback: (response: JSONObject?) -> Unit) = patch<T>(data, endpointUrl(endpoint), callback)

    fun <T> put(data: T?, url: String, callback: (response: JSONObject?) -> Unit) =
        send<T>(
            VolleyRequest.Method.PATCH,
            data,
            url,
            callback
        )

    fun <T> put(data: T?, callback: (response: JSONObject?) -> Unit) = patch<T>(data, endpointUrl(endpoint), callback)

    fun <T> post(data: T?, url: String, callback: (response: JSONObject?) -> Unit) =
        send<T>(
            VolleyRequest.Method.POST,
            data,
            url,
            callback
        )

    fun <T> post(data: T?, callback: (response: JSONObject?) -> Unit) = post<T>(data, endpointUrl(endpoint), callback)

    fun <T> delete(data: T?, url: String, callback: (response: JSONObject?) -> Unit) =
        send<T>(
            VolleyRequest.Method.DELETE,
            data,
            url,
            callback
        )

    fun <T> delete(data: T?, callback: (response: JSONObject?) -> Unit) = delete<T>(data, endpointUrl(endpoint), callback)

}