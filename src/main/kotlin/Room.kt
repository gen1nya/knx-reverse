import com.google.gson.annotations.SerializedName

data class Room(
    @SerializedName("name")
    val name: String,
    @SerializedName("devices")
    val devices: List<SmartHomeDevice>
)