package dev.apollointhehouse.network.packet.entity

import dev.apollointhehouse.nbt.tags.CompoundTag
import dev.apollointhehouse.network.extensions.readCompressedCompoundTag
import dev.apollointhehouse.network.extensions.writeCompressedCompoundTag
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import kotlinx.io.Sink

data class PacketTileEntityData(
    val tag: CompoundTag? = null,
) : Packet {
    override fun write(sink: Sink) {
        val tag = tag
        if (tag != null) sink.writeCompressedCompoundTag(tag)
    }

    override val estimatedSize: Int
        get() = 0

    companion object : StreamingPacketFactory<PacketTileEntityData> {

        override suspend fun create(channel: ByteReadChannel): PacketTileEntityData {
            val tag = channel.readCompressedCompoundTag()

            return PacketTileEntityData(tag = tag)
        }
    }
}
