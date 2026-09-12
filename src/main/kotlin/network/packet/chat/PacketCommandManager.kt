package dev.apollointhehouse.network.packet.chat

import dev.apollointhehouse.network.extensions.readJavaStringUTF8
import dev.apollointhehouse.network.extensions.writeJavaStringUTF8
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*

data class PacketCommandManager(
    val suggestions: String
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeJavaStringUTF8(suggestions)
    }

    override val estimatedSize: Int
        get() = 1

    companion object : StreamingPacketFactory<PacketCommandManager> {
		override suspend fun create(channel: ByteReadChannel): PacketCommandManager = PacketCommandManager(channel.readJavaStringUTF8(65532))
    }
}
