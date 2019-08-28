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

    override var requestTAG: String = javaClass.simpleName
        set(value) {
            field = "${javaClass.simpleName}.$value"
        }

    fun cancelAll(method: String) = cancelAll(javaClass.simpleName, method)

    suspend fun create(zoneId: String, newDNSRecord: DNSRecord) = suspendCoroutine<DNSRecordResponse> { cont ->
        cancelAll("create")

        val validKeys = setOf("type", "name", "content", "ttl", "priority", "proxied")
        val data = JSONObject(
            getAdapter(DNSRecord::class.java).toJson(newDNSRecord)
        )
        val payload = JSONObject()

        val keys = data.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            if (validKeys.contains(key)) payload.put(key, data[key])
        }

        requestTAG = "create"
        post(payload, endpointUrl(endpoint, zoneId, "dns_records")) {
            Timber.d(it.toString())

            cont.resume(
                getAdapter(DNSRecordResponse::class.java).
                        fromJson(it.toString()) ?: DNSRecordResponse(success = false)
            )
        }
    }

    suspend fun delete(zoneId: String, dnsRecordId: String) = suspendCoroutine<DNSRecordResponse> { cont ->
        cancelAll("delete")

        requestTAG = "delete"
        delete(null, endpointUrl(endpoint, zoneId, "dns_records", dnsRecordId)) {
            Timber.d(it.toString())

            cont.resume(
                getAdapter(DNSRecordResponse::class.java).
                    fromJson(it.toString()) ?: DNSRecordResponse(success = false)
            )
        }
    }

    suspend fun get(zoneId: String, dnsRecordId: String) = suspendCoroutine<DNSRecordResponse> { cont ->
        requestTAG = "get"
        get(null, endpointUrl(endpoint, zoneId, "dns_records", dnsRecordId)) {
            Timber.d(it.toString())

            cont.resume(
                getAdapter(DNSRecordResponse::class.java).fromJson(it.toString()) ?: DNSRecordResponse(success = false)
            )
        }
    }


    suspend fun list(zoneId: String) = suspendCoroutine<DNSRecordListResponse> { cont ->
        requestTAG = "list"
        get(null, endpointUrl(endpoint, zoneId, "dns_records")) {
            Timber.d(it.toString())

            cont.resume(
                getAdapter(DNSRecordListResponse::class.java).fromJson(it.toString())
                    ?: DNSRecordListResponse(success = false)
            )
        }
    }

    suspend fun update(zoneId: String, updatedDNSRecord: DNSRecord) = suspendCoroutine<DNSRecordResponse> { cont ->
        cancelAll("update")

        val payload = JSONObject(
            getAdapter(DNSRecord::class.java).toJson(updatedDNSRecord)
        )

        requestTAG = "update"
        put(payload, endpointUrl(endpoint, zoneId, "dns_records", updatedDNSRecord.id)) {
            Timber.d(it.toString())

            cont.resume(
                getAdapter(DNSRecordResponse::class.java).
                    fromJson(it.toString()) ?: DNSRecordResponse(success = false)
            )
        }
    }

}