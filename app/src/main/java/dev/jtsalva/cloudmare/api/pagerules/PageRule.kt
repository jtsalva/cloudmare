package dev.jtsalva.cloudmare.api.pagerules

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class PageRule(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "targets") val targets: List<Target>,
    @field:Json(name = "actions") val actions: List<Action>,
    @field:Json(name = "priority") var priority: Int,
    @field:Json(name = "status") var status: String,
    @field:Json(name = "modified_on") val modifiedOn: String,
    @field:Json(name = "created_on") val createdOn: String
) {

    companion object {
        const val ACTIVE = "active"
        const val DISABLED = "disabled"
    }

    @JsonClass(generateAdapter = true)
    data class Target(
        @field:Json(name = "target") var target: String,
        @field:Json(name = "constraint") var constraint: Constraint
    ) {

        @JsonClass(generateAdapter = true)
        data class Constraint(
            @field:Json(name = "operator") var operator: String,
            @field:Json(name = "value") var value: String
        )

    }

    @JsonClass(generateAdapter = true)
    data class Action(
        @field:Json(name = "id") val id: String,
        @field:Json(name = "value") var value: Any
    )

}