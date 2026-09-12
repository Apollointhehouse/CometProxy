package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.nbt.tags.CompoundTag
import dev.apollointhehouse.network.extensions.readCompressedCompoundTag
import dev.apollointhehouse.network.extensions.writeCompressedCompoundTag
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import kotlinx.io.Sink

data class PacketSetHeldObject(
    val entityID: Int = 0,
    val objectTag: CompoundTag? = null,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeInt(entityID)
        if (objectTag != null) {
            sink.writeByte(1)
            sink.writeCompressedCompoundTag(objectTag)
        } else {
            sink.writeByte(0)
        }
    }

    override val estimatedSize: Int
        get() = 0

    companion object : StreamingPacketFactory<PacketSetHeldObject> {
        override suspend fun create(channel: ByteReadChannel): PacketSetHeldObject {
            val entityID = channel.readInt()
            var objectTag: CompoundTag? = null
            if (channel.readByte().toInt() == 1) {
                objectTag = channel.readCompressedCompoundTag()
            }

            return PacketSetHeldObject(entityID = entityID, objectTag = objectTag)
        }
    }
}