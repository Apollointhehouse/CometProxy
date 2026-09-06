package dev.apollointhehouse.net.packet

import dev.apollointhehouse.net.packet.Packet.Companion.readJavaStringUTF8
import dev.apollointhehouse.net.packet.Packet.Companion.writeJavaStringUTF8
import io.ktor.utils.io.*

class PacketHandshake(
    val username: String = "",
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeJavaStringUTF8(username)
    }

    override val estimatedSize: Int
        get() = 4 + username.length + 4

    companion object : PacketFactory<PacketHandshake> {
		override val packetID = 2
        override suspend fun create(channel: ByteReadChannel): PacketHandshake {
            val username = channel.readJavaStringUTF8(16)

            return PacketHandshake(username = username)
        }
    }
}
