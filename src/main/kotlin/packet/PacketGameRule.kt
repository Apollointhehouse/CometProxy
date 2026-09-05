package dev.apollointhehouse.packet

import dev.apollointhehouse.data.nbt.tags.CompoundTag
import dev.apollointhehouse.packet.Packet.Companion.readCompressedCompoundTag
import dev.apollointhehouse.packet.Packet.Companion.writeCompressedCompoundTag
import io.ktor.utils.io.*

class PacketGameRule(
    val tag: CompoundTag = CompoundTag(null),
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeCompressedCompoundTag(tag)
    }

    override val estimatedSize: Int
        get() = 0

    companion object : PacketFactory<PacketGameRule> {
		override val packetID = 74
        override suspend fun create(channel: ByteReadChannel): PacketGameRule {
            val tag = channel.readCompressedCompoundTag()!!

            return PacketGameRule(tag = tag)
        }
    }
}