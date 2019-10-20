package dev.jtsalva.cloudmare.api.analytics

import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.getAdapter
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AnalyticsDashboardRequest(context: CloudMareActivity) : Request<AnalyticsDashboardRequest>(context) {

    suspend fun get(zoneId: String, since: Int = -10080) = suspendCoroutine<AnalyticsDashboardResponse> { cont ->
        val params = urlParams(
            "since" to since.toString()
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