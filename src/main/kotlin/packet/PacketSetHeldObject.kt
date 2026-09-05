package dev.apollointhehouse.packet

import dev.apollointhehouse.data.nbt.tags.CompoundTag
import dev.apollointhehouse.packet.Packet.Companion.readCompressedCompoundTag
import dev.apollointhehouse.packet.Packet.Companion.writeCompressedCompoundTag
import io.ktor.utils.io.*

class PacketSetHeldObject(
    val entityID: Int = 0,
    val objectTag: CompoundTag? = null,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(entityID)
        if (objectTag != null) {
            channel.writeByte(1)
            channel.writeCompressedCompoundTag(objectTag)
        } else {
            channel.writeByte(0)
        }
    }

    override val estimatedSize: Int
        get() = 0

    companion object : PacketFactory<PacketSetHeldObject> {
		override val packetID = 27
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