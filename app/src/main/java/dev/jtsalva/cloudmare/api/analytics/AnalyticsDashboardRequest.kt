package dev.jtsalva.cloudmare.api.analytics

import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.api.Request

class AnalyticsDashboardRequest(context: CloudMareActivity) : Request<AnalyticsDashboardRequest>(context) {

    companion object {
        private const val SEVEN_DAYS = 10080
    }

    suspend fun get(zoneId: String, since: Int = -SEVEN_DAYS): AnalyticsDashboardResponse {
        val params = urlParams(
            "since" to since.toString()
        )

        requestTAG = "get"
        return super.httpGet("zones/$zoneId/analytics/dashboard$params")
    }


}