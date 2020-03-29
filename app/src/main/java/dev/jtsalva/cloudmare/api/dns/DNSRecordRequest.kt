package dev.jtsalva.cloudmare.api.dns

import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.Response
import dev.jtsalva.cloudmare.api.toJson
import org.json.JSONObject

class DNSRecordRequest(context: CloudMareActivity) : Request<DNSRecordRequest>(context) {

    suspend fun create(zoneId: String, newDNSRecord: DNSRecord): DNSRecordResponse {
        fun filterInvalidKeys(newDNSRecordData: JSONObject): JSONObject {
            val validKeys = setOf("type", "name", "content", "ttl", "priority", "proxied")
            val validData = JSONObject()

            val keys = newDNSRecordData.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                if (validKeys.contains(key)) validData.put(key, newDNSRecordData[key])
            }

            return validData
        }

        val payload = filterInvalidKeys(newDNSRecord.toJson())

        requestTAG = "create"
        return httpPost("zones/$zoneId/dns_records", payload)
    }

    suspend fun delete(zoneId: String, dnsRecordId: String): Response {
        requestTAG = "delete"
        return httpDelete("zones/$zoneId/dns_records/$dnsRecordId")
    }

    suspend fun get(zoneId: String, dnsRecordId: String): DNSRecordResponse {
        requestTAG = "get"
        return httpGet("zones/$zoneId/dns_records/$dnsRecordId")
    }


    suspend fun list(zoneId: String,
                     pageNumber: Int = 1,
                     perPage: Int = 20,
                     order: String = DNSRecord.SORT_BY_TYPE,
                     direction: String = DIRECTION_DESCENDING,
                     contains: String? = null): DNSRecordListResponse {
            var params = urlParams(
                "page" to pageNumber,
                "per_page" to perPage,
                "order" to order,
                "direction" to direction
            )

            if (contains != null && contains != "") {
                val matcher = "contains%3A$contains"
                params +=
                    "&${urlParams(
                        "name" to matcher,
                        "type" to matcher,
                        "content" to matcher,
                        "match" to "any"
                    ).substringAfter("?")}"
            }

            requestTAG = "list"
            return httpGet("zones/$zoneId/dns_records$params")
        }

    suspend fun update(zoneId: String, updatedDNSRecord: DNSRecord): DNSRecordResponse {
        val payload = updatedDNSRecord.toJson()

        requestTAG = "update"
        return httpPut("zones/$zoneId/dns_records/${updatedDNSRecord.id}", payload)
    }

}