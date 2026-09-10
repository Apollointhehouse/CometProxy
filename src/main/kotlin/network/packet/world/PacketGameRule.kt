package dev.apollointhehouse.network.packet.world

import dev.apollointhehouse.nbt.tags.CompoundTag
import dev.apollointhehouse.network.extensions.readCompressedCompoundTag
import dev.apollointhehouse.network.extensions.writeCompressedCompoundTag
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.PacketFactory
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