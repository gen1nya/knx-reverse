package domain

open class SmartHomeDevice(
    val id: Int,
    val type: SmartHomeDeviceType,
    val name: String,
    val page: Rooms,
    val kind: DeviceKind = DeviceKind.LIGHT,
    var value: Double = 0.0
)