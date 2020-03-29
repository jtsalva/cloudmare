package dev.jtsalva.cloudmare.api.pagerules

import com.squareup.moshi.JsonClass
import dev.jtsalva.cloudmare.api.Response

@JsonClass(generateAdapter = true)
class PageRuleResponse(
    success: Boolean,
    errors: List<Error> = emptyList(),
    messages: List<Message> = emptyList(),
    override val result: PageRule? = null
) : Response(success, errors, messages)
