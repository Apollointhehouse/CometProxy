package dev.apollointhehouse.network.packet.chat

import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import io.ktor.utils.io.core.writeFully
import kotlinx.io.Sink
import java.nio.charset.StandardCharsets
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.math.min
import kotlin.text.String
import kotlin.text.toByteArray

data class PacketMessageTranslatable(
    val key: Short = 0,
    val type: Byte = 0,
    val args: Array<String> = arrayOf(),
    val format: Short = -1,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeShort(key)
        sink.writeByte(type or (if (format.toInt() != -1) -128 else 0))
        if (format.toInt() != -1) {
            sink.writeShort(format)
        }

        val argc = min(args.size, 4).toByte()
        sink.writeByte(argc)
        val args: Array<ByteArray> = Array(argc.toInt()) { ByteArray(0) }

        for (i in 0..<argc) {
            args[i] = this.args[i].toByteArray(StandardCharsets.UTF_8)
        }

        for (arg in args) {
            val len = arg.size.coerceAtMost(255).toByte()
            sink.writeByte(len)
        }

        for (arg in args) {
            val len = arg.size.coerceAtMost(255)
            sink.writeFully(arg, 0, len)
        }
    }

    override val estimatedSize: Int
        get() = 4 + this.args.size + this.args.size * 16

    companion object : StreamingPacketFactory<PacketMessageTranslatable> {
        override suspend fun create(channel: ByteReadChannel): PacketMessageTranslatable {
            val key = channel.readShort()
            var type = channel.readByte()
            val formatted = (type and -128).toInt() != 0
            type = (type and 127)
            val format = if (formatted) channel.readShort() else 0
            val read = channel.readByte().toUByte().toInt()
            val argc = read.coerceAtMost(4)
            val rawArgs: Array<ByteArray> = Array(argc) { ByteArray(0) }

            for (i in 0..<argc) {
                val len = channel.readByte().toUByte().toInt()
                rawArgs[i] = ByteArray(len)
            }

            for (i in 0..<argc) {
                channel.readFully(rawArgs[i])
            }

            val args = Array(argc) { "" }

            for (i in 0..<argc) {
                args[i] = String(rawArgs[i], StandardCharsets.UTF_8)
            }

            return PacketMessageTranslatable(key = key, type = type, args = args, format = format)
        }
    }
}