package dev.jtsalva.cloudmare.api.pagerules

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class PageRule(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "targets") val targets: List<Target>,
    @field:Json(name = "actions") val actions: List<Action>,
    @field:Json(name = "priority") val priority: Int,
    @field:Json(name = "status") val status: String,
    @field:Json(name = "modified_on") val modifiedOn: String,
    @field:Json(name = "created_on") val createdOn: String
) {

    @JsonClass(generateAdapter = true)
    data class Target(
        @field:Json(name = "target") val target: String,
        @field:Json(name = "constraint") val constraint: Constraint
    ) {

        @JsonClass(generateAdapter = true)
        data class Constraint(
            @field:Json(name = "operator") val operator: String,
            @field:Json(name = "value") val value: String
        )

    }

    @JsonClass(generateAdapter = true)
    data class Action(
        @field:Json(name = "id") val id: String,
        @field:Json(name = "value") val value: Any
    )

}