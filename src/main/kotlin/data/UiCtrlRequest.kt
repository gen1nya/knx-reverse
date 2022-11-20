package data

import com.google.gson.annotations.SerializedName

class UiCtrlRequest(
    @SerializedName("action")
    val action: String = "onClick",
    @SerializedName("id")
    val id: Int,
    @SerializedName("arg1")
    val arg1: Any,
    @SerializedName("arg2")
    val arg2: Int,
): BaseRequest(command = "uictrl")

