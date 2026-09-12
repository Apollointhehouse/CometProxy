package dev.apollointhehouse.network.packet.handshake

import dev.apollointhehouse.network.extensions.readJavaStringUTF16BE
import dev.apollointhehouse.network.extensions.writeJavaStringUTF16BE
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import kotlinx.io.Sink
import java.nio.charset.StandardCharsets

data class PacketPingHandshake(
    val payload: UByte = 0u,
    val identifier: UByte = 0u,
    val pingHostString: String = "",
    val protocolVersion: UByte = 0u,
    val hostname: String = "",
    val port: Int = 0,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeByte(payload.toByte())
        sink.writeByte(identifier.toByte())
        sink.writeJavaStringUTF16BE(pingHostString)
        sink.writeShort((3 + StandardCharsets.UTF_16BE.encode(pingHostString).array().size + 4).toShort())
        sink.writeByte(protocolVersion.toByte())
        sink.writeJavaStringUTF16BE(hostname)
        sink.writeInt(port)
    }

    override val estimatedSize: Int = 0

    companion object : StreamingPacketFactory<PacketPingHandshake> {
        override suspend fun create(channel: ByteReadChannel): PacketPingHandshake {
            val payload = channel.readByte().toUByte()
            val identifier = channel.readByte().toUByte()
            val pingHostString = channel.readJavaStringUTF16BE(255)
            channel.readShort().toUByte()
            val protocolVersion = channel.readByte().toUByte()
            val hostname = channel.readJavaStringUTF16BE(255)
            val port = channel.readInt()

            return PacketPingHandshake(
                payload = payload,
                identifier = identifier,
                pingHostString = pingHostString,
                protocolVersion = protocolVersion,
                hostname = hostname,
                port = port
            )
        }
    }
}