package dev.apollointhehouse.network.packet.entity

import dev.apollointhehouse.model.EntityDataItem
import dev.apollointhehouse.model.SyncedEntityData
import dev.apollointhehouse.network.extensions.readJavaStringUTF16BE
import dev.apollointhehouse.network.extensions.writeJavaStringUTF16BE
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import kotlinx.io.Sink

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
    override fun write(sink: Sink) {
        sink.writeInt(id)
        sink.writeShort(type)
        sink.writeInt(x)
        sink.writeInt(y)
        sink.writeInt(z)
        sink.writeByte(yaw)
        sink.writeByte(pitch)
        SyncedEntityData.pack(unpackedData, sink)
        sink.writeJavaStringUTF16BE(nickname)
        sink.writeByte(chatColor)
    }

    override val estimatedSize: Int
        get() = 21

    companion object : StreamingPacketFactory<PacketAddMob> {
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

            return PacketAddMob(
                id = id,
                type = type,
                x = x,
                y = y,
                z = z,
                yaw = yaw,
                pitch = pitch,
                nickname = nickname,
                chatColor = chatColor,
                unpackedData = unpackedData
            )
        }
    }
}