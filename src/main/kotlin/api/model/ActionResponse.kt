package api.model

import com.google.gson.annotations.SerializedName

class ActionResponse(
    val result: Any?,
    val exitCode:Int?,
    val trans: String?,
    @SerializedName("out")
    val output: List<String>?,
)