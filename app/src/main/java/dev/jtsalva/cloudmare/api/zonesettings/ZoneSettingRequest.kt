package dev.jtsalva.cloudmare.api.zonesettings

import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.getAdapter
import org.json.JSONObject

class ZoneSettingRequest(context: CloudMareActivity) : Request<ZoneSettingRequest>(context) {

    suspend fun list(zoneId: String): ZoneSettingListResponse {
        requestTAG = LIST
        return super.httpGet("zones/$zoneId/settings")
    }

    suspend fun update(zoneId: String, vararg zoneSettings: ZoneSetting): ZoneSettingListResponse {
        val payload = getAdapter(ZoneSetting::class).run {
            val strings = mutableListOf<String>()
            zoneSettings.forEach { zoneSetting ->
                strings.add(toJson(zoneSetting))
            }

            JSONObject("{\"items\":[${strings.joinToString(",")}]}")
        }

        requestTAG = UPDATE
        return super.httpPatch("zones/$zoneId/settings", payload)
    }

}