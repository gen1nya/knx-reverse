import com.jcraft.jsch.ChannelDirectTCPIP
import com.jcraft.jsch.JSch
import data.Communicator
import java.util.*

fun main(args: Array<String>) {
    val ip = "192.168.1.205"
    val port = 22
    val directTcpIpPort = 7000

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

    val directTcpChannel = schSession.openChannel("direct-tcpip") as ChannelDirectTCPIP
    val uuid = "aaf1b883-a0da-4ed4-8791-60d73e4a39d7"

    directTcpChannel.outputStream = System.`out`

    directTcpChannel.setPort(directTcpIpPort)
    directTcpChannel.setHost(ip)
    directTcpChannel.connect(1000)

    val communicator = Communicator(directTcpChannel)

    val hashMapLogin = HashMap<String, Any>()
    hashMapLogin["cmd"] = "ulog"
    hashMapLogin["action"] = "login"
    hashMapLogin["uid"] = uuid
    hashMapLogin["name"] = "user"
    println(communicator.send(hashMapLogin))

    val evenHashMap = HashMap<String, Any>()
    evenHashMap["cmd"] = "ctrl"
    evenHashMap["action"] = "event"
    evenHashMap["param"] = "on"
    println(communicator.send(evenHashMap))

    val lsldHashMap = HashMap<String, Any>()
    lsldHashMap["cmd"] = "lsld"
    communicator.send(lsldHashMap)

    for (id in -620000..-600000) {
        val hashMap = HashMap<String, Any>()
        hashMap["cmd"] = "uievt"
        hashMap["action"] = "add"
        hashMap["id"] = id
        communicator.send(hashMap)
    }

}