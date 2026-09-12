package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.network.extensions.readJavaStringUTF16BE
import dev.apollointhehouse.network.extensions.writeJavaStringUTF16BE
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*

data class PacketPlayerGamemode(
    val gamemodeId: String = "",
    val entityId: Int = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(entityId)
        channel.writeJavaStringUTF16BE(gamemodeId)
    }

    override val estimatedSize: Int
        get() = 5

    companion object : StreamingPacketFactory<PacketPlayerGamemode> {
        override suspend fun create(channel: ByteReadChannel): PacketPlayerGamemode {
            val entityId = channel.readInt()
            val gamemodeId = channel.readJavaStringUTF16BE(256)

            return PacketPlayerGamemode(gamemodeId = gamemodeId, entityId = entityId)
        }
    }
}
