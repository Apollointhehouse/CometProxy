package dev.apollointhehouse.net.packet

import dev.apollointhehouse.data.nbt.tags.CompoundTag
import dev.apollointhehouse.utils.extensions.readCompressedCompoundTag
import dev.apollointhehouse.utils.extensions.writeCompressedCompoundTag
import io.ktor.utils.io.*

data class PacketEntityTagData(
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
		override suspend fun create(channel: ByteReadChannel): PacketEntityTagData {
            val entityId = channel.readInt()
            val tag = channel.readCompressedCompoundTag()

            return PacketEntityTagData(entityId = entityId, tag = tag)
        }
    }
}
