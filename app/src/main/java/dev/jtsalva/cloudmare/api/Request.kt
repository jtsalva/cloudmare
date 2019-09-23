package dev.jtsalva.cloudmare.api

import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import dev.jtsalva.cloudmare.CloudMareActivity
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.nio.charset.Charset
import com.android.volley.Request as VolleyRequest
import com.android.volley.Response as JsonResponse

typealias ResponseCallback = (response: JSONObject?) -> Unit

open class Request(protected val context: CloudMareActivity) {

    companion object {
        const val CREATE = "create"
        const val GET = "get"
        const val LIST = "list"
        const val UPDATE = "update"
        const val DELETE = "delete"

        const val DIRECTION_ASCENDING = "asc"
        const val DIRECTION_DESCENDING = "desc"

        const val ORDER_STATUS = "status"
        const val ORDER_PRIORITY = "priority"
    }

    private fun ResponseCallback.logResponse(): ResponseCallback = { response ->
        Timber.v(response.toString())
        invoke(response)
    }

    private val className = javaClass.simpleName

    protected var requestTAG: String = className
        set(method) {
            if (!(method == GET || method == LIST))
                cancelAll(method)

            field = "$className.$method"
        }

    protected fun urlParams(vararg params: Pair<String, Any>): String = params.run {
        var urlParamsString = "?"
        forEach { urlParamsString += "${it.first}=${it.second}&" }
        return urlParamsString.substring(0, urlParamsString.length - 1)
    }

    fun cancelAll(method: String) =
        RequestQueueSingleton(context).requestQueue.cancelAll("$className.$method")

    private fun handleError(error: VolleyError, callback: ResponseCallback) {
        Timber.e(error.message ?: error.toString())

        val response = error.networkResponse
        if (response != null) {
            try {
                val res = String(
                    response.data,
                    Charset.forName(HttpHeaderParser.parseCharset(response.headers, "UTF-8"))
                )
                Timber.v("Response: $res")
                callback(JSONObject(res))
            } catch (e: Throwable) {
                val failedResponse = Response.createWithErrors(
                    Response.Error(
                        code = 0,
                        message = "Something wen't wrong decoding error response"
                    )
                )

                callback(JSONObject(failedResponse))
                Timber.e(e)
            }
        } else {
            Timber.e(error.localizedMessage ?: "null error response")

            val failedResponse = Response.createWithErrors(
                Response.Error(
                    code = 0,
                    message = "Make sure you're connected to the internet"
                )
            )

            callback(JSONObject(failedResponse))
        }
    }

    private fun send(method: Int, data: Any?, path: String, callback: ResponseCallback) {
        val url = "$BASE_URL/$path"

        RequestQueueSingleton.getInstance(context).addToRequestQueue(
            when (data) {
                null, is JSONObject -> AuthenticatedJsonObjectRequest(
                    method,
                    url,
                    data as? JSONObject,
                    JsonResponse.Listener(callback.logResponse()),
                    JsonResponse.ErrorListener { error -> handleError(error, callback.logResponse()) }
                )

                is JSONArray -> AuthenticatedJsonArrayRequest(
                    method,
                    url,
                    data,
                    JsonResponse.Listener(callback.logResponse()),
                    JsonResponse.ErrorListener { error -> handleError(error, callback.logResponse()) }
                )

                else -> throw Exception("invalid request data type must be either JSONObject or JSONArray")

            }.apply { setTag(requestTAG) }
        )
    }

    fun get(path: String, data: Any? = null, callback: ResponseCallback) =
        send(
            VolleyRequest.Method.GET,
            data,
            path,
            callback
        )

    fun patch(path: String, data: Any? = null, callback: ResponseCallback) =
        send(
            VolleyRequest.Method.PATCH,
            data,
            path,
            callback
        )

    fun put(path: String, data: Any? = null, callback: ResponseCallback) =
        send(
            VolleyRequest.Method.PATCH,
            data,
            path,
            callback
        )

    fun post(path: String, data: Any? = null, callback: ResponseCallback) =
        send(
            VolleyRequest.Method.POST,
            data,
            path,
            callback
        )

    fun delete(path: String, data: Any? = null, callback: ResponseCallback) =
        send(
            VolleyRequest.Method.DELETE,
            data,
            path,
            callback
        )

}