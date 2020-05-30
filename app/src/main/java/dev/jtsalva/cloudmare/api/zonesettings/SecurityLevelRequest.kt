package dev.jtsalva.cloudmare.api.zonesettings

import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.api.Request
import org.json.JSONObject

class SecurityLevelRequest(context: CloudMareActivity) : Request<SecurityLevelRequest>(context) {

    suspend fun get(zoneId: String): ZoneSettingResponse {
        return httpGet("zones/$zoneId/settings/security_level")
    }

    suspend fun update(zoneId: String, value: String): ZoneSettingResponse {
        val payload = JSONObject().apply {
            put("value", value)
        }

        return httpPatch("zones/$zoneId/settings/security_level", payload)
    }
}
