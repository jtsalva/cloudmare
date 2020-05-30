package dev.jtsalva.cloudmare.api.pagerules

import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.toJson

class PageRuleRequest(context: CloudMareActivity) : Request<PageRuleRequest>(context) {

    suspend fun list(
        zoneId: String,
        order: String = ORDER_PRIORITY,
        direction: String = DIRECTION_ASCENDING
    ): PageRuleListResponse {
        val params = urlParams("order" to order, "direction" to direction)

        return httpGet("zones/$zoneId/pagerules$params")
    }

    suspend fun create(zoneId: String, newPageRule: PageRule): PageRuleResponse {
        val payload = newPageRule.toJson()

        return httpPost("zones/$zoneId/pagerules", payload)
    }

    suspend fun update(zoneId: String, updatedPageRule: PageRule): PageRuleResponse {
        val payload = updatedPageRule.toJson()

        return httpPut("zones/$zoneId/pagerules/${updatedPageRule.id}", payload)
    }

    suspend fun delete(zoneId: String, pageRuleId: String): PageRuleResponse {
        return httpDelete("zones/$zoneId/pagerules/$pageRuleId")
    }
}
