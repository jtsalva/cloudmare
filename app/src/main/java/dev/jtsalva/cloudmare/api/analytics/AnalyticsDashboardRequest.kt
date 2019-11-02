package dev.jtsalva.cloudmare.api.analytics

import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.getAdapter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AnalyticsDashboardRequest(context: CloudMareActivity) : Request<AnalyticsDashboardRequest>(context) {

    companion object {
        private const val SEVEN_DAYS = 10080
    }

    suspend fun get(zoneId: String, since: Int = -SEVEN_DAYS) = suspendCoroutine<AnalyticsDashboardResponse> { cont ->
        val params = urlParams(
            "since" to since.toString()
        )

        requestTAG = GET
        get("zones/$zoneId/analytics/dashboard$params") {
            cont.resume(
                getAdapter(AnalyticsDashboardResponse::class).
                    fromJson(it.toString()) ?: AnalyticsDashboardResponse(success = false)
            )
        }
    }

}