import com.google.gson.Gson
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import java.io.IOException
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
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


var flats = hashMapOf<String, Server>()

fun main(args: Array<String>) {

    flats["201"] = Server("192.168.1.206").apply {
        output.subscribe({ response ->
            println("response 201: $response")
        }, {
            it.printStackTrace()
        })
    }
    flats["101"] = Server("192.168.1.205").apply {
        output.subscribe({ response ->
            println("response 101: $response")
        }, {
            it.printStackTrace()
        })
    }

    val server = HttpServer.create(InetSocketAddress(8000), 0)
    server.createContext("/device/switch", SwitchRequestHandler())
    server.createContext("/device/toggle", ToggleRequestHandler())
    //server.createContext("/device/toggle", ToggleRequestHandler())
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
            flats["101"]?.let {  knxServer101 ->
                knxServer101.socket?.close()
                knxServer101.socketServer?.close()
                knxServer101.socketServer = null
                knxServer101.socket = null
                knxServer101.socketServer = ServerSocket(8082)
                knxServer101.socket = knxServer101.socketServer?.accept()
            }
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
        val flat: String = args["flat"] ?: t.let { exchange ->
            exchange.sendResponseHeaders(400, 0L)
            exchange.responseBody.close()
            return
        }

        flats[flat]?.let { knxServer101 ->
            knxServer101.communicator.send(UiCtrlRequest(
                action = "onAnalog",
                id = id,
                arg1 = value,
                arg2 = 0
            ))
            knxServer101.httpExchanges[knxServer101.communicator.txId.get()] = t
        } ?: t.let { exchange ->
            exchange.sendResponseHeaders(400, 0L)
            exchange.responseBody.close()
        }
    }
}

class SwitchRequestHandler : HttpHandler {
    @Throws(IOException::class)
    override fun handle(t: HttpExchange) {
        val args = t.requestURI.splitQuery()

        val id = args["id"]?.toIntOrNull() ?: -1
        val state = args["enable"].toBoolean()
        val flat: String = args["flat"] ?: t.let { exchange ->
            exchange.sendResponseHeaders(400, 0L)
            exchange.responseBody.close()
            return
        }

        flats[flat]?.let { knxServer101 ->
            knxServer101.communicator.send(UiCtrlRequest(
                action = "onClick",
                id = id,
                arg1 = if (state) 0 else 1,
                arg2 = 0
            ))
            knxServer101.httpExchanges[knxServer101.communicator.txId.get()] = t
        } ?: t.let { exchange ->
            exchange.sendResponseHeaders(400, 0L)
            exchange.responseBody.close()
        }

    }
}

class ToggleRequestHandler : HttpHandler {
    override fun handle(t: HttpExchange) {
        val args = t.requestURI.splitQuery()

        val id = args["id"]?.toIntOrNull() ?: -1
        val flat: String = args["flat"] ?: t.let { exchange ->
            exchange.sendResponseHeaders(400, 0L)
            exchange.responseBody.close()
            return
        }

        flats[flat]?.let { knxServer ->
            knxServer.switchDevicesState[id] = !(knxServer.switchDevicesState[id]?: true)
            knxServer.communicator.send(UiCtrlRequest(
                action = "onClick",
                id = id,
                arg1 = if (knxServer.switchDevicesState[id] == true) 0 else 1,
                arg2 = 0
            ))
            knxServer.httpExchanges[knxServer.communicator.txId.get()] = t
        } ?: t.let { exchange ->
            exchange.sendResponseHeaders(400, 0L)
            exchange.responseBody.close()
        }
    }
}
