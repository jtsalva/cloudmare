package dev.jtsalva.cloudmare.api.analytics

import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.getAdapter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AnalyticsDashboardRequest(context: CloudMareActivity) : Request<AnalyticsDashboardRequest>(context) {

    suspend fun get(zoneId: String) = suspendCoroutine<AnalyticsDashboardResponse> { cont ->
        requestTAG = GET
        get("zones/$zoneId/analytics/dashboard") {
            cont.resume(
                getAdapter(AnalyticsDashboardResponse::class).
                    fromJson(it.toString()) ?: AnalyticsDashboardResponse(success = false)
            )
        }
    }

}