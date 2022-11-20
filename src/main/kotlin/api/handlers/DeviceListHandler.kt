package api.handlers

import com.google.gson.Gson
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import domain.devices
import java.io.OutputStream

class DeviceListHandler: HttpHandler {
    override fun handle(exchange: HttpExchange) {
        val response =  Gson().toJson(devices)
        exchange.sendResponseHeaders(200, response.length.toLong())
        val os: OutputStream = exchange.responseBody
        os.write(response.toByteArray())
        os.close()
    }

}