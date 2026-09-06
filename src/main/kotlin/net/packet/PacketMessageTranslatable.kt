package dev.apollointhehouse.net.packet

import io.ktor.utils.io.*
import java.nio.charset.StandardCharsets
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.math.min

class PacketMessageTranslatable(
    val key: Short = 0,
    val type: Byte = 0,
    val args: Array<String> = arrayOf(),
    val format: Short = -1,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeShort(key)
        channel.writeByte(type or (if (format.toInt() != -1) -128 else 0))
        if (format.toInt() != -1) {
            channel.writeShort(format)
        }

        val argc = min(args.size, 4).toByte()
        channel.writeByte(argc)
        val args: Array<ByteArray> = Array(argc.toInt()) { ByteArray(0) }

        for (i in 0..<argc) {
            args[i] = this.args[i].toByteArray(StandardCharsets.UTF_8)
        }

        for (arg in args) {
            val len = arg.size.coerceAtMost(255).toByte()
            channel.writeByte(len)
        }

        for (arg in args) {
            val len = arg.size.coerceAtMost(255)
            channel.writeFully(arg, 0, len)
        }
    }

    override val estimatedSize: Int
        get() = 4 + this.args.size + this.args.size * 16

    companion object : PacketFactory<PacketMessageTranslatable> {
		override val packetID = 70
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