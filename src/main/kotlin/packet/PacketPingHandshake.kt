package dev.apollointhehouse.packet

import dev.apollointhehouse.packet.Packet.Companion.readJavaStringUTF16BE
import dev.apollointhehouse.packet.Packet.Companion.writeJavaStringUTF16BE
import io.ktor.utils.io.*
import java.nio.charset.StandardCharsets

class PacketPingHandshake(
    val payload: UByte = 0u,
    val identifier: UByte = 0u,
    val pingHostString: String = "",
    val protocolVersion: UByte = 0u,
    val hostname: String = "",
    val port: Int = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeByte(payload.toByte())
        channel.writeByte(identifier.toByte())
        channel.writeJavaStringUTF16BE(pingHostString)
        channel.writeShort((3 + StandardCharsets.UTF_16BE.encode(pingHostString).array().size + 4).toShort())
        channel.writeByte(protocolVersion.toByte())
        channel.writeJavaStringUTF16BE(hostname)
        channel.writeInt(port)
    }

    override val estimatedSize: Int = 0

    companion object : PacketFactory<PacketPingHandshake> {
		override val packetID = 254
        override suspend fun create(channel: ByteReadChannel): PacketPingHandshake {
            val payload = channel.readByte().toUByte()
            val identifier = channel.readByte().toUByte()
            val pingHostString = channel.readJavaStringUTF16BE(255)
            channel.readShort().toUByte()
            val protocolVersion = channel.readByte().toUByte()
            val hostname = channel.readJavaStringUTF16BE(255)
            val port = channel.readInt()

            return PacketPingHandshake(                payload = payload,
                identifier = identifier,
                pingHostString = pingHostString,
                protocolVersion = protocolVersion,
                hostname = hostname,
                port = port
            )
        }
    }
}