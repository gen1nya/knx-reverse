package api.handlers

import com.google.gson.Gson
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import api.model.Room
import domain.Rooms
import domain.devices
import java.io.OutputStream


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