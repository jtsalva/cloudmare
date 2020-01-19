package dev.jtsalva.cloudmare.api.pagerules

import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.getAdapter
import org.json.JSONObject

class PageRuleRequest(context: CloudMareActivity) : Request<PageRuleRequest>(context) {

    suspend fun list(zoneId: String,
                     order: String = ORDER_PRIORITY,
                     direction: String = DIRECTION_ASCENDING): PageRuleListResponse {
            val params = urlParams("order" to order, "direction" to direction)

            requestTAG = LIST
            return super.httpGet("zones/$zoneId/pagerules$params")
        }

    suspend fun create(zoneId: String, newPageRule: PageRule): PageRuleResponse {
            val payload = JSONObject(
                getAdapter(PageRule::class).toJson(newPageRule)
            )

            requestTAG = CREATE
            return super.httpPost("zones/$zoneId/pagerules", payload)
        }

    suspend fun update(zoneId: String, updatedPageRule: PageRule): PageRuleResponse {
            val payload = JSONObject(
                getAdapter(PageRule::class).toJson(updatedPageRule)
            )

            requestTAG = UPDATE
            return super.httpPut("zones/$zoneId/pagerules/${updatedPageRule.id}", payload)
        }

    suspend fun delete(zoneId: String, pageRuleId: String): PageRuleResponse {
            requestTAG = DELETE
            return super.httpDelete("zones/$zoneId/pagerules/$pageRuleId")
        }

}