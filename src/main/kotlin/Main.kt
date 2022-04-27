import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.jcraft.jsch.ChannelDirectTCPIP
import com.jcraft.jsch.JSch
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.IOException
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import kotlin.concurrent.thread


object UserInfo : com.jcraft.jsch.UserInfo {
    private const val pass = Keys.pass
    val privatekey = Keys.privatekey
    val pubkey = Keys.pubkey

    override fun getPassphrase(): String = pass
    override fun getPassword(): String = pass
    override fun promptPassword(message: String?): Boolean = true
    override fun promptPassphrase(message: String?): Boolean = true
    override fun promptYesNo(message: String?): Boolean = false
    override fun showMessage(message: String?) {
        println(message)
    }
}
/*
val ids = arrayListOf<Int>(
    -611281, -609724, -609725, -609726, -609727, -609728, -609729, -609730, -609731, -609735, -609732, -609733, -609734, -609736, -609791, -609792, -609793, -609794, -609796,
    -609848, -609849, -609863, -609864
)

val loundgeIds = arrayListOf<Int>(
    -611304, -609749, -609750, -609751, -609752, -609753, -609842, -609843, -609901, -609902,
    -609903, -609904, -609905, -609906, -609907, -609908, -609917, -609918, -609922, -609923,
    -609924, -609925, -609926, -609927, -609928, -609930, -609931, -609936, -609937, -609938,
    -609939, -611305,
)

val onezerotwoids = arrayListOf<Int>(
    -611281, -609724, -609725, -609726, -609727, -609728, -609729, -609735, -609732, -609733, -609734,
    -609736, -609791, -609794, -609796, -609848, -609849, -609863, -609864, -611977, -611978, -611979
)*/


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

lateinit var communicator: Communicator
lateinit var directTcpChannel: ChannelDirectTCPIP
var socketServer: ServerSocket? =null
var socket: Socket? = null

val switchDevicesState: HashMap<Int, Boolean> = hashMapOf()

val httpExchanges = hashMapOf<Int, HttpExchange?>()

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

    /* val channel = schSession.openChannel("shell")
    channel.inputStream = System.`in`
    channel.outputStream = System.out
    channel.connect(3 * 1000) */

    directTcpChannel = schSession.openChannel("direct-tcpip") as ChannelDirectTCPIP
    val uuid = "aaf1b883-a0da-4ed4-8791-60d73e4a39d7"

    //directTcpChannel.inputStream = System.`in`
    directTcpChannel.outputStream = System.`out`

    directTcpChannel.setPort(directTcpIpPort)
    directTcpChannel.setHost(ip)
    directTcpChannel.connect(1000)

    communicator = Communicator(directTcpChannel)

    val output = io.reactivex.rxjava3.core.Observable.create<String> { emitter ->
        val inputStream = directTcpChannel.inputStream

        while (true) {
            Thread.sleep(1)
            if(inputStream.available() > 0) {
                val length = inputStream.available()
                val bytes = ByteArray(length)
                inputStream.read(bytes, 0, length)
                val responses = bytes.toString(Charsets.UTF_8)
                    .replace(">>", "")
                    .replace(" ", "")
                    .replace(";\n",",")
                    .replace(";",",")
                for (line in responses.split("\n").filter { it.isNotBlank() && it.isNotEmpty() }) {
                    val response = line.replace("\n", "")
                    emitter.onNext(response)
                    try {
                        if (socket?.isConnected == true) {
                            socket?.getOutputStream()?.write((response + '\n').toByteArray())
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        createSocketServer()
                    }

                    if (response.startsWith("{result")) {
                        val actionResponse = Gson().fromJson(response, ActionResponse::class.java)
                        val txId = (actionResponse.trans?.toIntOrNull() ?: -1)
                        httpExchanges[txId]?.let { httpExchange ->
                            val resp = Gson().toJson(actionResponse, ActionResponse::class.java)
                            httpExchange.sendResponseHeaders(200, resp.length.toLong())
                            val os: OutputStream = httpExchange.responseBody
                            os.write(resp.toByteArray())
                            os.close()
                        }
                        httpExchanges[txId] = null

                    }
                    if(response.startsWith("{data:{data")) {
                        val eventResponse = Gson().fromJson(response, EventResponse::class.java)
                        if (eventResponse.event == EventResponse.UI_STATE_EVENT || eventResponse.event == EventResponse.UI_ANALOG_EVENT) {
                            devices.find { it.id == eventResponse.data.id }
                                ?.let {
                                    it.value = eventResponse.data.data?.toDoubleOrNull() ?: 0.0
                                }
                        }
                    }
                }

            }
        }
    }
        .subscribeOn(Schedulers.io())


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


    for (id in devices.map { it.id }) {
        val hashMap = HashMap<String, Any>()
        hashMap["cmd"] = "uievt"
        hashMap["action"] = "add"
        hashMap["id"] = id
        communicator.send(hashMap)
    }

    /*for (id in ids) {
        val hashMap = HashMap<String, Any>()
        hashMap["cmd"] = "uievt"
        hashMap["action"] = "add"
        hashMap["id"] = id
        communicator.send(hashMap)
    }*/


    output.subscribe({ response ->

        println("response: $response")
    }, {
        it.printStackTrace()
    })

    val server = HttpServer.create(InetSocketAddress(8000), 0)
    server.createContext("/device/switch", SwitchRequestHandler())
    server.createContext("/device/toggle", ToggleRequestHandler())
    server.createContext("/device/analog", AnalogRequestHandler())
    server.createContext("/server/devices/", DeviceListHandler())
    server.createContext("/v2/server/devices/", V2DeviceListHandler())
    server.executor = null

    server.start()

    createSocketServer()
}

var socketAwaiting = false

fun createSocketServer() {
    if (socketAwaiting) return
    thread {
        try {
            socketAwaiting = true
            socket?.close()
            socketServer?.close()
            socketServer = null
            socket = null
            socketServer = ServerSocket(8082)
            socket = socketServer?.accept()
            socketAwaiting = false
        } catch (e: Exception) {
            e.printStackTrace()
            socketAwaiting = false
        }
    }
}

class DeviceListHandler: HttpHandler {
    override fun handle(exchange: HttpExchange) {
        val response =  Gson().toJson(devices)
        exchange.sendResponseHeaders(200, response.length.toLong())
        val os: OutputStream = exchange.responseBody
        os.write(response.toByteArray())
        os.close()
    }

}

class V2DeviceListHandler: HttpHandler {
    override fun handle(exchange: HttpExchange) {
        val response = Gson().toJson(Rooms.values().map { room ->
            Room(
                name = room.name.replace("_", " ").toLowerCase().capitalize(),
                devices =  devices.filter { it.page == room }
            )
        })
        exchange.sendResponseHeaders(200, response.length.toLong())
        val os: OutputStream = exchange.responseBody
        os.write(response.toByteArray())
        os.close()
    }

}

class AnalogRequestHandler: HttpHandler {
    override fun handle(t: HttpExchange) {
        val args = t.requestURI.splitQuery()

        val id = args["id"]?.toIntOrNull() ?: -1
        val value = args["value"]?.toDoubleOrNull() ?: 0.0

        communicator.send(UiCtrlRequest(
            action = "onAnalog",
            id = id,
            arg1 = value,
            arg2 = 0
        ))
        httpExchanges[communicator.txId.get()] = t
    }
}

class SwitchRequestHandler : HttpHandler {
    @Throws(IOException::class)
    override fun handle(t: HttpExchange) {
        val args = t.requestURI.splitQuery()

        val id = args["id"]?.toIntOrNull() ?: -1
        val state = args["enable"].toBoolean()

        communicator.send(UiCtrlRequest(
            action = "onClick",
            id = id,
            arg1 = if (state) 0 else 1,
            arg2 = 0
        ))
        httpExchanges[communicator.txId.get()] = t
    }
}

class ToggleRequestHandler : HttpHandler {
    override fun handle(t: HttpExchange) {
        val args = t.requestURI.splitQuery()

        val id = args["id"]?.toIntOrNull() ?: -1
        switchDevicesState[id] = !(switchDevicesState[id]?: true)
        communicator.send(UiCtrlRequest(
            action = "onClick",
            id = id,
            arg1 = if (switchDevicesState[id] == true) 0 else 1,
            arg2 = 0
        ))
        httpExchanges[communicator.txId.get()] = t

    }
}


open class BaseRequest(
    @SerializedName("cmd")
    val command: String = ""
) {
    @SerializedName("trans")
    var transId: String = "0"

}

class UiCtrlRequest(
    @SerializedName("action")
    val action: String = "onClick",
    @SerializedName("id")
    val id: Int,
    @SerializedName("arg1")
    val arg1: Any,
    @SerializedName("arg2")
    val arg2: Int,
): BaseRequest(command = "uictrl")


