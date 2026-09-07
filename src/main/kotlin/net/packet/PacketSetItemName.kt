package dev.apollointhehouse.net.packet

import dev.apollointhehouse.utils.extensions.readJavaStringUTF8
import dev.apollointhehouse.utils.extensions.writeJavaStringUTF8
import io.ktor.utils.io.*

class PacketSetItemName(
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
