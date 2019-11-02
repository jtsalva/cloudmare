package dev.jtsalva.cloudmare.api.analytics

import com.squareup.moshi.JsonClass
import dev.jtsalva.cloudmare.api.Response

@JsonClass(generateAdapter = true)
class AnalyticsDashboardResponse(
    success: Boolean,
    errors: List<Error> = emptyList(),
    messages: List<Message> = emptyList(),
    override val result: AnalyticsDashboard? = null
) : Response(success, errors, messages)