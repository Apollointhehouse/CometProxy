package dev.apollointhehouse.network.packet.world

import dev.apollointhehouse.network.extensions.readBoolean
import dev.apollointhehouse.network.extensions.writeBoolean
import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import io.ktor.utils.io.*
import kotlinx.io.Source

data class PacketChunkVisibility(
    val chunkX: Int = 0,
    val chunkZ: Int = 0,
    val playerAdded: Boolean = false,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(chunkX)
        channel.writeInt(chunkZ)
        channel.writeBoolean(playerAdded)
    }

    override val estimatedSize: Int
        get() = size

    companion object : BufferedPacketFactory<PacketChunkVisibility> {
        override val size: Int = 9

        override fun create(buffer: Source): PacketChunkVisibility {
            val chunkX = buffer.readInt()
            val chunkZ = buffer.readInt()
            val playerAdded = buffer.readBoolean()

            return PacketChunkVisibility(chunkX = chunkX, chunkZ = chunkZ, playerAdded = playerAdded)
        }
    }
}
