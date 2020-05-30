package dev.jtsalva.cloudmare.api

import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import dev.jtsalva.cloudmare.CloudMareActivity
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.nio.charset.Charset
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.android.volley.Request as VolleyRequest
import com.android.volley.Response as JsonResponse

typealias ResponseCallback = (response: JSONObject?) -> Unit

@Suppress("UNUSED")
open class Request<R : Request<R>>(protected val activity: CloudMareActivity) {

    companion object {
        const val DIRECTION_ASCENDING = "asc"
        const val DIRECTION_DESCENDING = "desc"

        const val ORDER_STATUS = "status"
        const val ORDER_PRIORITY = "priority"

        private const val CHARSET = "UTF-8"
        private const val LOCAL_ERROR_CODE = -1

        fun cancelAll(activity: CloudMareActivity) {
            RequestQueueSingleton(activity).requestQueue.cancelAll(activity)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun launch(
        context: CoroutineContext = activity.coroutineContext,
        block: suspend R.() -> Unit
    ) = activity.launch(context) { block.invoke(this as R) }

    protected inline fun urlParams(vararg params: Pair<String, Any>): String = params.run {
        val urlParamsString = StringBuilder("?")
        forEach { urlParamsString.append("${it.first}=${it.second}&") }
        return urlParamsString.dropLast(1).toString()
    }

    private inline fun handleError(error: VolleyError, callback: ResponseCallback) {
        Timber.e(error.message ?: error.toString())

        val response = error.networkResponse
        var callbackResponse: JSONObject
        if (response != null) {
            try {
                callbackResponse = String(
                    response.data,
                    Charset.forName(HttpHeaderParser.parseCharset(response.headers, CHARSET))
                ).toJson()
                Timber.e("Error Response: $callbackResponse")
            } catch (e: Throwable) {
                callbackResponse = Response.createWithErrors(
                    Response.Error(
                        code = LOCAL_ERROR_CODE,
                        message = "Something went wrong decoding error response"
                    )
                ).toJson()
                Timber.e(e)
            }
        } else {
            callbackResponse = Response.createWithErrors(
                Response.Error(
                    code = LOCAL_ERROR_CODE,
                    message = "Make sure you're connected to the internet"
                )
            ).toJson()
            Timber.e(error.localizedMessage ?: "null error response")
        }

        callback(callbackResponse)
    }

    private suspend inline fun <reified T : Response> send(method: Int, path: String, data: Any?) =
        suspendCoroutine<T> { cont ->
            val url = "$BASE_URL/$path"

            val callback: ResponseCallback = { response ->
                Timber.v(response.toString())
                cont.resume(response.fromJson())
            }

            RequestQueueSingleton.getInstance(activity).addToRequestQueue(
                when (data) {
                    is JSONObject, null -> AuthenticatedJsonObjectRequest(
                        method,
                        url,
                        data as? JSONObject,
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

                    else -> throw Exception("invalid request data type must be either JSONObject or JSONArray")
                }.apply { setTag(activity) }
            )
        }

    internal suspend inline fun <reified T : Response> httpGet(path: String, data: Any? = null) =
        send<T>(VolleyRequest.Method.GET, path, data)

    internal suspend inline fun <reified T : Response> httpPatch(path: String, data: Any? = null) =
        send<T>(VolleyRequest.Method.PATCH, path, data)

    internal suspend inline fun <reified T : Response> httpPut(path: String, data: Any? = null) =
        send<T>(VolleyRequest.Method.PUT, path, data)

    internal suspend inline fun <reified T : Response> httpPost(path: String, data: Any? = null) =
        send<T>(VolleyRequest.Method.POST, path, data)

    internal suspend inline fun <reified T : Response> httpDelete(path: String, data: Any? = null) =
        send<T>(VolleyRequest.Method.DELETE, path, data)
}
