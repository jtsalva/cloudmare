package dev.jtsalva.cloudmare.api.zonesettings

import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.api.Request
import org.json.JSONObject

class DevelopmentModeRequest(context: CloudMareActivity) : Request<DevelopmentModeRequest>(context) {

    suspend fun get(zoneId: String): ZoneSettingResponse {
        requestTAG = "get"
        return super.httpGet("zones/$zoneId/settings/development_mode")
    }

    suspend fun update(zoneId: String, value: String): ZoneSettingResponse {
        val payload = JSONObject().apply { put("value", value) }

        requestTAG = "update"
        return super.httpPatch("zones/$zoneId/settings/development_mode", payload)
    }

}