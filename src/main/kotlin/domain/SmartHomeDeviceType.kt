package domain

enum class SmartHomeDeviceType(val id: Int) {
    SWITCH(0), ANALOG(1), ANALOG_WITHOUT_DIMMING(2)
}