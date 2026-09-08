package dev.apollointhehouse.net.packet

import dev.apollointhehouse.data.nbt.tags.CompoundTag
import dev.apollointhehouse.utils.extensions.readCompressedCompoundTag
import dev.apollointhehouse.utils.extensions.writeCompressedCompoundTag
import io.ktor.utils.io.*

data class PacketGameRule(
    val tag: CompoundTag = CompoundTag(null),
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeCompressedCompoundTag(tag)
    }

    override val estimatedSize: Int
        get() = 0

    companion object : PacketFactory<PacketGameRule> {
		override suspend fun create(channel: ByteReadChannel): PacketGameRule {
            val tag = channel.readCompressedCompoundTag()!!

            return PacketGameRule(tag = tag)
        }
    }
}