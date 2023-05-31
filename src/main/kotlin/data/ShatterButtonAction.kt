package data

class ShatterButtonAction (
    buttonName: String,
    typeName: String
) {

    private val button = Button.byName(buttonName)
    private val type = Type.byName(typeName)

    override fun toString(): String = "on${type.name}${button.name}"

    sealed class Button(val name: String) {
        object On: Button("On")
        object Off: Button("Off")

        companion object {
            fun byName(name: String): Button {
                return when (name) {
                    On.name -> On
                    Off.name -> Off
                    else -> throw IllegalStateException()
                }
            }

        }

    }

    sealed class Type(val name: String) {
        object Hold: Type("Hold")
        object Release: Type("Release")

        companion object {
            fun byName(name: String): Type {
                return when (name) {
                    Hold.name -> Hold
                    Release.name -> Release
                    else -> throw IllegalStateException()
                }
            }

        }

    }

}