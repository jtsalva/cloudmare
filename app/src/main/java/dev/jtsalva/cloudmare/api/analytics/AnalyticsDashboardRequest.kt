package dev.jtsalva.cloudmare.api.analytics

import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.getAdapter
import dev.jtsalva.cloudmare.api.toDateString
import timber.log.Timber
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AnalyticsDashboardRequest(context: CloudMareActivity) : Request<AnalyticsDashboardRequest>(context) {

    suspend fun get(zoneId: String) = suspendCoroutine<AnalyticsDashboardResponse> { cont ->
        val params = urlParams(
//            "since" to Date(Calendar.getInstance().time.time - 604800000L).toDateString(),
//            "until" to Calendar.getInstance().time.toDateString(),
//            "continuous" to "false"
        )

        Timber.e(params)

        requestTAG = GET
        get("zones/$zoneId/analytics/dashboard$params") {
            cont.resume(
                getAdapter(AnalyticsDashboardResponse::class).
                    fromJson(it.toString()) ?: AnalyticsDashboardResponse(success = false)
            )
        }
    }

}