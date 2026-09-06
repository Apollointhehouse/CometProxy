package dev.apollointhehouse.net.packet

import dev.apollointhehouse.net.packet.Packet.Companion.readJavaStringUTF8
import dev.apollointhehouse.net.packet.Packet.Companion.writeJavaStringUTF8
import io.ktor.utils.io.*

class PacketCommandManager(
    val suggestions: String
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeJavaStringUTF8(suggestions)
    }

    override val estimatedSize: Int
        get() = 1

    companion object : PacketFactory<PacketCommandManager> {
		override val packetID = 120
        override suspend fun create(channel: ByteReadChannel): PacketCommandManager = PacketCommandManager(channel.readJavaStringUTF8(65532))
    }
}
