package api.handlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import data.UiCtrlRequest
import flats
import splitQuery
import java.io.IOException


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
            knxServer101.communicator.send(
                UiCtrlRequest(
                    action = "onClick",
                    id = id,
                    arg1 = if (state) 0 else 1,
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
