package dev.apollointhehouse.net.packet

import dev.apollointhehouse.data.nbt.tags.CompoundTag
import dev.apollointhehouse.net.packet.Packet.Companion.readCompressedCompoundTag
import dev.apollointhehouse.net.packet.Packet.Companion.writeCompressedCompoundTag
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
		override val packetID = 140

        override suspend fun create(channel: ByteReadChannel): PacketTileEntityData {
            val tag = channel.readCompressedCompoundTag()

            return PacketTileEntityData(tag = tag)
        }
    }
}
