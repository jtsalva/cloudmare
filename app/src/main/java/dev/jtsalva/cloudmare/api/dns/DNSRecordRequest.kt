package dev.jtsalva.cloudmare.api.dns

import android.content.Context
import android.util.Log
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.endpointUrl
import dev.jtsalva.cloudmare.api.getAdapter
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DNSRecordRequest(context: Context) : Request(context, "zones") {

    companion object {
        private const val TAG = "DNSRecordRequest"
    }

    suspend fun create(zoneId: String, newDNSRecord: DNSRecord) = suspendCoroutine<DNSRecordResponse> { cont ->
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

        post(payload, endpointUrl(endpoint, zoneId, "dns_records")) {
            Log.d(TAG, it.toString())

            cont.resume(
                getAdapter(DNSRecordResponse::class.java).
                        fromJson(it.toString()) ?: DNSRecordResponse(success = false)
            )
        }
    }

    suspend fun delete(zoneId: String, dnsRecordId: String) = suspendCoroutine<DNSRecordResponse> { cont ->
        delete(null, endpointUrl(endpoint, zoneId, "dns_records", dnsRecordId)) {
            Log.d(TAG, it.toString())

            cont.resume(
                getAdapter(DNSRecordResponse::class.java).
                    fromJson(it.toString()) ?: DNSRecordResponse(success = false)
            )
        }
    }

    suspend fun get(zoneId: String, dnsRecordId: String) = suspendCoroutine<DNSRecordResponse> { cont ->
        get(null, endpointUrl(endpoint, zoneId, "dns_records", dnsRecordId)) {
            Log.d(TAG, it.toString())

            cont.resume(
                getAdapter(DNSRecordResponse::class.java).fromJson(it.toString()) ?: DNSRecordResponse(success = false)
            )
        }
    }


    suspend fun list(zoneId: String) = suspendCoroutine<DNSRecordListResponse> { cont ->
        get(null, endpointUrl(endpoint, zoneId, "dns_records")) {
            Log.d(TAG, it.toString())

            cont.resume(
                getAdapter(DNSRecordListResponse::class.java).fromJson(it.toString())
                    ?: DNSRecordListResponse(success = false)
            )
        }
    }

    suspend fun update(zoneId: String, updatedDNSRecord: DNSRecord) = suspendCoroutine<DNSRecordResponse> { cont ->
        val payload = JSONObject(
            getAdapter(DNSRecord::class.java).toJson(updatedDNSRecord)
        )
        put(payload, endpointUrl(endpoint, zoneId, "dns_records", updatedDNSRecord.id)) {
            Log.d(TAG, it.toString())

            cont.resume(
                getAdapter(DNSRecordResponse::class.java).
                    fromJson(it.toString()) ?: DNSRecordResponse(success = false)
            )
        }
    }

}