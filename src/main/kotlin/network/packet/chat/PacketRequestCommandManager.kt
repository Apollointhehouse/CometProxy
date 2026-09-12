package dev.apollointhehouse.network.packet.chat

import dev.apollointhehouse.network.extensions.readJavaStringUTF8
import dev.apollointhehouse.network.extensions.writeJavaStringUTF8
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*

data class PacketRequestCommandManager(
    val username: String = "",
    val text: String = "",
    val cursor: Int = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeJavaStringUTF8(username)
        channel.writeJavaStringUTF8(text)
        channel.writeInt(cursor)
    }

    override val estimatedSize: Int
        get() = 10

    companion object : StreamingPacketFactory<PacketRequestCommandManager> {
		override suspend fun create(channel: ByteReadChannel): PacketRequestCommandManager {
            val username = channel.readJavaStringUTF8(20)
            val text = channel.readJavaStringUTF8(256)
            val cursor = channel.readInt()

            return PacketRequestCommandManager(username = username, text = text, cursor = cursor)
        }
    }
}
