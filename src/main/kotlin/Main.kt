import api.Server
import api.handlers.*
import com.sun.net.httpserver.HttpServer
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

var flats = hashMapOf<String, Server>()

fun main(args: Array<String>) {

    /*flats["201"] = api.Server("192.168.1.206").apply {
        output.subscribe({ response ->
            println("response 201: $response")
        }, {
            it.printStackTrace()
        })
    }*/
    flats["101"] = Server("192.168.1.206").apply {
        output.subscribe({ response ->
            println("response 101: $response")
        }, {
            it.printStackTrace()
        })
    }

    HttpServer.create(InetSocketAddress(8000), 0).apply {
        createContext("/device/switch", SwitchRequestHandler())
        createContext("/device/toggle", ToggleRequestHandler())
        createContext("/device/analog", AnalogRequestHandler())
        createContext("/server/domain.getDevices/", DeviceListHandler())
        createContext("/v2/server/domain.getDevices/", V2DeviceListHandler())
        createContext("/device/shatterButton", ShatterButtonHandler())
        executor = null
        start()
    }

    //createSocketServer()
}

/*
var socketAwaiting = false

fun createSocketServer() {

    // TODO add multiple flats support
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
}*/
