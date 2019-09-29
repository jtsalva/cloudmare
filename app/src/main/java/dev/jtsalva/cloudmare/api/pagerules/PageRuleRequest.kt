package dev.jtsalva.cloudmare.api.pagerules

import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.getAdapter
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PageRuleRequest(context: CloudMareActivity) : Request<PageRuleRequest>(context) {

//    fun launch(block: suspend PageRuleRequest.() -> Unit) = context.launch { this.block() }

    suspend fun list(zoneId: String,
                     order: String = ORDER_PRIORITY,
                     direction: String = DIRECTION_ASCENDING) =
        suspendCoroutine<PageRuleListResponse> { cont ->
            val params = urlParams("order" to order, "direction" to direction)

            requestTAG = LIST
            get("zones/$zoneId/pagerules$params") {
                cont.resume(
                    getAdapter(PageRuleListResponse::class).
                        fromJson(it.toString()) ?: PageRuleListResponse(success = false)
                )
            }
        }

    suspend fun create(zoneId: String, newPageRule: PageRule) =
        suspendCoroutine<PageRuleResponse> { cont ->
            val payload = JSONObject(
                getAdapter(PageRule::class).toJson(newPageRule)
            )

            requestTAG = CREATE
            post("zones/$zoneId/pagerules", payload) {
                cont.resume(
                    getAdapter(PageRuleResponse::class).
                        fromJson(it.toString()) ?: PageRuleResponse(success = false)
                )
            }
        }

    suspend fun update(zoneId: String, updatedPageRule: PageRule) =
        suspendCoroutine<PageRuleResponse> { cont ->
            val payload = JSONObject(
                getAdapter(PageRule::class).toJson(updatedPageRule)
            )

            requestTAG = UPDATE
            put("zones/$zoneId/pagerules/${updatedPageRule.id}", payload) {
                cont.resume(
                    getAdapter(PageRuleResponse::class).
                        fromJson(it.toString()) ?: PageRuleResponse(success = false)
                )
            }
        }

    suspend fun delete(zoneId: String, pageRuleId: String) =
        suspendCoroutine<PageRuleResponse> { cont ->
            requestTAG = DELETE
            delete("zones/$zoneId/pagerules/$pageRuleId") {
                cont.resume(
                    getAdapter(PageRuleResponse::class).
                        fromJson(it.toString()) ?: PageRuleResponse(success = false)
                )
            }
        }

}