package dev.jtsalva.cloudmare.api.pagerules

import android.content.Context
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.endpointUrl
import dev.jtsalva.cloudmare.api.getAdapter
import org.json.JSONObject
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PageRuleRequest(context: Context) : Request(context, "zones") {

    suspend fun list(zoneId: String) =
        suspendCoroutine<PageRuleListResponse> { cont ->
            val url = endpointUrl(endpoint, zoneId, "pagerules")

            requestTAG = Request.LIST
            get(null, url) {
                Timber.v(it.toString())

                cont.resume(
                    getAdapter(PageRuleListResponse::class).fromJson(it.toString())
                        ?: PageRuleListResponse(success = false)
                )
            }
        }

    suspend fun create(zoneId: String, newPageRule: PageRule) =
        suspendCoroutine<PageRuleResponse> { cont ->
            val data = JSONObject(
                getAdapter(PageRule::class).toJson(newPageRule)
            )

            requestTAG = Request.CREATE
            post(data, endpointUrl(endpoint, zoneId, "pagerules")) {
                Timber.v(it.toString())

                cont.resume(
                    getAdapter(PageRuleResponse::class).fromJson(it.toString()) ?:
                    PageRuleResponse(success = false)
                )
            }
        }

    suspend fun update(zoneId: String, updatedPageRule: PageRule) =
        suspendCoroutine<PageRuleResponse> { cont ->
            val data = JSONObject(
                getAdapter(PageRule::class).toJson(updatedPageRule)
            )

            requestTAG = Request.UPDATE
            put(data, endpointUrl(endpoint, zoneId, "pagerules", updatedPageRule.id)) {
                Timber.v(it.toString())

                cont.resume(
                    getAdapter(PageRuleResponse::class).fromJson(it.toString()) ?:
                    PageRuleResponse(success = false)
                )
            }
        }

    suspend fun delete(zoneId: String, pageRuleId: String) =
        suspendCoroutine<PageRuleResponse> { cont ->
            requestTAG = Request.DELETE
            delete(null, endpointUrl(endpoint, zoneId, "pagerules", pageRuleId)) {
                Timber.v(it.toString())

                cont.resume(
                    getAdapter(PageRuleResponse::class).fromJson(it.toString()) ?:
                    PageRuleResponse(success = false)
                )
            }
        }

}