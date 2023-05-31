package api.handlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import data.ShatterButtonAction
import data.UiCtrlRequest
import flats
import splitQuery

class ShatterButtonHandler : HttpHandler {

    override fun handle(t: HttpExchange) {
        val args = t.requestURI.splitQuery()

        val id = args["id"]?.toIntOrNull() ?: -1
        val type = (args["type"] ?: "release").capitalize()
        val button = (args["button"] ?: "off").capitalize()

        val shatterButtonAction = ShatterButtonAction(button, type)

        val flat: String = args["flat"] ?: t.let { exchange ->
            exchange.sendResponseHeaders(400, 0L)
            exchange.responseBody.close()
            return
        }

        flats[flat]?.let { knxServer101 ->
            knxServer101.communicator.send(
                UiCtrlRequest(
                    action = shatterButtonAction.toString(),
                    id = id,
                    arg1 = 1, // autoUpdate = true
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
