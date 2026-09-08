package dev.apollointhehouse.net.packet

import dev.apollointhehouse.data.SyncedEntityData
import dev.apollointhehouse.data.nbt.tags.CompoundTag
import dev.apollointhehouse.utils.extensions.readCompressedCompoundTag
import dev.apollointhehouse.utils.extensions.writeCompressedCompoundTag
import io.ktor.utils.io.*

data class PacketAddEntity(
    val entityId: Int = 0,
    val xPosition: Int = 0,
    val yPosition: Int = 0,
    val zPosition: Int = 0,
    val pitch: Float = 0f,
    val yaw: Float = 0f,
    val type: Short = 0,
    val unpackedData: List<SyncedEntityData.DataItem<*>> = listOf(),
    val hasVelocity: Boolean = false,
    val xVelocity: Short = 0,
    val yVelocity: Short = 0,
    val zVelocity: Short = 0,
    val ownerId: Int = 0,
    val metaData: Int = 0,
    val tag: CompoundTag? = null
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(entityId)
        channel.writeShort(type)
        channel.writeInt(xPosition)
        channel.writeInt(yPosition)
        channel.writeInt(zPosition)
        channel.writeFloat(pitch)
        channel.writeFloat(yaw)
        SyncedEntityData.pack(unpackedData, channel)
        val optionals = makeOptionalsByte(this.hasVelocity, this.ownerId >= 0, this.metaData >= 0, this.tag != null)
        channel.writeByte(optionals)
        if (hasVelocity(optionals)) {
           channel.writeShort(xVelocity)
           channel.writeShort(yVelocity)
           channel.writeShort(zVelocity)
        }

        if (hasOwner(optionals)) {
           channel.writeInt(ownerId)
        }

        if (hasMeta(optionals)) {
           channel.writeInt(metaData)
        }

        if (hasTag(optionals)) {
            channel.writeCompressedCompoundTag(tag!!)
        }
    }

    override val estimatedSize: Int
        get() = if (21 + ownerId <= 0) 0 else 6

    companion object : PacketFactory<PacketAddEntity> {
		        fun hasOwner(value: Byte): Boolean {
            return (value.toInt() and 1) != 0
        }

        fun hasMeta(value: Byte): Boolean {
            return (value.toInt() and 2) != 0
        }

        fun hasVelocity(value: Byte): Boolean {
            return (value.toInt() and 4) != 0
        }

        fun hasTag(value: Byte): Boolean {
            return (value.toInt() and 8) != 0
        }

        fun makeOptionalsByte(hasVelocity: Boolean, hasOwner: Boolean, hasMeta: Boolean, hasCompound: Boolean): Byte {
            var value: Byte = 0
            if (hasOwner) {
                value = (0.toInt() or 1).toByte()
            }

            if (hasMeta) {
                value = (value.toInt() or 2).toByte()
            }

            if (hasVelocity) {
                value = (value.toInt() or 4).toByte()
            }

            if (hasCompound) {
                value = (value.toInt() or 8).toByte()
            }

            return value
        }

        override suspend fun create(channel: ByteReadChannel): PacketAddEntity {
            val entityId = channel.readInt()
            val type = channel.readShort()
            val xPosition = channel.readInt()
            val yPosition = channel.readInt()
            val zPosition = channel.readInt()
            val pitch = channel.readFloat()
            val yaw = channel.readFloat()
            val unpackedData = SyncedEntityData.unpack(channel)
            val optionals = channel.readByte()

            var xVelocity: Short = 0
            var yVelocity: Short = 0
            var zVelocity: Short = 0
            var hasVelocity = false

            if (hasVelocity(optionals)) {
                xVelocity = channel.readShort()
                yVelocity = channel.readShort()
                zVelocity = channel.readShort()
                hasVelocity = true
            }

            var ownerId = 0
            if (hasOwner(optionals)) {
                ownerId = channel.readInt()
            }

            var metaData = 0
            if (this.hasMeta(optionals)) {
                metaData = channel.readInt()
            }

            var tag: CompoundTag? = null
            if (this.hasTag(optionals)) {
                tag = channel.readCompressedCompoundTag()
            }

            return PacketAddEntity(
                                entityId,
                xPosition,
                yPosition,
                zPosition,
                pitch,
                yaw,
                type,
                unpackedData,
                hasVelocity,
                xVelocity,
                yVelocity,
                zVelocity,
                ownerId,
                metaData,
                tag
            )
        }

    }
}
