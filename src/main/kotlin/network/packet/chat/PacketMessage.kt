package dev.apollointhehouse.network.packet.chat

import dev.apollointhehouse.network.extensions.readBoolean
import dev.apollointhehouse.network.extensions.readJavaStringUTF8
import dev.apollointhehouse.network.extensions.writeBoolean
import dev.apollointhehouse.network.extensions.writeJavaStringUTF8
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import kotlin.experimental.or

data class PacketMessage(
    val format: Short = 0,
    var message: String = "",
    val type: Byte = 0,
    val encrypted: Boolean = false,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeByte(type or (if (format.toInt() != 0) (-128).toByte() else 0.toByte()))
        channel.writeBoolean(encrypted)
        if (format.toInt() != 0) {
            channel.writeShort(format)
        }

        channel.writeJavaStringUTF8(message)
    }

    override val estimatedSize: Int
        get() = message.length + 1

    companion object : StreamingPacketFactory<PacketMessage> {
        override suspend fun create(channel: ByteReadChannel): PacketMessage {
            var type = channel.readByte()
            val formatted = (type.toInt() and -128) != 0
            type = (type.toInt() and 127).toByte()
            val encrypted = channel.readBoolean()
            val format = if (formatted) channel.readShort() else 0
            val message = channel.readJavaStringUTF8(1024)

            return PacketMessage(format = format, message = message, type = type, encrypted = encrypted)
        }
    }
}