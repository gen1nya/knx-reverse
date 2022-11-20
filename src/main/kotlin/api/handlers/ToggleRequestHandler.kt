package api.handlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import data.UiCtrlRequest
import flats
import splitQuery


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
            knxServer.communicator.send(
                UiCtrlRequest(
                    action = "onClick",
                    id = id,
                    arg1 = if (knxServer.switchDevicesState[id] == true) 0 else 1,
                    arg2 = 0
                )
            )
            knxServer.httpExchanges[knxServer.communicator.txId.get()] = t
        } ?: t.let { exchange ->
            exchange.sendResponseHeaders(400, 0L)
            exchange.responseBody.close()
        }
    }
}