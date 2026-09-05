package dev.apollointhehouse.packet

import dev.apollointhehouse.packet.Packet.Companion.readJavaStringUTF16BE
import dev.apollointhehouse.packet.Packet.Companion.writeJavaStringUTF16BE
import io.ktor.utils.io.*

class PacketPlayerGamemode(
    val gamemodeId: String = "",
    val entityId: Int = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(entityId)
        channel.writeJavaStringUTF16BE(gamemodeId)
    }

    override val estimatedSize: Int
        get() = 5

    companion object : PacketFactory<PacketPlayerGamemode> {
		override val packetID = 41
        override suspend fun create(channel: ByteReadChannel): PacketPlayerGamemode {
            val entityId = channel.readInt()
            val gamemodeId = channel.readJavaStringUTF16BE(256)

            return PacketPlayerGamemode(gamemodeId = gamemodeId, entityId = entityId)
        }
    }
}
