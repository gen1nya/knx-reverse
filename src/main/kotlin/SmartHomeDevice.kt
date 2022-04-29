open class SmartHomeDevice(
    val id: Int,
    val type: SmartHomeDeviceType,
    val name: String,
    val page: Rooms,
    val kind: DeviceKind = DeviceKind.LIGHT,
    var value: Double = 0.0
)

enum class SmartHomeDeviceType(val id: Int) {
    SWITCH(0), ANALOG(1), ANALOG_WITHOUT_DIMMING(2)
}

enum class Rooms {
    MASTER_BEDROOM, LOUNGE;
}

enum class DeviceKind {
    LIGHT, FAN, SHUTTER;
}

object WardrobeSpot: SmartHomeDevice(-609731, SmartHomeDeviceType.SWITCH, "Wardrobe spot", Rooms.MASTER_BEDROOM)
object DeskSpot: SmartHomeDevice(-609730, SmartHomeDeviceType.SWITCH, "Desk spot", Rooms.MASTER_BEDROOM)
object Bathroom: SmartHomeDevice(-609864, SmartHomeDeviceType.SWITCH, "Bathroom", Rooms.MASTER_BEDROOM)
object Hidden: SmartHomeDevice(-609724, SmartHomeDeviceType.ANALOG, "Hidden", Rooms.MASTER_BEDROOM)
object Shutter: SmartHomeDevice(-609736, SmartHomeDeviceType.ANALOG, "Shutter", Rooms.MASTER_BEDROOM, DeviceKind.SHUTTER)

object Kitchen: SmartHomeDevice(-609936, SmartHomeDeviceType.SWITCH, "Kitchen", Rooms.LOUNGE)
object ExFan: SmartHomeDevice(-609907, SmartHomeDeviceType.SWITCH, "Kitchen ExFan", Rooms.LOUNGE, DeviceKind.FAN)
object Undercubort: SmartHomeDevice(-609908, SmartHomeDeviceType.SWITCH, "Undercubot", Rooms.LOUNGE)
object Balcony1: SmartHomeDevice(-609937, SmartHomeDeviceType.SWITCH, "Balcony", Rooms.LOUNGE)
object Balcony2: SmartHomeDevice(-609917, SmartHomeDeviceType.SWITCH, "Balcony", Rooms.LOUNGE)
object KitchenBalcony2: SmartHomeDevice(-609918, SmartHomeDeviceType.SWITCH, "Kitchen balcony", Rooms.LOUNGE)
object Wc: SmartHomeDevice(-609928, SmartHomeDeviceType.SWITCH, "WC", Rooms.LOUNGE)
object GuestExFan: SmartHomeDevice(-609930, SmartHomeDeviceType.SWITCH, "Guest ExFan", Rooms.LOUNGE, DeviceKind.FAN)
object BedroomBathroom: SmartHomeDevice(-609939, SmartHomeDeviceType.SWITCH, "Bedroom Bathroom", Rooms.LOUNGE)
object BathroomMirror: SmartHomeDevice(-609931, SmartHomeDeviceType.SWITCH, "Bathroom Mirror", Rooms.LOUNGE)
object Corridor: SmartHomeDevice(-609938, SmartHomeDeviceType.SWITCH, "Corridor", Rooms.LOUNGE)
object Dinning: SmartHomeDevice(-609904, SmartHomeDeviceType.ANALOG_WITHOUT_DIMMING, "Dinning", Rooms.LOUNGE)
object LoungeHidden: SmartHomeDevice(-609901, SmartHomeDeviceType.ANALOG, "Hidden", Rooms.LOUNGE)
object Entrance: SmartHomeDevice(-609925, SmartHomeDeviceType.ANALOG, "Entrance", Rooms.LOUNGE)
object Sitting: SmartHomeDevice(-609922, SmartHomeDeviceType.ANALOG, "Sitting", Rooms.LOUNGE)


val devices = arrayListOf(
    WardrobeSpot,
    DeskSpot,
    Hidden,
    Bathroom,
    Kitchen,
    ExFan,
    Undercubort,
    Balcony1,
    Balcony2,
    KitchenBalcony2,
    Wc,
    GuestExFan,
    BedroomBathroom,
    BathroomMirror,
    Corridor,
    Dinning,
    LoungeHidden,
    Entrance,
    Sitting,
    Shutter,
)
