import com.jcraft.jsch.JSch
import java.util.*

private object ServerUserInfo : com.jcraft.jsch.UserInfo {
    private const val pass = "4i4kovite4ervenotikveni4kov4eta"
    val privatekey = "-----BEGIN RSA PRIVATE KEY-----\r\nProc-Type: 4,ENCRYPTED\r\nDEK-Info: DES-EDE3-CBC,77E7B94911496BC1\r\n\r\nhXeeXQKpKYry5V8PFqOIb4Rn77Lt5zZYgCg1dPrkbAY12nYG1lpUgZnAqndNQzV3\r\ntJRDBOuDe/fRY26SX9IDsNRbnMrxuagIjwUK/I6KUdOudtc/qZURcq0Rk6ApHr8c\r\nBlLdhmuVycCMVY58DNRhypJ8qYjoVKV9u6xaaBaxJUsvv8wWAHW4dILPDf9JcPUU\r\ngT+rjLcf6X2sGETEqR9qEKF/MR6jLzvB7FfXEQiaHkCsiKpMFkOvIXtWVod3csPv\r\nSf9SVZkeLEj3xn7ek5NKuLc4W+92WTBIRBKaOo3esI2CMz2+wYAg/HlqxseXNacq\r\njtMlfq8PzrNwaybNcMvnTU+bBYuo8XRW9aEFc1YZgSzu0vMB/oyO+P8iuioyIDXB\r\nSTJBlFPWgcfJhT9g940ATyLUz3kIBkNA2XeUF2k44R07A+nx95ZYZJPUUx378UW9\r\n8E6XCOlJdMimQoj7qgRy15G9ZUQ3kMluOWXEy/+vQ1UD9vvJGx3BBinMJmNA31x0\r\nIYmmF9bh+LnC6AG6YDGQqrRMzuoHFE/GdY6vw9kSsgFo6l9YwekGFe6dVQXpIsQF\r\ngjS5UyT+MS/5RLLZIy+eCGmYzABnHzJ1RP/SIjdss46j2Im5ZgXzr58hehNR9a4L\r\nUmAkRgaYQpEmaYjbYOL3aOSrNXTFLWdO9DMW25r2ZJwurP4Cte5FzAV/OLrGZPiW\r\n3gVbULUKom6RCe6z+osPCiTrXm3yEpDk+vcnTRLlzIWUd2wmcYhVjDKZ75eF/P/9\r\nt/N3yyLt2HBPVNgE63sbzdU6aF6hjtvcsGMRF4DRHJvUvuMxI/OZQVlr5tiRRaJ2\r\np2ZHstg0sU6F6uZ52sIo0sQXTzZeTuXCxYpsqx99wNCjjQiDr4fZWdpqjR5TkyOE\r\n3J4cDCShQ2IEdU9ojdeiBiBxkq1r055n5mZ8eIdJLFO2bdBIW4e5JvvW3fuKUr0G\r\nhWk19SmXx6rxXLApEMfXw9WJKOXK0KDc/BCkQn5viB/TA/zKfTod3Fp78x1prxzb\r\n7+eMwv/q66wRIGbck9qlNieHDhtudn3n0GkXChSCCWMA+z2WMTSBG5RuUm8itTSu\r\nTfpHLFZDIKsyaDKLhILGd5OXs5RNdlTtanSazNq1WqDglfdpr2HGXygmKOk5Ccsp\r\nmu20lgXiutEIn2NKijIKbDnb+GUumPvgWzPDsH67zfwbEmszQaawpCY6kQLHnO+U\r\nia1qBRCwCLJdevSv2b4lZXlN6t1vGLLESJzy7TXcgj5J3JbhMr3Tdyq1mcvdF2uk\r\n1aRyQnNe08XnnhdwvfhDD3Mw/phMSBLh2vC4oxXBn4gn8749dK4KZkPXicjcD6GI\r\n1XV5fSS6wExwZ1KpFnWhssWjaJGYezmycIDJIZ4uxrdiO9Ev9+q6kHv8o82RPBkZ\r\nw6ldxG7ZAchK4p5QBnawGPCGyV/qhKL6smlb/jU4zjHGO5NnOavVAiW1GvSo5v8J\r\n34A4ZX9wyBtDpq4bD8H3R5AYkSR5HjIwmPUQpJOg3u55swPcm2Gd7GnztRn6Qyee\r\nlGfiX8M2UlRN0qeMGm1cHiiR1x/tzZjUY2MQZP63l1/Gmz1lKdPOHg==\r\n-----END RSA PRIVATE KEY-----\r\n".toByteArray()
    val pubkey = "ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEA0gV1Jw1WSDz//JkKRKG6Mf01WHz+GxlGh5kzqiXqs9wb0vZJJYdGm2yosZkI7PTsxjifntjjpshxUt0HmU0nd0PmZ2qNc/VHcB2sEkmf2YfOKx8CBl3kwdKtXB2yQ4OclhPg4bimnVDunRtYIiVC/jGxyku7M8/N3zMPRcty/l5YGynNG1YEfnid6tmtX1DE7iqtmjV2TDlz1V98P3O1MSlfkt/8z6AzsPm+X59kA6LOnWHy924BESOwx1jgZwtFntKJClSCnC4q4aW5aGfimlH7VkWFxDCwjnCW+tQvz4fE2bhZmx3i0V5PUKQrlMa3lcJWXJACM9duHdHnr01sRQ== root@work".toByteArray()

    override fun getPassphrase(): String = pass
    override fun getPassword(): String = pass
    override fun promptPassword(message: String?): Boolean = true
    override fun promptPassphrase(message: String?): Boolean = true
    override fun promptYesNo(message: String?): Boolean = false
    override fun showMessage(message: String?) {
        println(message)
    }
}

fun main(args: Array<String>) {
    val ip = "192.168.1.205"
    val port = 22

    val jsch = JSch()
    val properties = Properties().apply {
        put("StrictHostKeyChecking", "no")
    }

    jsch.addIdentity("tpctrl", ServerUserInfo.privatekey, ServerUserInfo.pubkey, null)
    val schSession = jsch.getSession("root", ip, port)
        .apply {
            setConfig(properties)
            setPassword(ServerUserInfo.password)
            userInfo = ServerUserInfo
            connect()
        }

    val channel = schSession.openChannel("shell")
    channel.inputStream = System.`in`
    channel.outputStream = System.out
    channel.connect(3 * 1000)
}