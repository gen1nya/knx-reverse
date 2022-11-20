package api.handlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import data.UiCtrlRequest
import flats
import splitQuery


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
            knxServer101.communicator.send(
                UiCtrlRequest(
                    action = "onAnalog",
                    id = id,
                    arg1 = value,
                    arg2 = 0
                )
            )
            knxServer101.httpExchanges[knxServer101.communicator.txId.get()] = t
        } ?: t.let { exchange ->
            exchange.sendResponseHeaders(400, 0L)
            exchange.responseBody.close()
        }
    }
}
