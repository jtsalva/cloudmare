package dev.jtsalva.cloudmare.api.analytics

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AnalyticsDashboard(
    @field:Json(name = "totals") val totals: DataSlice,
    @field:Json(name = "timeseries") val timeSeries: List<DataSlice>,
    @field:Json(name = "query") val query: Query? = null
) {

    @JsonClass(generateAdapter = true)
    data class DataSlice(
        @field:Json(name = "since") val since: String,
        @field:Json(name = "until") val until: String,
        @field:Json(name = "requests") val requests: Requests,
        @field:Json(name = "bandwidth") val bandwidth: Bandwidth,
        @field:Json(name = "threats") val threats: Threats,
        @field:Json(name = "pageviews") val pageviews: PageViews,
        @field:Json(name = "uniques") val uniques: Uniques
    ) {

        @JsonClass(generateAdapter = true)
        data class Requests(
            @field:Json(name = "all") val all: Int,
            @field:Json(name = "cached") val cached: Int,
            @field:Json(name = "uncached") val uncached: Int,
            @field:Json(name = "content_type") val contentType: Map<String, Int>? = null,
            @field:Json(name = "country") val country: Map<String, Int>? = null,
            @field:Json(name = "ssl") val ssl: Map<String, Int>? = null,
            @field:Json(name = "ssl_protocols") val sslProtocols: Map<String, Int>? = null,
            @field:Json(name = "http_status") val httpStatus: Map<String, Int>? = null,
            @field:Json(name = "threats") val threats: Threats? = null
        )

        @JsonClass(generateAdapter = true)
        data class Bandwidth(
            @field:Json(name = "all") val all: Int,
            @field:Json(name = "cached") val cached: Int,
            @field:Json(name = "uncached") val uncached: Int,
            @field:Json(name = "content_type") val contentType: Map<String, Int>? = null,
            @field:Json(name = "country") val country: Map<String, Int>? = null,
            @field:Json(name = "ssl") val ssl: Map<String, Int>? = null,
            @field:Json(name = "ssl_protocols") val sslProtocols: Map<String, Int>? = null,
            @field:Json(name = "pageviews") val pageViews: PageViews? = null,
            @field:Json(name = "uniques") val uniques: Uniques? = null
        )

        @JsonClass(generateAdapter = true)
        data class Threats(
            @field:Json(name = "all") val all: Int,
            @field:Json(name = "country") val country: Map<String, Int>? = null,
            @field:Json(name = "type") val type: Map<String, Int>? = null
        )

        @JsonClass(generateAdapter = true)
        data class PageViews(
            @field:Json(name = "all") val all: Int,
            @field:Json(name = "search_engine") val searchEngine: Map<String, Int>? = null
        )

        @JsonClass(generateAdapter = true)
        data class Uniques(
            @field:Json(name = "all") val all: Int
        )
    }

    @JsonClass(generateAdapter = true)
    data class Query(
        @field:Json(name = "since") val since: String,
        @field:Json(name = "until") val until: String,
        @field:Json(name = "time_delta") val timeDelta: Int
    )

}