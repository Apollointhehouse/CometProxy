package dev.apollointhehouse.network.packet.entity

import dev.apollointhehouse.model.EntityDataItem
import dev.apollointhehouse.model.SyncedEntityData
import dev.apollointhehouse.nbt.tags.CompoundTag
import dev.apollointhehouse.network.extensions.readCompressedCompoundTag
import dev.apollointhehouse.network.extensions.writeCompressedCompoundTag
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import kotlinx.io.Sink
import kotlinx.io.writeFloat

data class PacketAddEntity(
    val entityId: Int = 0,
    val xPosition: Int = 0,
    val yPosition: Int = 0,
    val zPosition: Int = 0,
    val pitch: Float = 0f,
    val yaw: Float = 0f,
    val type: Short = 0,
    val unpackedData: List<EntityDataItem<*>> = listOf(),
    val hasVelocity: Boolean = false,
    val xVelocity: Short = 0,
    val yVelocity: Short = 0,
    val zVelocity: Short = 0,
    val ownerId: Int = 0,
    val metaData: Int = 0,
    val tag: CompoundTag? = null
) : Packet {
    override fun write(sink: Sink) {
        sink.writeInt(entityId)
        sink.writeShort(type)
        sink.writeInt(xPosition)
        sink.writeInt(yPosition)
        sink.writeInt(zPosition)
        sink.writeFloat(pitch)
        sink.writeFloat(yaw)
        SyncedEntityData.pack(unpackedData, sink)
        val optionals = makeOptionalsByte(this.hasVelocity, this.ownerId >= 0, this.metaData >= 0, this.tag != null)
        sink.writeByte(optionals)
        if (hasVelocity(optionals)) {
            sink.writeShort(xVelocity)
            sink.writeShort(yVelocity)
            sink.writeShort(zVelocity)
        }

        if (hasOwner(optionals)) {
            sink.writeInt(ownerId)
        }

        if (hasMeta(optionals)) {
            sink.writeInt(metaData)
        }

        if (hasTag(optionals)) {
            sink.writeCompressedCompoundTag(tag!!)
        }
    }

    override val estimatedSize: Int
        get() = if (21 + ownerId <= 0) 0 else 6

    companion object : StreamingPacketFactory<PacketAddEntity> {
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
