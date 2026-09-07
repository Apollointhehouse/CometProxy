package dev.apollointhehouse.net.packet

import dev.apollointhehouse.utils.extensions.readJavaStringUTF16BE
import dev.apollointhehouse.utils.extensions.writeJavaStringUTF16BE
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
			    override suspend fun create(channel: ByteReadChannel): PacketPlayerGamemode {
            val entityId = channel.readInt()
            val gamemodeId = channel.readJavaStringUTF16BE(256)

            return PacketPlayerGamemode(gamemodeId = gamemodeId, entityId = entityId)
        }
    }
}
