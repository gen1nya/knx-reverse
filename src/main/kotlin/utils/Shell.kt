import com.jcraft.jsch.JSch
import java.util.*


fun main(args: Array<String>) {
    val ip = "46.199.87.66"
    val port = 65205

    val jsch = JSch()
    val properties = Properties().apply {
        put("StrictHostKeyChecking", "no")
    }

    jsch.addIdentity("tpctrl", UserInfo.privatekey, UserInfo.pubkey, null)
    val schSession = jsch.getSession("root", ip, port)
        .apply {
            setConfig(properties)
            setPassword(UserInfo.password)
            userInfo = UserInfo
            connect()
        }

    val channel = schSession.openChannel("shell")
    channel.inputStream = System.`in`
    channel.outputStream = System.out
    channel.connect(3 * 1000)
}