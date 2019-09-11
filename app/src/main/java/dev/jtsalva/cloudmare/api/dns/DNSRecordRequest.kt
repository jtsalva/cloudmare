package dev.jtsalva.cloudmare.api.dns

import android.content.Context
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.endpointUrl
import dev.jtsalva.cloudmare.api.getAdapter
import org.json.JSONObject
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DNSRecordRequest(context: Context) : Request(context, "zones") {

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

        requestTAG = Request.CREATE
        post(payload, endpointUrl(endpoint, zoneId, "dns_records")) {
            Timber.v(it.toString())

            cont.resume(
                getAdapter(DNSRecordResponse::class).
                        fromJson(it.toString()) ?: DNSRecordResponse(success = false)
            )
        }
    }

    suspend fun delete(zoneId: String, dnsRecordId: String) = suspendCoroutine<DNSRecordResponse> { cont ->
        requestTAG = Request.DELETE
        delete(null, endpointUrl(endpoint, zoneId, "dns_records", dnsRecordId)) {
            Timber.v(it.toString())

            cont.resume(
                getAdapter(DNSRecordResponse::class).
                    fromJson(it.toString()) ?: DNSRecordResponse(success = false)
            )
        }
    }

    suspend fun get(zoneId: String, dnsRecordId: String) = suspendCoroutine<DNSRecordResponse> { cont ->
        requestTAG = Request.GET
        get(null, endpointUrl(endpoint, zoneId, "dns_records", dnsRecordId)) {
            Timber.v(it.toString())

            cont.resume(
                getAdapter(DNSRecordResponse::class).fromJson(it.toString()) ?: DNSRecordResponse(success = false)
            )
        }
    }


    suspend fun list(zoneId: String, pageNumber: Int = 1, perPage: Int = 20) =
        suspendCoroutine<DNSRecordListResponse> { cont ->
            val params = urlParams("page" to pageNumber, "per_page" to perPage)
            val url = endpointUrl(endpoint, zoneId, "dns_records", params)

            requestTAG = Request.LIST
            get(null, url) {
                Timber.v(it.toString())

                cont.resume(
                    getAdapter(DNSRecordListResponse::class).fromJson(it.toString())
                        ?: DNSRecordListResponse(success = false)
                )
            }
        }

    suspend fun update(zoneId: String, updatedDNSRecord: DNSRecord) = suspendCoroutine<DNSRecordResponse> { cont ->
        val payload = JSONObject(
            getAdapter(DNSRecord::class).toJson(updatedDNSRecord)
        )

        requestTAG = Request.UPDATE
        put(payload, endpointUrl(endpoint, zoneId, "dns_records", updatedDNSRecord.id)) {
            Timber.v(it.toString())

            cont.resume(
                getAdapter(DNSRecordResponse::class).
                    fromJson(it.toString()) ?: DNSRecordResponse(success = false)
            )
        }
    }

}