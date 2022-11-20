package data

import com.google.gson.annotations.SerializedName

open class BaseRequest(
    @SerializedName("cmd")
    val command: String = ""
) {
    @SerializedName("trans")
    var transId: String = "0"

}