package data

import com.google.gson.Gson
import com.jcraft.jsch.ChannelDirectTCPIP
import java.io.OutputStream
import java.util.HashMap
import java.util.concurrent.atomic.AtomicInteger

class Communicator(
    private val directTcpChannel: ChannelDirectTCPIP
) {

    val txId = AtomicInteger(0)

    fun send(hashMap: HashMap<String, Any>): String {
        hashMap["trans"] = txId.incrementAndGet().toString()
        val json = Gson().toJson(hashMap)
        return send(json)
    }

    fun send(request: BaseRequest): String {
        request.transId = txId.incrementAndGet().toString()
        val json = Gson().toJson(request)
        return send(json)
    }

    private fun send(request: String): String {
        val outputStream: OutputStream = directTcpChannel.outputStream

        outputStream.write(request.toByteArray())
        outputStream.write(10)
        outputStream.flush()

        return "" // TODO parse it
        val output = ByteArray(1000)
        directTcpChannel.inputStream.read(output)
        return output.toString(Charsets.UTF_8).dropLastWhile { it != '\n' }
    }
}
