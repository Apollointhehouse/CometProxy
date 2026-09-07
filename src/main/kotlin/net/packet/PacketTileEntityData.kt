package dev.apollointhehouse.net.packet

import dev.apollointhehouse.data.nbt.tags.CompoundTag
import dev.apollointhehouse.utils.extensions.readCompressedCompoundTag
import dev.apollointhehouse.utils.extensions.writeCompressedCompoundTag
import io.ktor.utils.io.*

class PacketTileEntityData(
    val tag: CompoundTag? = null,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        val tag = tag
        if (tag != null) channel.writeCompressedCompoundTag(tag)
    }

    override val estimatedSize: Int
        get() = 0

    companion object : PacketFactory<PacketTileEntityData> {
		
        override suspend fun create(channel: ByteReadChannel): PacketTileEntityData {
            val tag = channel.readCompressedCompoundTag()

            return PacketTileEntityData(tag = tag)
        }
    }
}
