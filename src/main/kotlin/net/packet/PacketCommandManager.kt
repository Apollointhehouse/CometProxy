package dev.apollointhehouse.net.packet

import dev.apollointhehouse.utils.extensions.readJavaStringUTF8
import dev.apollointhehouse.utils.extensions.writeJavaStringUTF8
import io.ktor.utils.io.*

data class PacketCommandManager(
    val suggestions: String
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeJavaStringUTF8(suggestions)
    }

    override val estimatedSize: Int
        get() = 1

    companion object : PacketFactory<PacketCommandManager> {
		override suspend fun create(channel: ByteReadChannel): PacketCommandManager = PacketCommandManager(channel.readJavaStringUTF8(65532))
    }
}
