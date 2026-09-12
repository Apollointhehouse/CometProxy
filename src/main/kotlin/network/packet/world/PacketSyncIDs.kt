package dev.apollointhehouse.network.packet.world

import dev.apollointhehouse.network.extensions.readJavaStringUTF8
import dev.apollointhehouse.network.extensions.writeJavaStringUTF8
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import kotlin.experimental.and

data class PacketSyncIDs(
    val destinationId: Byte = -1,
    val mapping: MutableMap<Short, String> = mutableMapOf(),
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeByte(destinationId)
        channel.writeInt(mapping.size)

        for ((key, value) in this.mapping) {
            channel.writeShort(key)
            channel.writeJavaStringUTF8(value)
        }
    }

    override val estimatedSize: Int
        get() = 4 + 1

    companion object : StreamingPacketFactory<PacketSyncIDs> {
		override suspend fun create(channel: ByteReadChannel): PacketSyncIDs {
            val destinationId = channel.readByte() and 255.toByte()
            val count = channel.readInt()
            val mapping: MutableMap<Short, String> = mutableMapOf()

            repeat(count) {
                val id = channel.readShort() and '\uffff'.code.toShort()
                val name = channel.readJavaStringUTF8(128)
                mapping[id] = name
            }

            return PacketSyncIDs(destinationId = destinationId, mapping = mapping)
        }
    }
}