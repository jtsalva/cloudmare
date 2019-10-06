package dev.jtsalva.cloudmare.api.zonesettings

import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.getAdapter
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ZoneSettingRequest(context: CloudMareActivity) : Request<ZoneSettingRequest>(context) {

    suspend fun list(zoneId: String) = suspendCoroutine<ZoneSettingListResponse> { cont ->
        requestTAG = LIST
        get("zones/$zoneId/settings") {
            cont.resume(
                getAdapter(ZoneSettingListResponse::class).
                    fromJson(it.toString()) ?: ZoneSettingListResponse(success = false)
            )
        }
    }

    suspend fun update(zoneId: String, vararg zoneSettings: ZoneSetting) = suspendCoroutine<ZoneSettingListResponse> { cont ->
        val payload = getAdapter(ZoneSetting::class).run {
            val strings = mutableListOf<String>()
            zoneSettings.forEach { zoneSetting ->
                strings.add(toJson(zoneSetting))
            }

            JSONObject("{\"items\":[${strings.joinToString(",")}]}")
        }

        requestTAG = UPDATE
        patch("zones/$zoneId/settings", payload) {
            cont.resume(
                getAdapter(ZoneSettingListResponse::class).
                    fromJson(it.toString()) ?: ZoneSettingListResponse(success = false)
            )
        }
    }

}