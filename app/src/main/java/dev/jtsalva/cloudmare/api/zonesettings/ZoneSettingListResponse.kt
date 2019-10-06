package dev.jtsalva.cloudmare.api.zonesettings

import com.squareup.moshi.JsonClass
import dev.jtsalva.cloudmare.api.Response

@JsonClass(generateAdapter = true)
class ZoneSettingListResponse(
    success: Boolean,
    errors: List<Error> = emptyList(),
    messages: List<String> = emptyList(),
    override val result: List<ZoneSetting>? = null
) : Response(success, errors, messages)