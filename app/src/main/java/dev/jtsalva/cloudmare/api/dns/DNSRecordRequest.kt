package dev.jtsalva.cloudmare.api.dns

import android.content.Context
import android.util.Log
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.endpointUrl
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DNSRecordRequest(context: Context) : Request(context, "zones") {

    companion object {
        private const val TAG = "DNSRecordRequest"
    }

    suspend fun create(zoneId: String, newDNSRecord: DNSRecord) = suspendCoroutine<DNSRecordResponse> { cont ->
        val data = getAdapter(DNSRecord::class.java).toJson(newDNSRecord)
        super.post(JSONObject(data), endpointUrl(endpoint, zoneId, "dns_records")) {
            Log.d(TAG, it.toString())

            cont.resume(
                getAdapter(DNSRecordResponse::class.java).
                        fromJson(it.toString()) ?: DNSRecordResponse(success = false)
            )
        }
    }

    suspend fun get(zoneId: String, dnsRecordId: String) = suspendCoroutine<DNSRecordResponse> { cont ->
        super.get(null, endpointUrl(endpoint, zoneId, "dns_records", dnsRecordId)) {
            Log.d(TAG, it.toString())

            cont.resume(
                getAdapter(DNSRecordResponse::class.java).fromJson(it.toString()) ?: DNSRecordResponse(success = false)
            )
        }
    }


    suspend fun list(zoneId: String) = suspendCoroutine<DNSRecordListResponse> { cont ->
        super.get(null, endpointUrl(endpoint, zoneId, "dns_records")) {
            Log.d(TAG, it.toString())

            cont.resume(
                getAdapter(DNSRecordListResponse::class.java).fromJson(it.toString())
                    ?: DNSRecordListResponse(success = false)
            )
        }
    }

    suspend fun update(zoneId: String, updatedDNSRecord: DNSRecord) = suspendCoroutine<DNSRecordResponse> { cont ->
        val data = getAdapter(DNSRecord::class.java).toJson(updatedDNSRecord)
        super.put(JSONObject(data), endpointUrl(endpoint, zoneId, "dns_records", updatedDNSRecord.id)) {
            Log.d(TAG, it.toString())

            cont.resume(
                getAdapter(DNSRecordResponse::class.java).
                    fromJson(it.toString()) ?: DNSRecordResponse(success = false)
            )
        }
    }

}