package api.model

import com.google.gson.annotations.SerializedName
import domain.SmartHomeDevice

data class Room(
    @SerializedName("name")
    val name: String,
    @SerializedName("devices")
    val devices: List<SmartHomeDevice>
)