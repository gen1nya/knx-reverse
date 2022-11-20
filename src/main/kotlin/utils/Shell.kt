import com.jcraft.jsch.JSch
import java.util.*


fun main(args: Array<String>) {
    val ip = "192.168.1.205"
    val port = 22

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