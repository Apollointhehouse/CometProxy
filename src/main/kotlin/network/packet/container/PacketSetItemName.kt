package dev.apollointhehouse.network.packet.container

import dev.apollointhehouse.network.extensions.readJavaStringUTF8
import dev.apollointhehouse.network.extensions.writeJavaStringUTF8
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.PacketFactory
import io.ktor.utils.io.*

data class PacketSetItemName(
    val slot: Int = 0,
    val name: String = "",
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(slot)
        channel.writeJavaStringUTF8(name)
    }

    override val estimatedSize: Int
        get() = 2

    companion object : PacketFactory<PacketSetItemName> {
		override suspend fun create(channel: ByteReadChannel): PacketSetItemName {
            val slot = channel.readInt()
            val name = channel.readJavaStringUTF8(20)

            return PacketSetItemName(slot = slot, name = name)
        }
    }
}
