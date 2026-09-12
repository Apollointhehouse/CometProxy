package dev.apollointhehouse.network.packet.container

import dev.apollointhehouse.network.extensions.readJavaStringUTF8
import dev.apollointhehouse.network.extensions.writeJavaStringUTF8
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import kotlinx.io.Sink

data class PacketSetItemName(
    val slot: Int = 0,
    val name: String = "",
) : Packet {
    override fun write(sink: Sink) {
        sink.writeInt(slot)
        sink.writeJavaStringUTF8(name)
    }

    override val estimatedSize: Int
        get() = 2

    companion object : StreamingPacketFactory<PacketSetItemName> {
        override suspend fun create(channel: ByteReadChannel): PacketSetItemName {
            val slot = channel.readInt()
            val name = channel.readJavaStringUTF8(20)

            return PacketSetItemName(slot = slot, name = name)
        }
    }
}
