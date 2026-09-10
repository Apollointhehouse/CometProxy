package dev.apollointhehouse.network.packet.entity

import dev.apollointhehouse.model.EntityDataItem
import dev.apollointhehouse.model.SyncedEntityData
import dev.apollointhehouse.network.extensions.readJavaStringUTF16BE
import dev.apollointhehouse.network.extensions.writeJavaStringUTF16BE
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.PacketFactory
import io.ktor.utils.io.*

data class PacketAddMob(
    val id: Int = 0,
    val type: Short = 0,
    val x: Int = 0,
    val y: Int = 0,
    val z: Int = 0,
    val yaw: Byte = 0,
    val pitch: Byte = 0,
    val nickname: String = "",
    val chatColor: Byte = 0,
    val unpackedData: List<EntityDataItem<*>>? = listOf(),
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(id)
        channel.writeShort(type)
        channel.writeInt(x)
        channel.writeInt(y)
        channel.writeInt(z)
        channel.writeByte(yaw)
        channel.writeByte(pitch)
        SyncedEntityData.pack(unpackedData, channel)
        channel.writeJavaStringUTF16BE(nickname)
        channel.writeByte(chatColor)
    }

    override val estimatedSize: Int
        get() = 21

    companion object : PacketFactory<PacketAddMob> {
		override suspend fun create(channel: ByteReadChannel): PacketAddMob {
            val id = channel.readInt()
            val type = channel.readShort()
            val x = channel.readInt()
            val y = channel.readInt()
            val z = channel.readInt()
            val yaw = channel.readByte()
            val pitch = channel.readByte()
            val unpackedData = SyncedEntityData.unpack(channel)
            val nickname = channel.readJavaStringUTF16BE(256)
            val chatColor = channel.readByte()

            return PacketAddMob(id = id, type = type, x = x, y = y, z = z, yaw = yaw, pitch = pitch, nickname = nickname, chatColor = chatColor, unpackedData = unpackedData)
        }
    }
}