package dev.apollointhehouse.network.packet.handshake

import dev.apollointhehouse.network.extensions.readJavaStringUTF8
import dev.apollointhehouse.network.extensions.writeJavaStringUTF8
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*

data class PacketHandshake(
    val username: String = "",
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeJavaStringUTF8(username)
    }

    override val estimatedSize: Int
        get() = 4 + username.length + 4

    companion object : StreamingPacketFactory<PacketHandshake> {
        override suspend fun create(channel: ByteReadChannel): PacketHandshake {
            val username = channel.readJavaStringUTF8(16)

            return PacketHandshake(username = username)
        }
    }
}
