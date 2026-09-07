package dev.apollointhehouse.net.packet

import dev.apollointhehouse.utils.extensions.readBoolean
import dev.apollointhehouse.utils.extensions.writeBoolean
import io.ktor.utils.io.*

class PacketChunkVisibility(
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
        get() = 9

    companion object : PacketFactory<PacketChunkVisibility> {
		override suspend fun create(channel: ByteReadChannel): PacketChunkVisibility {
            val chunkX = channel.readInt()
            val chunkZ = channel.readInt()
            val playerAdded = channel.readBoolean()

            return PacketChunkVisibility(chunkX = chunkX, chunkZ = chunkZ, playerAdded = playerAdded)
        }
    }
}
