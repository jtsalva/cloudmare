package dev.jtsalva.cloudmare.api.dns

import android.content.Context
import android.util.Log
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.ResponseListener
import dev.jtsalva.cloudmare.api.endpointUrl
import org.json.JSONObject

class DNSRecordRequest(context: Context) : Request(context, "zones") {

    companion object {
        private const val TAG = "DNSRecordRequest"
    }

    fun create(zoneId: String, newDNSRecord: DNSRecord, callback: ResponseListener<DNSRecordListResponse>) {
        val data = getAdapter(DNSRecord::class.java).toJson(newDNSRecord)
        super.post(JSONObject(data), endpointUrl(endpoint, zoneId, "dns_records")) {
            Log.d(TAG, it.toString())

            callback(
                getAdapter(DNSRecordListResponse::class.java).
                        fromJson(it.toString()) ?: DNSRecordListResponse(success = false)
            )
        }
    }

    fun get(zoneId: String, dnsRecordId: String, callback: ResponseListener<DNSRecordResponse>) =
        super.get(null, endpointUrl(endpoint, zoneId, "dns_records", dnsRecordId)) {
            Log.d(TAG, it.toString())

            callback(
                getAdapter(DNSRecordResponse::class.java).
                    fromJson(it.toString()) ?: DNSRecordResponse(success = false)
            )
        }


    fun list(zoneId: String, callback: ResponseListener<DNSRecordListResponse>) =
        super.get(null, endpointUrl(endpoint, zoneId, "dns_records")) {
            Log.d(TAG, it.toString())

            callback(
                getAdapter(DNSRecordListResponse::class.java).
                    fromJson(it.toString()) ?: DNSRecordListResponse(success = false)
            )
        }

    fun update(zoneId: String, updatedDNSRecord: DNSRecord, callback: ResponseListener<DNSRecordResponse>) {
        val data = getAdapter(DNSRecord::class.java).toJson(updatedDNSRecord)
        super.put(JSONObject(data), endpointUrl(endpoint, zoneId, "dns_records", updatedDNSRecord.id)) {
            Log.d(TAG, it.toString())

            callback(
                getAdapter(DNSRecordResponse::class.java).
                    fromJson(it.toString()) ?: DNSRecordResponse(success = false)
            )
        }
    }

}