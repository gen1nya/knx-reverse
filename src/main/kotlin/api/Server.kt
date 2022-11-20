package api

import UserInfo
import com.google.gson.Gson
import com.jcraft.jsch.ChannelDirectTCPIP
import com.jcraft.jsch.JSch
import com.sun.net.httpserver.HttpExchange
import api.model.ActionResponse
import data.Communicator
import api.model.EventResponse
import domain.devices
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.OutputStream
import java.util.*

class Server (
    private val url: String
) {

    var communicator: Communicator
    var directTcpChannel: ChannelDirectTCPIP
    //var socketServer: ServerSocket? = null
    //var socket: Socket? = null

    val switchDevicesState: HashMap<Int, Boolean> = hashMapOf()

    val httpExchanges = hashMapOf<Int, HttpExchange?>()

    init {
        val ip = url
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

        directTcpChannel = schSession.openChannel("direct-tcpip") as ChannelDirectTCPIP
        val uuid = "aaf1b883-a0da-4ed4-8791-60d73e4a39d7"

        directTcpChannel.outputStream = System.`out`

        directTcpChannel.setPort(directTcpIpPort)
        directTcpChannel.setHost(ip)
        directTcpChannel.connect(1000)

        communicator = Communicator(directTcpChannel)

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

    }

    val output: Observable<String> = Observable.create<String> { emitter ->
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
                    .replace(",\n",",")
                    .replace(";",",")
                for (line in responses.split("\n").filter { it.isNotBlank() && it.isNotEmpty() }) {
                    val response = line.replace("\n", "")
                    emitter.onNext(response)
                    /*try {
                        if (socket?.isConnected == true) {
                            socket?.getOutputStream()?.write((response + '\n').toByteArray())
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        createSocketServer()
                    }*/

                    if (response.startsWith("{result") || response.startsWith("{\"result\"")) {
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
                    if(response.startsWith("{data:{data") || response.startsWith("{\"data\":{\"data\"")) {
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

}