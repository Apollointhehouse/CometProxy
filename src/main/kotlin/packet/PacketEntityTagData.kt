package dev.apollointhehouse.packet

import dev.apollointhehouse.data.nbt.tags.CompoundTag
import dev.apollointhehouse.packet.Packet.Companion.readCompressedCompoundTag
import dev.apollointhehouse.packet.Packet.Companion.writeCompressedCompoundTag
import io.ktor.utils.io.*

class PacketEntityTagData(
    val entityId: Int = 0,
    val tag: CompoundTag? = null,
) : Packet {
    

    override suspend fun write(channel: ByteWriteChannel) {
      channel.writeInt(entityId)
        channel.writeCompressedCompoundTag(tag!!)
    }

    override val estimatedSize: Int
        get() = 0

    companion object : PacketFactory<PacketEntityTagData> {
		override val packetID = 42
        override suspend fun create(channel: ByteReadChannel): PacketEntityTagData {
            val entityId = channel.readInt()
            val tag = channel.readCompressedCompoundTag()

            return PacketEntityTagData(entityId = entityId, tag = tag)
        }
    }
}
