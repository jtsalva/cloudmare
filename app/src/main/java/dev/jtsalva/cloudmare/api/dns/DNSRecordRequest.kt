package dev.jtsalva.cloudmare.api.dns

import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.getAdapter
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DNSRecordRequest(context: CloudMareActivity) : Request<DNSRecordRequest>(context) {

    suspend fun create(zoneId: String, newDNSRecord: DNSRecord) = suspendCoroutine<DNSRecordResponse> { cont ->
        val validKeys = setOf("type", "name", "content", "ttl", "priority", "proxied")
        val data = JSONObject(
            getAdapter(DNSRecord::class).toJson(newDNSRecord)
        )
        val payload = JSONObject()

        val keys = data.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            if (validKeys.contains(key)) payload.put(key, data[key])
        }

        requestTAG = CREATE
        post("zones/$zoneId/dns_records", payload) {
            cont.resume(
                getAdapter(DNSRecordResponse::class).
                        fromJson(it.toString()) ?: DNSRecordResponse(success = false)
            )
        }
    }

    suspend fun delete(zoneId: String, dnsRecordId: String) = suspendCoroutine<DNSRecordResponse> { cont ->
        requestTAG = DELETE
        delete("zones/$zoneId/dns_records/$dnsRecordId") {
            cont.resume(
                getAdapter(DNSRecordResponse::class).
                    fromJson(it.toString()) ?: DNSRecordResponse(success = false)
            )
        }
    }

    suspend fun get(zoneId: String, dnsRecordId: String) = suspendCoroutine<DNSRecordResponse> { cont ->
        requestTAG = GET
        get("zones/$zoneId/dns_records/$dnsRecordId") {
            cont.resume(
                getAdapter(DNSRecordResponse::class).
                    fromJson(it.toString()) ?: DNSRecordResponse(success = false)
            )
        }
    }


    suspend fun list(zoneId: String,
                     pageNumber: Int = 1,
                     perPage: Int = 20,
                     order: String = DNSRecord.SORT_BY_TYPE,
                     direction: String = DIRECTION_DESCENDING) =
        suspendCoroutine<DNSRecordListResponse> { cont ->
            val params = urlParams(
                "page" to pageNumber,
                "per_page" to perPage,
                "order" to order,
                "direction" to direction
            )

            requestTAG = LIST
            get("zones/$zoneId/dns_records$params") {
                cont.resume(
                    getAdapter(DNSRecordListResponse::class).
                        fromJson(it.toString()) ?: DNSRecordListResponse(success = false)
                )
            }
        }

    suspend fun update(zoneId: String, updatedDNSRecord: DNSRecord) = suspendCoroutine<DNSRecordResponse> { cont ->
        val payload = JSONObject(
            getAdapter(DNSRecord::class).toJson(updatedDNSRecord)
        )

        requestTAG = UPDATE
        put("zones/$zoneId/dns_records/${updatedDNSRecord.id}", payload) {
            cont.resume(
                getAdapter(DNSRecordResponse::class).
                    fromJson(it.toString()) ?: DNSRecordResponse(success = false)
            )
        }
    }

}